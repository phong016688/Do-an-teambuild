package com.phong.teamcnpm.teambuilding.data.repository

import com.phong.teamcnpm.teambuilding.data.source.local.room.GroupEntity
import com.phong.teamcnpm.teambuilding.data.source.local.room.MyAppDao
import com.phong.teamcnpm.teambuilding.data.source.remote.service.RestfulApi
import com.phong.teamcnpm.teambuilding.domain.entities.Group
import com.phong.teamcnpm.teambuilding.domain.entities.User
import com.phong.teamcnpm.teambuilding.domain.repository.GroupRepository
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.rx3.asObservable
import kotlinx.coroutines.rx3.rxSingle

class GroupRepositoryImpl(
  private val api: RestfulApi,
  private val localApi: MyAppDao
) : GroupRepository {

  @ExperimentalCoroutinesApi
  override fun observeGroupFromLocale(): Observable<Group> {
    return localApi.groupDao().observeGroup()
      .map { it.map(GroupEntity::toGroup).first() }
      .asObservable()
  }

  override fun getGroupById(groupId: String): Single<Group> {
    return api.getGroupById(groupId)
      .map { it.data.toGroup() }
  }

  override fun getGroupFromLocale(): Single<Group> {
    return rxSingle {
      localApi.groupDao().getGroup().firstOrNull()?.toGroup() ?: Group()
    }
  }

  override fun saveGroupToLocale(group: Group): Single<Unit> {
    return rxSingle {
      localApi.groupDao().insertGroup(GroupEntity.fromGroup(group))
    }
  }

  override fun getUserInGroup(groupId: String): Single<List<User>> {
    return api.getUserInGroup(groupId).map { it.data }
  }
}