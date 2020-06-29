package com.phong.teamcnpm.teambuilding.ui.main.event

import com.phong.teamcnpm.teambuilding.domain.entities.Event
import com.phong.teamcnpm.teambuilding.ui.BasePresenter
import com.phong.teamcnpm.teambuilding.ui.BaseView
import io.reactivex.rxjava3.core.Single
import java.security.acl.Group

interface EventPresenter : BasePresenter {
  fun observerEventsFromLocale()
  fun getEventsByUser()
}

interface EventView : BaseView {
  fun getAllEventsSuccess(events: List<Event>)
}