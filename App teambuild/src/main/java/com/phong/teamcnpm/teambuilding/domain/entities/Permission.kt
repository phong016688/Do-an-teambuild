package com.phong.teamcnpm.teambuilding.domain.entities

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Permission(
  @SerializedName("_id")
  val id: String = "",
  val code: String = "",
  val createdAt: Double = 0.0,
  val description: String = "",
  val updatedAt: Double = 0.0
):Parcelable