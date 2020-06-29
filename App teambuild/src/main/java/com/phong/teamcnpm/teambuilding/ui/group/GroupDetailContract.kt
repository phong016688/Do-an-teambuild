package com.phong.teamcnpm.teambuilding.ui.group

import com.phong.teamcnpm.teambuilding.domain.entities.Event
import com.phong.teamcnpm.teambuilding.domain.entities.Group
import com.phong.teamcnpm.teambuilding.domain.entities.User
import com.phong.teamcnpm.teambuilding.ui.BasePresenter
import com.phong.teamcnpm.teambuilding.ui.BaseView

interface GroupDetailPresenter : BasePresenter {
  fun getGroupById(id: String)
  fun getEventsInGroup(groupId: String)
  fun getUsersInGroup(groupId: String)
  fun modifyVote(eventId: String, type: String, groupId: String)
}

interface GroupDetailView : BaseView {
  fun onGetGroupSuccess(group: Group)
  fun onGetEventsByUserSuccess(events: List<Event>)
  fun onGetUsersInGroupSuccess(users: List<User>)
}