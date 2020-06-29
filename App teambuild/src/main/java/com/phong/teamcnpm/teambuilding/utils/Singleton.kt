package com.phong.teamcnpm.teambuilding.utils

class Singleton<out T> private constructor() {

    @Volatile // read from memory, not read from cache
    private var instances: Singleton<T>? = null
    fun getInstance(): Singleton<T> {
        return instances ?: synchronized(this) { // synchroized if has two thread call getinstance
            instances ?: Singleton<T>().also { instances = it }
        }
    }
}

