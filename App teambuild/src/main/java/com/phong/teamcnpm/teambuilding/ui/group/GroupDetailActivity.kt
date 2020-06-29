package com.phong.teamcnpm.teambuilding.ui.group

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.jakewharton.rxbinding4.recyclerview.scrollEvents
import com.jakewharton.rxbinding4.swiperefreshlayout.refreshes
import com.jakewharton.rxbinding4.view.clicks
import com.jakewharton.rxbinding4.view.scrollChangeEvents
import com.jakewharton.rxbinding4.widget.textChanges
import com.phong.teamcnpm.teambuilding.R
import com.phong.teamcnpm.teambuilding.data.source.local.preference.SharedPrefsApi
import com.phong.teamcnpm.teambuilding.domain.entities.Event
import com.phong.teamcnpm.teambuilding.domain.entities.Group
import com.phong.teamcnpm.teambuilding.domain.entities.User
import com.phong.teamcnpm.teambuilding.ui.BaseActivity
import com.phong.teamcnpm.teambuilding.ui.group.comment_event.FragmentComment
import com.phong.teamcnpm.teambuilding.ui.group.newevent.FragmentNewEvent
import com.phong.teamcnpm.teambuilding.ui.group.show_vote.FragmentShowVote
import com.phong.teamcnpm.teambuilding.ui.group.update_event.FragmentUpdateEvent
import com.phong.teamcnpm.teambuilding.utils.Constants
import com.phong.teamcnpm.teambuilding.utils.extensions.*
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_group_detail.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.util.*
import java.util.concurrent.TimeUnit


class GroupDetailActivity : BaseActivity(), GroupDetailView, ItemEventListener,
  FragmentNewEvent.NewEventListener {
  private val mPresenter: GroupDetailPresenter by inject { parametersOf(this) }
  private val mSharedPrefsApi: SharedPrefsApi by inject()
  private var mGroup: Group? = null
  private var mEventsInGroup: List<Event> = emptyList()
  private var mUsersInGroup: List<User> = emptyList()
  private lateinit var mAdapter: EventAdapter
  private var mSearchState: String = ""

  override val disposables: CompositeDisposable
    get() = compositeDisposable

  override val onItemClickListener: (eventId: String) -> Unit
    get() = { eventId ->
      if (eventId.isNotEmpty())
        mGroup?.let { group ->
          mEventsInGroup.firstOrNull { it.id == eventId }?.let { event ->
            FragmentUpdateEvent.newInstance(
              mUsersInGroup,
              group.id,
              event
            ).show(supportFragmentManager, FragmentUpdateEvent.TAG)
          }
        }
    }
  override val onActionEventClickListener: (actionName: String, eventId: String) -> Unit
    get() = { title, eventId ->
      if (eventId.isNotEmpty()) {
        when (title) {
          "LIKED" -> {
            mPresenter.modifyVote(eventId, "LIKE", mGroup?.id ?: "")
          }
          "DISLIKED" -> {
            mPresenter.modifyVote(eventId, "DISLIKE", mGroup?.id ?: "")
          }
          "NONE" -> mPresenter.modifyVote(eventId, "NONE", mGroup?.id ?: "")
          else -> {
            FragmentShowVote.newInstance(mUsersInGroup, eventId, title)
              .show(supportFragmentManager, "ShowVoteAdapter")
          }
        }
      }
    }
  override val onStateClickListener: (state: String) -> Unit
    get() = {
      mSearchState = it
      searchStateTextView.handleToStateEvent(it)
      val text = searchEventEditText.text
      sortedRecyclerView(if (text.isEmpty()) "*" else text.toString())
    }
  override val onCommentClickListener: (eventId: String) -> Unit
    get() = { eventId ->
      mEventsInGroup.firstOrNull { it.id == eventId }?.let {
        FragmentComment.newInstance(it, mUsersInGroup)
          .show(supportFragmentManager, FragmentComment.TAG)
      }
    }
  override val onButtonAddEventClickListener: () -> Unit
    get() = {
      mGroup?.let {
        FragmentNewEvent.newInstance(mUsersInGroup, it.id)
          .show(supportFragmentManager, FragmentNewEvent.TAG)
      }
    }
  override val onNewEventListener: () -> Unit
    get() = {
      mGroup?.let {
        mPresenter.getEventsInGroup(it.id)
      }
    }

  override fun mainContentView(): Int = R.layout.activity_group_detail

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    getData()
    onLoading(Unit)
    initView()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      handleEvent()
    }
  }

  private fun initView() {
    mAdapter = EventAdapter(this, this, mSharedPrefsApi)
    eventsRecyclerView.adapter = mAdapter
    eventsRecyclerView.hasFixedSize()
  }

  @RequiresApi(Build.VERSION_CODES.M)
  private fun handleEvent() {
    val toEventId = intent.getStringExtra(Constants.TO_EVENT_ID) ?: ""
    Observable.merge(detailGroupTextView.clicks().map { 1 }, eventsGroupTextView.clicks().map { 2 })
      .startWithItem(if (toEventId.isEmpty()) 1 else 2)
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe {
        when (it) {
          1 -> showDetailGroup()
          2 -> showEventGroup()
        }
      }.addTo(compositeDisposable)
    refreshLayout.refreshes()
      .subscribe {
        getData()
        onLoading(Unit)
      }.addTo(compositeDisposable)
    searchEventEditText.textChanges()
      .debounce(200L, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
      .subscribe { chars ->
        if (mAdapter.currentList.isEmpty()) return@subscribe
        sortedRecyclerView(chars)
      }.addTo(compositeDisposable)
    searchStateTextView.clicks()
      .subscribe {
        mSearchState = ""
        searchStateTextView.handleToStateEvent("")
        sortedRecyclerView(searchEventEditText.text.toString())
      }.addTo(compositeDisposable)
  }

  private fun getData() {
    val groupId = intent.getStringExtra(Constants.GROUP_ID)
    groupId?.let {
      mPresenter.getGroupById(it)
      mPresenter.getEventsInGroup(it)
      mPresenter.getUsersInGroup(it)
    }
  }

  private fun sortedRecyclerView(chars: CharSequence) {
    val newEvents = mAdapter.currentList.run {
      if (chars.isEmpty() && mSearchState.isEmpty())
        sortedBy { it.name }
      else
        sortedByDescending { getLevelSort(it.name, chars.toString(), it.state) }
    }
    mAdapter.submitList(newEvents.toMutableList())
    Handler().postDelayed({
      eventsRecyclerView.scrollToPosition(0)
    }, 200)
  }

  private fun getLevelSort(_string: String, _find: String, state: String): Int {
    val string = _string.toLowerCase(Locale.getDefault())
    val find = _find.toLowerCase(Locale.getDefault())
    var level = 0
    if (state.toLowerCase(Locale.getDefault()) == mSearchState.toLowerCase(Locale.getDefault()))
      level += 5000
    if (find.isEmpty()) return level
    for (i in find.indices) {
      if (string.contains(find[i]))
        level += 10 - string.indexOf(find[i])
      if (i > 0) if (string.contains("${find[i - 1]}${find[i]}"))
        level += 40 - string.indexOf(find[i])
      if (i > 1) if (string.contains("${find[i - 2]}${find[i - 1]}${find[i]}"))
        level += 80 - string.indexOf(find[i])
      if (i > 2) if (string.contains("${find[i - 3]}${find[i - 2]}${find[i - 1]}${find[i]}"))
        level += 120 - string.indexOf(find[i])
    }
    return level
  }

  override fun <T> onLoading(value: T) {
    loadingView.show()
  }

  private fun showEventGroup() {
    containerDetail.gone()
    eventGroupContainer.show()
    eventsGroupTextView.setTextColor(ContextCompat.getColor(this, R.color.red))
    detailGroupTextView.setTextColor(ContextCompat.getColor(this, R.color.black))
  }

  private fun showDetailGroup() {
    containerDetail.show()
    eventGroupContainer.gone()
    detailGroupTextView.setTextColor(ContextCompat.getColor(this, R.color.red))
    eventsGroupTextView.setTextColor(ContextCompat.getColor(this, R.color.black))
  }

  override fun onGetGroupSuccess(group: Group) {
    mGroup = group
    avatarImageView.loadFromUrl(group.avatar)
    nameGroupTextView.text = group.name
    descriptionTextView.text = group.description
  }

  override fun onGetError(throwable: Throwable) {
    loadingView.gone()
    Log.d(TAG, throwable.message.toString())
  }

  override fun onGetEventsByUserSuccess(events: List<Event>) {
    mEventsInGroup = events
    mAdapter.submitList(events.sortedBy { it.name }.toMutableList())
    loadingView.gone()
    refreshLayout.isRefreshing = false
  }

  override fun onGetUsersInGroupSuccess(users: List<User>) {
    mUsersInGroup = users
  }

  companion object {
    private val TAG = this::class.java.simpleName
    fun getInstance(context: Context, groupId: String, toEventId: String = ""): Intent =
      Intent(context, GroupDetailActivity::class.java).apply {
        putExtra(Constants.TO_EVENT_ID, toEventId)
        putExtra(Constants.GROUP_ID, groupId)
      }
  }
}