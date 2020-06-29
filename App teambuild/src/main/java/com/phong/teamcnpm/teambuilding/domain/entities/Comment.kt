package com.phong.teamcnpm.teambuilding.domain.entities

import com.google.gson.annotations.SerializedName

data class Comment(
  @SerializedName("_id")
  val id: String = "",
  val idUser: String = "",
  val idEvent: String = "",
  val content: String = "",
  val createAt: Long = 0
)