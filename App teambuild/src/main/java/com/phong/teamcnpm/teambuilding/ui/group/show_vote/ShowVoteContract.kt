package com.phong.teamcnpm.teambuilding.ui.group.show_vote

import com.phong.teamcnpm.teambuilding.domain.entities.Vote
import com.phong.teamcnpm.teambuilding.ui.BasePresenter
import com.phong.teamcnpm.teambuilding.ui.BaseView

interface ShowVotePresenter : BasePresenter {
  fun getAllVoteInEvent(eventId: String)
}

interface ShowVoteView : BaseView {
  fun onGetAllVoteSuccess(votes: List<Vote>)
}