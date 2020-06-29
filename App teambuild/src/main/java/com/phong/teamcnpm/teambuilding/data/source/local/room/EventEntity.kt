package com.phong.teamcnpm.teambuilding.data.source.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.phong.teamcnpm.teambuilding.domain.entities.Event

@Entity(tableName = "current_event")
data class EventEntity(
  @PrimaryKey
  val id: String = "",
  val createdAt: Double = 0.0,
  val description: String = "",
  val dislikeCount: Int = 0,
  val feedbackCount: Int = 0,
  val idGroup: String = "",
  val likeCount: Int = 0,
  val name: String = "",
  val state: String = ""
) {
  fun toEvent(): Event {
    return Event(
      id = this.id,
      createdAt = this.createdAt,
      description = this.description,
      dislikeCount = this.dislikeCount,
      feedbackCount = this.feedbackCount,
      groupId = this.idGroup,
      likeCount = this.likeCount,
      name = this.name,
      state = this.state
    )
  }

  companion object {
    fun fromEvent(event: Event): EventEntity {
      return EventEntity(
        id = event.id,
        createdAt = event.createdAt,
        description = event.description,
        dislikeCount = event.dislikeCount,
        feedbackCount = event.feedbackCount,
        idGroup = event.groupId,
        likeCount = event.likeCount,
        name = event.name,
        state = event.state
      )
    }
  }
}