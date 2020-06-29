package com.phong.teamcnpm.teambuilding.domain.entities

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Group(
  @SerializedName("_id")
  val id: String = "",
  val avatar: String = "",
  val createdAt: Double = 0.0,
  val createdBy: User? = null,
  val description: String = "",
  val isActive: Boolean = true,
  val name: String = "",
  val updatedAt: Double = 0.0,
  val eventCount: Int = 0
) : Parcelable