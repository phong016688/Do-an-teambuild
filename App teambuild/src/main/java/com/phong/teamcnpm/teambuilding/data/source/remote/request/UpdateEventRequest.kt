package com.phong.teamcnpm.teambuilding.data.source.remote.request

import com.google.gson.annotations.SerializedName

data class UpdateEventRequest(
  @SerializedName("_id")
  val id: String,
  @SerializedName("input")
  val eventRequest: NewEventRequest
)