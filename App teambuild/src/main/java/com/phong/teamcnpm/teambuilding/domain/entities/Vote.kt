package com.phong.teamcnpm.teambuilding.domain.entities

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Vote(
  @SerializedName("_id")
  val id: String = "",
  val idUser: String = "",
  val idEvent: String = "",
  val type: String = "",
  val createAt: Long = 0L,
  val updateAt: Long = 0L
) : Parcelable