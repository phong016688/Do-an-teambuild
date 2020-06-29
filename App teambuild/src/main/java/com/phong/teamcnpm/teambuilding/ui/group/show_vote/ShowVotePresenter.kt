package com.phong.teamcnpm.teambuilding.ui.group.show_vote

import com.phong.teamcnpm.teambuilding.domain.repository.EventRepository
import com.phong.teamcnpm.teambuilding.utils.extensions.addTo
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class ShowVotePresenterImpl(
  private val view: ShowVoteView,
  private val eventRepo: EventRepository
) : ShowVotePresenter {

  override fun getAllVoteInEvent(eventId: String) {
    eventRepo.getAllVoteEvent(eventId)
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({
        view.onGetAllVoteSuccess(it)
      }, {
        view.onGetError(it)
      })
      .addTo(view.disposables)
  }

  override fun onStart() = Unit

  override fun onStop() = Unit

  override fun onDestroy() = Unit
}