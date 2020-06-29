package com.phong.teamcnpm.teambuilding.data.source.remote.response

import com.google.gson.annotations.SerializedName
import com.phong.teamcnpm.teambuilding.domain.entities.Event
import com.phong.teamcnpm.teambuilding.domain.entities.Group
import com.phong.teamcnpm.teambuilding.domain.entities.User

data class GroupResponse(
  @SerializedName("_id")
  val id: String,
  val name: String,
  val avatar: String,
  val description: String,
  val isActive: Boolean,
  val createdAt: Double,
  val createdBy: User,
  val updatedAt: Double,
  val updatedBy: User,
  val eventCount: Int
) {
  fun toGroup(): Group {
    return Group(
      id = this.id,
      avatar = this.avatar,
      createdAt = this.createdAt,
      createdBy = this.createdBy,
      description = this.description,
      isActive = this.isActive,
      name = this.name,
      updatedAt = this.updatedAt,
      eventCount = this.eventCount
    )
  }
}