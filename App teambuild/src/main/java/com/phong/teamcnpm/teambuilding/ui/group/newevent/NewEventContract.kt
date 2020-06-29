package com.phong.teamcnpm.teambuilding.ui.group.newevent

import com.phong.teamcnpm.teambuilding.domain.entities.Event
import com.phong.teamcnpm.teambuilding.ui.BasePresenter
import com.phong.teamcnpm.teambuilding.ui.BaseView

interface NewEventPresenter : BasePresenter {
  fun newEvent(event: Event)
}

interface NewEventView : BaseView {
}