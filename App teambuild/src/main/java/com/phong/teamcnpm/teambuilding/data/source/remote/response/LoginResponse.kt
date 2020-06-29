package com.phong.teamcnpm.teambuilding.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("token")
    val token: String
)