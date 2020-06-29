package com.phong.teamcnpm.teambuilding.ui.group

import com.phong.teamcnpm.teambuilding.data.source.local.preference.SharedPrefsApi
import com.phong.teamcnpm.teambuilding.domain.repository.EventRepository
import com.phong.teamcnpm.teambuilding.domain.repository.GroupRepository
import com.phong.teamcnpm.teambuilding.utils.extensions.addTo
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class GroupDetailPresenterImpl(
  private val view: GroupDetailView,
  private val groupRepo: GroupRepository,
  private val evenRepo: EventRepository,
  private val prefsApi: SharedPrefsApi
) : GroupDetailPresenter {

  override fun getGroupById(id: String) {
    groupRepo.getGroupById(id)
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({
        view.onGetGroupSuccess(it)
      }, {
        view.onGetError(it)
      })
      .addTo(view.disposables)
  }

  override fun getEventsInGroup(groupId: String) {
    evenRepo.getEventsInGroup(groupId)
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({
        view.onGetEventsByUserSuccess(it)
      }, {
        view.onGetError(it)
      })
      .addTo(view.disposables)
  }

  override fun getUsersInGroup(groupId: String) {
    groupRepo.getUserInGroup(groupId)
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({
        view.onGetUsersInGroupSuccess(it)
      }, {
        view.onGetError(it)
      })
      .addTo(view.disposables)
  }

  override fun modifyVote(eventId: String, type: String, groupId: String) {
    evenRepo.modifyEvent(eventId, type)
      .flatMap { evenRepo.getEventsInGroup(groupId) }
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({
        view.onGetEventsByUserSuccess(it)
      }, {
        view.onGetError(it)
      })
      .addTo(view.disposables)
  }

  override fun onStart() = Unit

  override fun onStop() = Unit

  override fun onDestroy() = Unit
}