package com.phong.teamcnpm.teambuilding.data.source.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.phong.teamcnpm.teambuilding.domain.entities.User

@Entity(tableName = "current_user")
data class UserEntity(
  @PrimaryKey
  val id: String = "",
  val name: String = "",
  val email: String = "",
  val phoneNumber: String = "",
  val avatar: String = "",
  val birthday: String = "",
  val role: String = "",
  val groupId: String = ""
) {
  fun toUser(): User {
    return User(
      id = this.id,
      name = this.name,
      email = this.email,
      phoneNumber = this.phoneNumber,
      avatar = this.avatar,
      birthday = this.birthday,
      role = this.role,
      groupId = this.groupId
    )
  }

  companion object {
    fun fromUser(user: User): UserEntity {
      return UserEntity(
        id = user.id,
        name = user.name,
        email = user.email,
        phoneNumber = user.phoneNumber,
        avatar = user.avatar,
        birthday = user.birthday,
        role = user.role,
        groupId = user.groupId
      )
    }
  }
}
