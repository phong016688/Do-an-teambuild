package com.phong.teamcnpm.teambuilding.domain.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserState(
  val idUser: String = "",
  val state: String = ""
) : Parcelable