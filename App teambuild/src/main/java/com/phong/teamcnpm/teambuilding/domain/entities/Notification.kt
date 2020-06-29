package com.phong.teamcnpm.teambuilding.domain.entities

import com.google.gson.annotations.SerializedName

data class Notification(
  @SerializedName("_id")
  val id: String = "",
  val title: String,
  val content: String,
  val image: String
)