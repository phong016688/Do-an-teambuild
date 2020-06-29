package com.phong.teamcnpm.teambuilding.domain.entities

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Event(
  @SerializedName("_id")
  val id: String = "",
  val createdAt: Double = 0.0,
  val createdBy: User? = null,
  val description: String = "",
  val likeCount: Int = 0,
  val dislikeCount: Int = 0,
  val feedbackCount: Int = 0,
  @SerializedName("idGroup")
  val groupId: String = "",
  val isActive: Boolean = true,
  val isLocked: Boolean = true,
  val name: String = "",
  val state: String = "",
  val updatedAt: Double = 0.0,
  val updatedBy: User? = null,
  val users: List<UserState> = emptyList(),
  val likeCurrent: Vote? = null
) : Parcelable {
  fun hasId() = id.isNotEmpty()
}