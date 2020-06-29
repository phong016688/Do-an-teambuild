package com.phong.teamcnpm.teambuilding.data

sealed class Result<out T> {
    data class Success<out T>(val data: T?) : Result<T>()
    data class Loading<out T>(val value: T?) : Result<T>()
    data class Error<out T>(val error: Throwable) : Result<T>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$error]"
            is Loading -> "Loading"
        }
    }
}