package com.phong.teamcnpm.teambuilding.domain.repository

import com.phong.teamcnpm.teambuilding.domain.entities.User
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single


interface AuthenticationRepository {
  fun observeLogout(): Observable<Unit>
  fun login(email: String, password: String): Single<Unit>
  fun syncDataCurrentUser(): Single<Unit>
  fun logout(): Single<Unit>
  fun getCurrentUserFormLocale(): Single<User>
  fun observeCurrentUserFormLocale(): Observable<User>
}