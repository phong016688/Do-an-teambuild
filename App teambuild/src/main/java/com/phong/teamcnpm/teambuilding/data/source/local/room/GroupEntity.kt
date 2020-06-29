package com.phong.teamcnpm.teambuilding.data.source.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.phong.teamcnpm.teambuilding.domain.entities.Group

@Entity(tableName = "current_group")
data class GroupEntity(
  @PrimaryKey
  val id: String = "",
  val avatar: String = "",
  val createdAt: Double = 0.0,
  val description: String = "",
  val name: String = ""
) {
  fun toGroup(): Group {
    return Group(
      id = this.id,
      avatar = this.avatar,
      createdAt = this.createdAt,
      description = this.description,
      name = this.name
    )
  }

  companion object {
    fun fromGroup(group: Group): GroupEntity {
      return GroupEntity(
        group.id,
        group.avatar,
        group.createdAt,
        group.description,
        group.name
      )
    }
  }
}