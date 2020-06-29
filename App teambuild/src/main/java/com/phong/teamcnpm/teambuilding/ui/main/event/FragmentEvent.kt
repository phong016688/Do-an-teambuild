package com.phong.teamcnpm.teambuilding.ui.main.event

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.phong.teamcnpm.teambuilding.R
import com.phong.teamcnpm.teambuilding.data.source.local.preference.SharedPrefsApi
import com.phong.teamcnpm.teambuilding.domain.entities.Event
import com.phong.teamcnpm.teambuilding.domain.entities.User
import com.phong.teamcnpm.teambuilding.ui.group.EventAdapter
import com.phong.teamcnpm.teambuilding.ui.group.GroupDetailActivity
import com.phong.teamcnpm.teambuilding.ui.group.ItemEventListener
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_event.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf


class FragmentEvent : Fragment(), EventView, ItemEventListener {
  private val mCompositeDisposable = CompositeDisposable()
  private val mPresenter: EventPresenter by inject { parametersOf(this) }
  private val mSharedPrefsApi: SharedPrefsApi by inject()
  private lateinit var mAdapter: EventAdapter
  private var mGroupId: String = ""

  override val disposables: CompositeDisposable
    get() = mCompositeDisposable
  override val onItemClickListener: (eventId: String) -> Unit
    get() = { eventId ->
      activity?.also {
        if (mGroupId.isNotEmpty())
          startActivity(GroupDetailActivity.getInstance(it, mGroupId, eventId))
      }
    }
  override val onActionEventClickListener: (actionName: String, eventId: String) -> Unit
    get() = { _, _ -> Unit }
  override val onStateClickListener: (state: String) -> Unit
    get() = { Unit }
  override val onButtonAddEventClickListener: () -> Unit
    get() = { Unit }
  override val onCommentClickListener: (eventId: String) -> Unit
    get() = { Unit }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_event, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    mPresenter.observerEventsFromLocale()
    Log.d("####", "create ${this::class.java.simpleName}")
    initView()
  }

  private fun initView() {
    mAdapter = EventAdapter(this, this, mSharedPrefsApi)
    eventRecyclerView.layoutManager = LinearLayoutManager(context)
    eventRecyclerView.adapter = mAdapter
    eventRecyclerView.hasFixedSize()
    mAdapter.submitList(ArrayList())
  }

  override fun getAllEventsSuccess(events: List<Event>) {
    mAdapter.submitList(events.toMutableList())
    mGroupId = events.getOrNull(0)?.groupId ?: ""
  }

  override fun onGetError(throwable: Throwable) {
    Log.d("FragmentEvent", throwable.message.toString())
  }

  companion object {
    @JvmStatic
    fun newInstance() =
      FragmentEvent().apply {
        arguments = Bundle().apply {}
      }
  }
}