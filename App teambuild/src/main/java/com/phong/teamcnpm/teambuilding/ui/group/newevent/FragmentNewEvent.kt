package com.phong.teamcnpm.teambuilding.ui.group.newevent

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import com.jakewharton.rxbinding4.view.clicks
import com.phong.teamcnpm.teambuilding.R
import com.phong.teamcnpm.teambuilding.domain.entities.Event
import com.phong.teamcnpm.teambuilding.domain.entities.User
import com.phong.teamcnpm.teambuilding.domain.entities.UserState
import com.phong.teamcnpm.teambuilding.ui.group.newevent.popup.PopupSelectUserFragment
import com.phong.teamcnpm.teambuilding.utils.Constants
import com.phong.teamcnpm.teambuilding.utils.extensions.addTo
import com.phong.teamcnpm.teambuilding.utils.extensions.gone
import com.phong.teamcnpm.teambuilding.utils.extensions.hideSoftKeyboardFromWindow
import com.phong.teamcnpm.teambuilding.utils.extensions.show
import com.phong.teamcnpm.teambuilding.widgets.BaseBottomSheetFragment
import com.phong.teamcnpm.teambuilding.widgets.DialogManager
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_new_event.view.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.util.ArrayList
import java.util.concurrent.TimeUnit

class FragmentNewEvent : BaseBottomSheetFragment(), NewEventView,
  PopupSelectUserFragment.SelectedUserListener {
  private val mPresenter: NewEventPresenter by inject { parametersOf(this) }
  private val mDialogManager: DialogManager by inject { parametersOf(requireContext()) }
  private val mCompositeDisposable = CompositeDisposable()
  private var mUsersInGroup: List<User> = emptyList()
  private lateinit var mView: View
  private var mEventNew: Event = Event()
  private var mUserSelected: List<UserState> = emptyList()
  private var mNewEventListener: NewEventListener? = null

  override val disposables: CompositeDisposable
    get() = mCompositeDisposable

  override fun isShowAnchorView(): Boolean = true

  override fun isSheetAlwaysExpanded(): Boolean = false

  override fun onUserSelected(users: List<User>) {
    mUserSelected = users.map {
      UserState(
        it.id,
        "APPROVED"
      )
    }
    mView.usersTitleTextView.text = "${mUserSelected.size} user selected"
  }

  override fun onAttach(context: Context) {
    super.onAttach(context)
    if (context is NewEventListener) {
      mNewEventListener = context
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    inflateView(R.layout.fragment_new_event)
    mView = view
    super.onViewCreated(view, savedInstanceState)
    arguments?.getParcelableArrayList<User>(Constants.LIST_USER_IN_GROUP)?.toList()?.let {
      mUsersInGroup = it
    }
    initView()
    handleEvent()
  }

  override fun onComplete() {
    mView.loadingContainer.gone()
    mNewEventListener?.onNewEventListener?.invoke()
    dismiss()
  }

  override fun onGetError(throwable: Throwable) {
    mView.loadingContainer.gone()
    Log.d(TAG, throwable.message.toString())
  }

  override fun onDestroy() {
    mCompositeDisposable.clear()
    super.onDestroy()
  }

  private fun handleEvent() {
    mView.setOnClickListener {
      context?.hideSoftKeyboardFromWindow(it)
    }
    mView.pickTimeImageView.clicks()
      .debounce(200, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
      .subscribe {
        mDialogManager.showDatePickerDialog()
      }.addTo(mCompositeDisposable)
    mView.selectUsersButton.clicks()
      .debounce(200, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
      .subscribe {
        PopupSelectUserFragment.newInstance(mUsersInGroup, mUserSelected)
          .show(childFragmentManager, PopupSelectUserFragment.TAG)
      }
      .addTo(mCompositeDisposable)
    mView.submitButton.clicks()
      .filter { checkCanSubmitData() }
      .doOnNext { mView.loadingContainer.show() }
      .subscribe {
        val groupId = arguments?.getString(Constants.GROUP_ID) ?: return@subscribe
        val description = mView.timeTextView.text.toString() + "<tb>" +
            mView.addressTextView.text.toString() + "<tb>" +
            mView.numberOfParticipantsEditText.text.toString() + "<tb>" +
            mView.messageEditText.text.toString()
        setDataNewEvent(
          mView.titleEventEditText.text.toString(),
          groupId,
          mUserSelected,
          description
        )
        mPresenter.newEvent(mEventNew)
      }.addTo(mCompositeDisposable)
  }

  private fun checkCanSubmitData(): Boolean {
    when {
      mView.titleEventEditText.text.isEmpty() -> mView.errorMessageTextView.text =
        "Name field is not empty"
      mView.timeTextView.text.isEmpty() -> mView.errorMessageTextView.text =
        "Time field is not empty"
      mView.addressTextView.text.isEmpty() -> mView.errorMessageTextView.text =
        "Address field is not empty"
      mView.numberOfParticipantsEditText.text.isEmpty() -> mView.errorMessageTextView.text =
        "Number field  is not empty"
      else -> return true
    }
    return false
  }

  private fun setDataNewEvent(
    name: String,
    groupId: String,
    users: List<UserState>,
    description: String
  ) {
    mEventNew = Event(
      name = name,
      groupId = groupId,
      users = users,
      description = description
    )
  }

  private fun initView() {
    mDialogManager
      .datePickerDialog(DatePickerDialog.OnDateSetListener { _, year, month, day ->
        mView.timeTextView.text = "$day/$month/$year"
      })
  }

  interface NewEventListener {
    val onNewEventListener: () -> Unit
  }

  companion object {
    val TAG = this::class.java.simpleName
    fun newInstance(usersInGroup: List<User>, groupId: String) = FragmentNewEvent().apply {
      arguments = bundleOf(
        Constants.LIST_USER_IN_GROUP to ArrayList(usersInGroup),
        Constants.GROUP_ID to groupId
      )
    }
  }
}