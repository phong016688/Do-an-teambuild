package com.phong.teamcnpm.teambuilding.ui.group.comment_event

import com.phong.teamcnpm.teambuilding.domain.repository.EventRepository
import com.phong.teamcnpm.teambuilding.utils.extensions.addTo
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class CommentPresenterImpl(
  private val view: CommentView,
  private val eventRepo: EventRepository
) : CommentPresenter {

  override fun getCommentsForEvent(eventId: String) {
    eventRepo.allCommentEvent(eventId)
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({
        view.onGetCommentsForEventSuccess(it)
      }, {
        view.onGetError(it)
      })
      .addTo(view.disposables)
  }

  override fun newCommentInEvent(eventId: String, content: String) {
    eventRepo.addCommentInEvent(eventId, content)
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({
        view.onNewCommentInEventSuccess()
      }, {
        view.onGetError(it)
      })
      .addTo(view.disposables)
  }

  override fun onStart() = Unit

  override fun onStop() = Unit

  override fun onDestroy() = Unit
}