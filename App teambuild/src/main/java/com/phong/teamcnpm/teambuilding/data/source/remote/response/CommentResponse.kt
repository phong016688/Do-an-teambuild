package com.phong.teamcnpm.teambuilding.data.source.remote.response

import com.google.gson.annotations.SerializedName
import com.phong.teamcnpm.teambuilding.domain.entities.Comment
import com.phong.teamcnpm.teambuilding.domain.entities.User

data class CommentResponse(
  @SerializedName("_id")
  val id: String,
  val idUser: String,
  val idEvent: String,
  val content: String,
  val createAt: Float
) {
  fun toComment(): Comment = Comment(
    id,
    idUser,
    idEvent,
    content,
    createAt.toLong()
  )
}