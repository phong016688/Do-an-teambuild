package com.phong.teamcnpm.teambuilding.ui.group.update_event

import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import com.jakewharton.rxbinding4.view.clicks
import com.phong.teamcnpm.teambuilding.R
import com.phong.teamcnpm.teambuilding.domain.entities.Event
import com.phong.teamcnpm.teambuilding.domain.entities.User
import com.phong.teamcnpm.teambuilding.domain.entities.UserState
import com.phong.teamcnpm.teambuilding.ui.group.newevent.FragmentNewEvent
import com.phong.teamcnpm.teambuilding.ui.group.newevent.popup.PopupSelectUserFragment
import com.phong.teamcnpm.teambuilding.utils.Constants
import com.phong.teamcnpm.teambuilding.utils.extensions.*
import com.phong.teamcnpm.teambuilding.widgets.BaseBottomSheetFragment
import com.phong.teamcnpm.teambuilding.widgets.DialogManager
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_update_event.view.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.util.ArrayList
import java.util.concurrent.TimeUnit

class FragmentUpdateEvent : BaseBottomSheetFragment(), UpdateEventView,
  PopupSelectUserFragment.SelectedUserListener {
  private val mPresenter: UpdateEventPresenter by inject { parametersOf(this) }
  private val mDialogManager: DialogManager by inject { parametersOf(requireContext()) }
  private val mCompositeDisposable = CompositeDisposable()
  private var mUsersInGroup: List<User> = emptyList()
  private lateinit var mView: View
  private var mEventNew: Event? = null
  private var mEventOld: Event? = null
  private var mUserSelected: List<UserState> = emptyList()
  private var mNewEventListener: FragmentNewEvent.NewEventListener? = null

  override val disposables: CompositeDisposable
    get() = mCompositeDisposable

  override fun isShowAnchorView(): Boolean = true

  override fun isSheetAlwaysExpanded(): Boolean = true

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
    if (context is FragmentNewEvent.NewEventListener) {
      mNewEventListener = context
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    inflateView(R.layout.fragment_update_event)
    mView = view
    super.onViewCreated(view, savedInstanceState)
    arguments?.getParcelableArrayList<User>(Constants.LIST_USER_IN_GROUP)?.toList()?.let {
      mUsersInGroup = it
    }
    arguments?.getParcelable<Event>(Constants.EVENT)?.let {
      mEventOld = it
    }
    initView()
    handleEvent()
    mUserSelected = mEventOld?.users ?: emptyList()
  }


  override fun onUpdateEventSuccess() {
    mView.loadingContainer.gone()
    mNewEventListener?.onNewEventListener?.invoke()
    dismiss()
  }

  override fun onDeleteEventSuccess() {
    mView.loadingContainer.gone()
    mNewEventListener?.onNewEventListener?.invoke()
    dismiss()
  }

  override fun onCompleteEventSuccess() {
    mView.loadingContainer.gone()
    mNewEventListener?.onNewEventListener?.invoke()
    dismiss()
  }

  override fun onCancelEventSuccess() {
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
      .map { checkCanSubmitData() }
      .doOnNext { mView.loadingContainer.show() }
      .subscribe {
        if (!it) {
          context?.toast("Not change")
          mView.loadingContainer.gone()
          return@subscribe
        }
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
        mEventNew?.let {
          mPresenter.updateEvent(it)
          if (mView.stateEventTextView.text == "CANCELLED") {
            mPresenter.cancelEvent(it)
          } else if (mView.stateEventTextView.text == "COMPLETED") {
            mPresenter.completeEvent(it)
          }
        }
      }.addTo(mCompositeDisposable)
    mView.statePre.clicks()
      .filter { mView.statePre.text.isNotEmpty() }
      .filter { mView.stateEventTextView.text != "CANCELLED" }
      .subscribe {
        if (mView.stateEventTextView.text == "COMPLETED") {
          mView.stateEventTextView.handleToStateEvent("PROCESSING")
        } else {
          mView.stateEventTextView.handleToStateEvent("CANCELLED")
        }
      }.addTo(mCompositeDisposable)
    mView.stateNext.clicks()
      .filter { mView.stateNext.text.isNotEmpty() }
      .filter { mView.stateEventTextView.text != "COMPLETED" }
      .subscribe {
        if (mView.stateEventTextView.text == "CANCELLED") {
          mView.stateEventTextView.handleToStateEvent("PROCESSING")
        } else {
          mView.stateEventTextView.handleToStateEvent("COMPLETED")
        }
      }.addTo(mCompositeDisposable)
    mView.deleteButton.clicks()
      .subscribe {
        mDialogManager.dialogBasic("Confirm",
          "You want delete this event",
          "OKE",
          "CANCEL",
          DialogInterface.OnClickListener { dialogInterface, _ ->
            mEventOld?.let {
              mPresenter.deleteEvent(it)
              mView.loadingContainer.show()
            }
            dialogInterface.dismiss()
          },
          DialogInterface.OnClickListener { dialogInterface, _ ->
            dialogInterface.dismiss()
          })
      }
  }

  private fun checkCanSubmitData(): Boolean {
    val descriptions = mEventOld?.description?.split("<tb>")
    return (mView.titleEventEditText.text.toString() != mEventOld?.name ?: "" ||
        mView.timeTextView.text.toString() != descriptions?.getOrNull(0) ?: "" ||
        mView.addressTextView.text.toString() != descriptions?.getOrNull(1) ?: "" ||
        mView.numberOfParticipantsEditText.text.toString() != descriptions?.getOrNull(2) ?: "" ||
        mView.messageEditText.text.toString() != descriptions?.getOrNull(3) ?: "" ||
        mView.stateEventTextView.text.toString() != mEventOld?.state ?: "" ||
        mView.usersTitleTextView.text.toString() != "Add users: ")
        && mView.titleEventEditText.text.isNotEmpty()
  }

  private fun setDataNewEvent(
    name: String,
    groupId: String,
    users: List<UserState>,
    description: String
  ) {
    mEventNew = Event(
      id = mEventOld?.id ?: "",
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
    mView.apply {
      mEventOld?.let { event ->
        val descriptions = event.description.split("<tb>")
        titleEventEditText.setText(event.name)
        timeTextView.text = descriptions.getOrNull(0) ?: ""
        addressTextView.setText(descriptions.getOrNull(1) ?: "")
        numberOfParticipantsEditText.setText(descriptions.getOrNull(2) ?: "")
        messageEditText.setText(descriptions.getOrNull(3) ?: "")
        stateEventTextView.handleToStateEvent(event.state)
        when (event.state) {
          "COMPLETED" -> {
            statePre.text = ""
            stateNext.text = ""
          }
          "PROCESSING" -> {
            statePre.text = "<"
            stateNext.text = ">"
          }
          "CANCELLED" -> {
            statePre.text = ""
            stateNext.text = ""
          }
        }
      }
    }
  }

  companion object {
    val TAG = this::class.java.simpleName
    fun newInstance(usersInGroup: List<User>, groupId: String, oldEvent: Event) =
      FragmentUpdateEvent().apply {
        arguments = bundleOf(
          Constants.LIST_USER_IN_GROUP to ArrayList(usersInGroup),
          Constants.GROUP_ID to groupId,
          Constants.EVENT to oldEvent
        )
      }
  }
}