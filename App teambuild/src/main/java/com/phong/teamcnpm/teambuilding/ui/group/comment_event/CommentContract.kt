package com.phong.teamcnpm.teambuilding.ui.group.comment_event

import com.phong.teamcnpm.teambuilding.domain.entities.Comment
import com.phong.teamcnpm.teambuilding.ui.BasePresenter
import com.phong.teamcnpm.teambuilding.ui.BaseView

interface CommentPresenter : BasePresenter {
  fun getCommentsForEvent(eventId: String)
  fun newCommentInEvent(eventId: String, content: String)
}

interface CommentView : BaseView {
  fun onGetCommentsForEventSuccess(comments: List<Comment>)
  fun onNewCommentInEventSuccess()
}
