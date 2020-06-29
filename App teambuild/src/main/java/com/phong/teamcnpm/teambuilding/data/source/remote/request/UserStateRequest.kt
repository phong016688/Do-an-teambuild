package com.phong.teamcnpm.teambuilding.data.source.remote.request

import com.google.gson.annotations.SerializedName

data class UserStateRequest(
  @SerializedName("idUser")
  val idUser: String,
  @SerializedName("state")
  val state: String
)