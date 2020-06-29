package com.phong.teamcnpm.teambuilding.ui.group.update_event

import android.util.Log
import com.phong.teamcnpm.teambuilding.domain.entities.Event
import com.phong.teamcnpm.teambuilding.domain.repository.EventRepository
import com.phong.teamcnpm.teambuilding.utils.extensions.addTo
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class UpdateEventPresenterImpl(
  private val view: UpdateEventView,
  private val eventRepo: EventRepository
) : UpdateEventPresenter {

  override fun updateEvent(event: Event) {
    eventRepo.updateEvent(event)
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({
        view.onUpdateEventSuccess()
        Log.d("#####", "success")
      }, {
        view.onGetError(it)
        Log.d("#####", it.message.toString())
      }).addTo(view.disposables)
  }

  override fun deleteEvent(event: Event) {
    Log.d("#####", "de ")
    eventRepo.deleteEvent(event)
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({
        view.onDeleteEventSuccess()
        Log.d("#####", "de success")
      }, {
        view.onGetError(it)
        Log.d("#####", it.message.toString())
      })
      .addTo(view.disposables)
  }

  override fun completeEvent(event: Event) {
    eventRepo.completeEvent(event)
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({
        view.onCompleteEventSuccess()
      }, {
        view.onGetError(it)
      })
      .addTo(view.disposables)
  }

  override fun cancelEvent(event: Event) {
    eventRepo.cancelEvent(event)
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({
        view.onCancelEventSuccess()
      }, {
        view.onGetError(it)
      })
      .addTo(view.disposables)
  }

  override fun onStart() = Unit

  override fun onStop() = Unit

  override fun onDestroy() = Unit
}