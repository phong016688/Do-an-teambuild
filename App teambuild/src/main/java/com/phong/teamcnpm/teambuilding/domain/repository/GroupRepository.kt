package com.phong.teamcnpm.teambuilding.domain.repository

import com.phong.teamcnpm.teambuilding.domain.entities.Group
import com.phong.teamcnpm.teambuilding.domain.entities.User
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface GroupRepository {
  fun getGroupById(groupId: String): Single<Group>
  fun getGroupFromLocale(): Single<Group>
  fun observeGroupFromLocale(): Observable<Group>
  fun saveGroupToLocale(group: Group): Single<Unit>
  fun getUserInGroup(groupId: String): Single<List<User>>
}