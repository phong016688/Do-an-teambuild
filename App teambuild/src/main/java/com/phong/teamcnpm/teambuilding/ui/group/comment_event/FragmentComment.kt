package com.phong.teamcnpm.teambuilding.ui.group.comment_event

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import com.jakewharton.rxbinding4.recyclerview.scrollEvents
import com.jakewharton.rxbinding4.view.clicks
import com.phong.teamcnpm.teambuilding.R
import com.phong.teamcnpm.teambuilding.data.source.local.preference.SharedPrefsApi
import com.phong.teamcnpm.teambuilding.data.source.local.preference.SharedPrefsKey
import com.phong.teamcnpm.teambuilding.domain.entities.Comment
import com.phong.teamcnpm.teambuilding.domain.entities.Event
import com.phong.teamcnpm.teambuilding.domain.entities.User
import com.phong.teamcnpm.teambuilding.utils.Constants
import com.phong.teamcnpm.teambuilding.utils.extensions.addTo
import com.phong.teamcnpm.teambuilding.widgets.BaseBottomSheetFragment
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_comment.view.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class FragmentComment : BaseBottomSheetFragment(), CommentView {
  private val mPresenter: CommentPresenter by inject { parametersOf(this) }
  private lateinit var mView: View
  private var mEvent: Event? = null
  private lateinit var mAdapter: CommentAdapter
  private val mCompositeDisposable = CompositeDisposable()
  private val mSharedPrefsApi: SharedPrefsApi by inject()

  override fun isSheetAlwaysExpanded(): Boolean = true

  override fun isShowAnchorView(): Boolean = true

  override val disposables: CompositeDisposable
    get() = mCompositeDisposable

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    inflateView(R.layout.fragment_comment)
    super.onViewCreated(view, savedInstanceState)
    mView = view
    mEvent = arguments?.getParcelable(Constants.EVENT) ?: return
    mEvent?.let { mPresenter.getCommentsForEvent(it.id) }
    initView()
    handleEvents()
  }

  private fun initView() {
    mAdapter = CommentAdapter(
      arguments?.getParcelableArrayList<User>(Constants.LIST_USER_IN_GROUP)?.toList() ?: emptyList()
    )
    mView.commentRecyclerView.adapter = mAdapter
    mView.commentRecyclerView.hasFixedSize()
    mAdapter.submitList(emptyList())
  }

  private fun handleEvents() {
    mView.sendImageButton.clicks()
      .debounce(200L, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
      .filter { mView.commentEditText.text.isNotEmpty() }
      .subscribe {
        mEvent?.let {
          mPresenter.newCommentInEvent(it.id, mView.commentEditText.text.toString())
        }
      }.addTo(mCompositeDisposable)
    mView.commentRecyclerView.scrollEvents()
      .subscribe {
        if (it.dy > 20) {
          this.lockDragDown()
        } else {
          this.openDragDown()
        }
      }.addTo(mCompositeDisposable)
  }

  override fun onGetCommentsForEventSuccess(comments: List<Comment>) {
    mAdapter.submitList(comments.sortedBy { it.createAt })
  }

  override fun onNewCommentInEventSuccess() {
    val oldContent = mView.commentEditText.text.toString()
    val currentUserId = mSharedPrefsApi.get(SharedPrefsKey.CURRENT_USER_ID, String::class.java)
    val comment = Comment(
      System.currentTimeMillis().toString(),
      currentUserId,
      mEvent?.id ?: "",
      oldContent,
      System.currentTimeMillis()
    )
    val newList = arrayListOf<Comment>(comment)
    newList.addAll(mAdapter.currentList)
    mAdapter.submitList(newList.sortedBy { it.createAt })
    mView.commentEditText.text.clear()
    Handler().postDelayed(
      { mView.commentRecyclerView.scrollToPosition(mAdapter.currentList.lastIndex) },
      200L
    )
  }

  override fun onGetError(throwable: Throwable) {
    Log.d(TAG, throwable.message.toString())
  }

  override fun onDestroy() {
    mCompositeDisposable.clear()
    super.onDestroy()
  }

  companion object {
    val TAG = this::class.java.simpleName
    fun newInstance(event: Event, usersInGroup: List<User>) = FragmentComment().apply {
      arguments = bundleOf(
        Constants.EVENT to event,
        Constants.LIST_USER_IN_GROUP to ArrayList(usersInGroup)
      )
    }
  }
}