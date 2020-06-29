package com.phong.teamcnpm.teambuilding.ui.group.show_vote

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import com.phong.teamcnpm.teambuilding.R
import com.phong.teamcnpm.teambuilding.domain.entities.User
import com.phong.teamcnpm.teambuilding.domain.entities.Vote
import com.phong.teamcnpm.teambuilding.utils.Constants
import com.phong.teamcnpm.teambuilding.widgets.BaseBottomSheetFragment
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_show_vote.view.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.util.*

class FragmentShowVote : BaseBottomSheetFragment(), ShowVoteView {
  private val mPresenter: ShowVotePresenter by inject { parametersOf(this) }
  private val mCompositeDisposable = CompositeDisposable()
  private var mUsersInGroup: List<User> = emptyList()
  private lateinit var mView: View
  private lateinit var mAdapter: ShowVoteAdapter

  override fun isShowAnchorView(): Boolean = true

  override fun isSheetAlwaysExpanded(): Boolean = true

  override val disposables: CompositeDisposable
    get() = mCompositeDisposable

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    inflateView(R.layout.fragment_show_vote)
    mView = view
    super.onViewCreated(view, savedInstanceState)
    arguments?.getParcelableArrayList<User>(Constants.LIST_USER_IN_GROUP)?.toList()?.let {
      mUsersInGroup = it
      mAdapter = ShowVoteAdapter(mUsersInGroup)
    }
    arguments?.getString(Constants.TITLE_PAGE)?.let {
      mView.titleTextView.text = it
    }
    arguments?.getString(Constants.EVENT_ID)?.let { eventId ->
      mPresenter.getAllVoteInEvent(eventId)
    }
    initView()
  }

  private fun initView() {
    mView.voteRecyclerView.adapter = mAdapter
  }

  override fun onGetAllVoteSuccess(votes: List<Vote>) {
    mAdapter.submitList(votes.filter { it.type == mView.titleTextView.text })
  }

  override fun onDestroy() {
    mCompositeDisposable.clear()
    super.onDestroy()
  }

  override fun onGetError(throwable: Throwable) {
    Log.d(TAG, throwable.message.toString())
  }

  companion object {
    val TAG = this::class.java.simpleName
    fun newInstance(usersInGroup: List<User>, eventId: String, titlePAge: String) =
      FragmentShowVote().apply {
        arguments = bundleOf(
          Constants.LIST_USER_IN_GROUP to ArrayList(usersInGroup),
          Constants.TITLE_PAGE to titlePAge,
          Constants.EVENT_ID to eventId
        )
      }
  }
}