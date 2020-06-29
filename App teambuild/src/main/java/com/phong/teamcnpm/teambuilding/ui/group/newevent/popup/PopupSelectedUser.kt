package com.phong.teamcnpm.teambuilding.ui.group.newevent.popup

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.rxbinding4.view.clicks
import com.phong.teamcnpm.teambuilding.R
import com.phong.teamcnpm.teambuilding.data.source.local.preference.SharedPrefsApi
import com.phong.teamcnpm.teambuilding.data.source.local.preference.SharedPrefsKey
import com.phong.teamcnpm.teambuilding.domain.entities.User
import com.phong.teamcnpm.teambuilding.domain.entities.UserState
import com.phong.teamcnpm.teambuilding.utils.Constants
import com.phong.teamcnpm.teambuilding.utils.extensions.addTo
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_popup_select_user.view.*
import org.koin.android.ext.android.inject
import java.util.ArrayList

class PopupSelectUserFragment : DialogFragment() {
  private val mSharedPreferences: SharedPrefsApi by inject()
  private lateinit var mView: View
  private lateinit var mSelectUserAdapter: SelectedUserAdapter
  private var mListUserInGroup: List<User> = emptyList()
  private var mListUserSelected: List<UserState> = emptyList()
  private val mCompositeDisposable = CompositeDisposable()
  private var mSelectedListener: SelectedUserListener? = null

  override fun onAttach(context: Context) {
    super.onAttach(context)
    if (parentFragment is SelectedUserListener) {
      mSelectedListener = parentFragment as SelectedUserListener
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    mView = inflater.inflate(R.layout.fragment_popup_select_user, container, false)
    initData()
    initViews()
    handleEvents()
    return mView
  }

  override fun onStart() {
    super.onStart()
    dialog?.window?.setLayout(600, 1000)
  }

  private fun initData() {
    val currentUserId = mSharedPreferences.get(SharedPrefsKey.CURRENT_USER_ID, String::class.java)
    arguments?.getParcelableArrayList<User>(Constants.LIST_USER_IN_GROUP)?.toList()?.let {
      mListUserInGroup = it.filter { user -> user.id != currentUserId }
    }
    arguments?.getParcelableArrayList<UserState>(Constants.LIST_USER_SELECTED)?.toList()?.let {
      mListUserSelected = it
    }
  }

  private fun initViews() {
    mSelectUserAdapter = SelectedUserAdapter()
    mView.userRecyclerView.adapter = mSelectUserAdapter
    mView.userRecyclerView.layoutManager = LinearLayoutManager(context)
    mView.userRecyclerView.hasFixedSize()
    mView.userRecyclerView.clearAnimation()

    val listSub = mListUserInGroup.map { user ->
      if (mListUserSelected.map { userSelected -> userSelected.idUser }.contains(user.id)) {
        true to user
      } else {
        false to user
      }
    }
    mSelectUserAdapter.submitList(listSub)
  }

  private fun handleEvents() {
    mView.cancelButton.clicks()
      .subscribe {
        dismiss()
      }
      .addTo(mCompositeDisposable)
    mView.okeButton.clicks()
      .subscribe {
        mSelectedListener?.onUserSelected(
          mSelectUserAdapter.currentList.filter { it.first }.map { it.second }
        )
        dismiss()
      }
      .addTo(mCompositeDisposable)
  }

  override fun onDestroy() {
    mCompositeDisposable.clear()
    super.onDestroy()
  }

  interface SelectedUserListener {
    fun onUserSelected(users: List<User>)
  }

  companion object {
    var TAG: String = PopupSelectUserFragment::class.java.simpleName

    fun newInstance(users: List<User>, usersSelected: List<UserState>): PopupSelectUserFragment {
      return PopupSelectUserFragment().apply {
        arguments = bundleOf(
          Constants.LIST_USER_IN_GROUP to ArrayList(users),
          Constants.LIST_USER_SELECTED to ArrayList(usersSelected)
        )
      }
    }
  }
}

