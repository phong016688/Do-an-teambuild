package com.phong.teamcnpm.teambuilding.data.source.remote.response

import com.google.gson.annotations.SerializedName
import com.phong.teamcnpm.teambuilding.domain.entities.Event
import com.phong.teamcnpm.teambuilding.domain.entities.User
import com.phong.teamcnpm.teambuilding.domain.entities.UserState
import com.phong.teamcnpm.teambuilding.domain.entities.Vote

data class EventResponse(
  @SerializedName("_id")
  val id: String,
  val name: String,
  val idGroup: String,
  val users: List<UserState>,
  val description: String,
  val isActive: Boolean,
  val isLocked: Boolean,
  val state: String,
  val createdAt: Double,
  val createdBy: User,
  val updatedAt: Double,
  val updatedBy: User,
  val dislikeCount: Int,
  val likeCount: Int = 0,
  val feedbackCount: Int = 0,
  val likeCurrent: Vote? = null

  ) {
  fun toEvent() = Event(
    id = this.id,
    name = this.name,
    groupId = this.idGroup,
    users = this.users,
    description = this.description,
    isActive = this.isActive,
    isLocked = this.isLocked,
    state = this.state,
    createdAt = this.createdAt,
    createdBy = this.createdBy,
    updatedAt = this.updatedAt,
    updatedBy = this.updatedBy,
    dislikeCount = this.dislikeCount,
    likeCount = this.likeCount,
    feedbackCount = feedbackCount,
    likeCurrent = likeCurrent
  )
}