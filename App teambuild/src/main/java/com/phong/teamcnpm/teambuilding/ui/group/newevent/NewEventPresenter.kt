package com.phong.teamcnpm.teambuilding.ui.group.newevent

import com.phong.teamcnpm.teambuilding.domain.entities.Event
import com.phong.teamcnpm.teambuilding.domain.repository.EventRepository
import com.phong.teamcnpm.teambuilding.utils.extensions.addTo
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class NewEventPresenterImpl(
  private val view: NewEventView,
  private val eventRepo: EventRepository
) : NewEventPresenter {

  override fun newEvent(event: Event) {
    eventRepo.newEvent(event)
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({
        view.onComplete()
      }, {
        view.onGetError(it)
      }).addTo(view.disposables)
  }

  override fun onStart() = Unit

  override fun onStop() = Unit

  override fun onDestroy() = Unit
}