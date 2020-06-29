package com.phong.teamcnpm.teambuilding.data.repository

import com.phong.teamcnpm.teambuilding.data.source.local.room.EventEntity
import com.phong.teamcnpm.teambuilding.data.source.local.room.MyAppDao
import com.phong.teamcnpm.teambuilding.data.source.remote.request.NewEventRequest
import com.phong.teamcnpm.teambuilding.data.source.remote.request.UpdateEventRequest
import com.phong.teamcnpm.teambuilding.data.source.remote.request.UserStateRequest
import com.phong.teamcnpm.teambuilding.data.source.remote.response.EventResponse
import com.phong.teamcnpm.teambuilding.data.source.remote.service.RestfulApi
import com.phong.teamcnpm.teambuilding.domain.entities.Comment
import com.phong.teamcnpm.teambuilding.domain.entities.Event
import com.phong.teamcnpm.teambuilding.domain.entities.Vote
import com.phong.teamcnpm.teambuilding.domain.repository.EventRepository
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.rx3.asObservable
import kotlinx.coroutines.rx3.rxSingle

class EventRepositoryImpl(
  private val api: RestfulApi,
  private val localApi: MyAppDao
) : EventRepository {

  @ExperimentalCoroutinesApi
  override fun observerEventFromLocale(): Observable<List<Event>> {
    return localApi.eventDao()
      .observerEvent()
      .map { it.map(EventEntity::toEvent) }
      .asObservable()
  }

  override fun getEventsFromLocale(): Single<List<Event>> {
    return rxSingle { localApi.eventDao().getEvents().map { it.toEvent() } }
  }

  override fun newEvent(event: Event): Single<Unit> {
    val eventRes = NewEventRequest.fromEvent(event)
    return api.addEventToGroup(eventRes)
  }

  override fun allCommentEvent(eventId: String): Single<List<Comment>> {
    return api.getCommentsByEvent(eventId)
      .map { response -> response.data.map { it.toComment() } }
  }

  override fun getEventsByUser(userId: String): Single<List<Event>> {
    return api.getEventsByUser(userId).map { it.data.map(EventResponse::toEvent) }
  }

  override fun getEventsInGroup(groupId: String): Single<List<Event>> {
    return api.getEventsInGroup(groupId).map { it.data.map(EventResponse::toEvent) }
  }

  override fun getPaginationEvent(
    groupId: String,
    currentPage: Int,
    pageSize: Int
  ): Single<List<Event>> {
    return api.getPaginationEvent(groupId, currentPage, pageSize)
      .map { it.data.map(EventResponse::toEvent) }
  }

  override fun saveEventsToLocale(events: List<Event>): Single<Unit> {
    return rxSingle { localApi.eventDao().insertEvents(events.map { EventEntity.fromEvent(it) }) }
  }

  override fun addCommentInEvent(eventId: String, content: String): Single<Unit> {
    return api.addCommentInEvent(eventId, content)
  }

  override fun getAllVoteEvent(eventId: String): Single<List<Vote>> {
    return api.getVoteByEvent(eventId).map { it.data }
  }

  override fun updateEvent(event: Event): Single<Unit> {
    return api.updateEvent(
      UpdateEventRequest(
        event.id, NewEventRequest(
          event.name,
          event.groupId,
          event.users.map { UserStateRequest(it.idUser, it.state) },
          event.description
        )
      )
    )
  }

  override fun deleteEvent(event: Event): Single<Unit> {
    return api.deleteEvent(event.id)
  }

  override fun completeEvent(event: Event): Single<Unit> {
    return api.completeEvent(event.id)
  }

  override fun cancelEvent(event: Event): Single<Unit> {
    return api.cancelEvent(event.id)
  }

  override fun modifyEvent(eventId: String, type: String): Single<Unit> {
    return api.modifyEvent(eventId, type)
  }
}
