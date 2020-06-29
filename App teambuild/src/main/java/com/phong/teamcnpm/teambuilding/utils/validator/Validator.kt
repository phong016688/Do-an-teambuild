package com.phong.teamcnpm.teambuilding.utils.validator

import android.content.Context

class Validator(private val context: Context) {

    fun isValidEmail(email: String): Boolean {
        return if (email.isBlank()) {
            false
        } else {
            email.matches(EMAIL_FORMAT.toRegex())
        }
    }

    fun isValidPassword(password: String): Boolean {
        return password.isNotBlank() && password.length >= 6
    }

    companion object {
        const val EMAIL_FORMAT =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)(\\.[A-Za-z]{2,}){0,1}\$"
    }
}