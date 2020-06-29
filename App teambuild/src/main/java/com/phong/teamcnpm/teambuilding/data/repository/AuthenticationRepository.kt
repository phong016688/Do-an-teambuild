package com.phong.teamcnpm.teambuilding.data.repository

import com.phong.teamcnpm.teambuilding.data.source.local.preference.SharedPrefsApi
import com.phong.teamcnpm.teambuilding.data.source.local.preference.SharedPrefsKey
import com.phong.teamcnpm.teambuilding.data.source.local.room.MyAppDao
import com.phong.teamcnpm.teambuilding.data.source.local.room.UserEntity
import com.phong.teamcnpm.teambuilding.data.source.remote.service.RestfulApi
import com.phong.teamcnpm.teambuilding.domain.entities.User
import com.phong.teamcnpm.teambuilding.domain.repository.AuthenticationRepository
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx3.asObservable
import kotlinx.coroutines.rx3.await
import kotlinx.coroutines.rx3.rxSingle
import java.util.concurrent.TimeUnit

class AuthenticationRepositoryImpl(
  private val api: RestfulApi,
  private val prefsApi: SharedPrefsApi,
  private val localeApi: MyAppDao
) : AuthenticationRepository {

  init {
    Observable.interval(3L, TimeUnit.MINUTES)
      .flatMapSingle { api.getProfileCurrentUser().map { Unit } }
      .onErrorReturn { Unit }
      .subscribeOn(Schedulers.io())
      .subscribe()
  }

  override fun observeLogout(): Observable<Unit> {
    return prefsApi.observe(SharedPrefsKey.KEY_TOKEN, String::class.java)
      .filter { it.isEmpty() }
      .map { Unit }
  }

  override fun login(email: String, password: String): Single<Unit> {
    return api.login(email, password)
      .flatMap {
        prefsApi.put(SharedPrefsKey.KEY_TOKEN, it.token)
        syncDataCurrentUser()
      }
  }

  override fun syncDataCurrentUser(): Single<Unit> {
    return rxSingle {
      val data = api.getProfileCurrentUser().map { it.data }.await()
      val user = data.toUser()
      prefsApi.put(SharedPrefsKey.ROLE_CURRENT_USER, user.role)
      prefsApi.put(SharedPrefsKey.CURRENT_USER_ID, user.id)
      prefsApi.put(SharedPrefsKey.CURRENT_GROUP_ID, user.groupId)
      localeApi.userDao().insertUser(UserEntity.fromUser(user))
    }
  }

  override fun logout(): Single<Unit> {
    return rxSingle {
      prefsApi.clearKey(SharedPrefsKey.KEY_TOKEN)
      prefsApi.clearKey(SharedPrefsKey.ROLE_CURRENT_USER)
      prefsApi.clearKey(SharedPrefsKey.CURRENT_USER_ID)
      prefsApi.clearKey(SharedPrefsKey.CURRENT_GROUP_ID)
      joinAll(
        launch { localeApi.userDao().deleteUser() },
        launch { localeApi.groupDao().deleteGroup() },
        launch { localeApi.eventDao().deleteEvent() }
      )
    }
  }

  override fun getCurrentUserFormLocale(): Single<User> {
    return rxSingle { localeApi.userDao().getCurrentUsers().firstOrNull()?.toUser() ?: User() }
  }

  @ExperimentalCoroutinesApi
  override fun observeCurrentUserFormLocale(): Observable<User> {
    return localeApi.userDao()
      .observerCurrentUsers()
      .map { it.map(UserEntity::toUser).first() }
      .asObservable()
  }
}