package com.phong.teamcnpm.teambuilding.ui.group.update_event

import com.phong.teamcnpm.teambuilding.domain.entities.Event
import com.phong.teamcnpm.teambuilding.ui.BasePresenter
import com.phong.teamcnpm.teambuilding.ui.BaseView

interface UpdateEventPresenter : BasePresenter {
  fun updateEvent(event: Event)
  fun deleteEvent(event: Event)
  fun completeEvent(event: Event)
  fun cancelEvent(event: Event)
}

interface UpdateEventView : BaseView {
  fun onUpdateEventSuccess()
  fun onDeleteEventSuccess()
  fun onCompleteEventSuccess()
  fun onCancelEventSuccess()
}