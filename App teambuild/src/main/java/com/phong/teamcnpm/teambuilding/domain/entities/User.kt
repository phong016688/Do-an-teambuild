package com.phong.teamcnpm.teambuilding.domain.entities

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
  @SerializedName("_id")
  val id: String = "",
  val avatar: String = "",
  val birthday: String = "",
  val createdAt: Double = 0.0,
  val createdBy: User? = null,
  val email: String = "",
  val isActive: Boolean = true,
  val isLocked: Boolean = true,
  @SerializedName("idGroup")
  val groupId: String = "",
  val name: String = "",
  val phoneNumber: String = "",
  val role: String = "0",
  val updatedAt: Double = 0.0,
  val permission: Permission? = null
) : Parcelable