package com.phong.teamcnpm.teambuilding.data.source.remote.request

import com.phong.teamcnpm.teambuilding.domain.entities.Event

data class NewEventRequest(
  val name: String,
  val idGroup: String,
  val users: List<UserStateRequest>,
  val description: String
) {
  companion object {
    fun fromEvent(event: Event): NewEventRequest {
      return NewEventRequest(
        event.name,
        event.groupId,
        event.users.map { UserStateRequest(it.idUser, it.state) },
        event.description
      )
    }
  }
}