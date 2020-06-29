package com.phong.teamcnpm.teambuilding.domain.repository

import com.phong.teamcnpm.teambuilding.domain.entities.Comment
import com.phong.teamcnpm.teambuilding.domain.entities.Event
import com.phong.teamcnpm.teambuilding.domain.entities.Vote
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface EventRepository {
  fun getEventsFromLocale(): Single<List<Event>>
  fun observerEventFromLocale(): Observable<List<Event>>
  fun saveEventsToLocale(events: List<Event>): Single<Unit>
  fun getEventsByUser(userId: String): Single<List<Event>>
  fun newEvent(event: Event): Single<Unit>
  fun allCommentEvent(eventId: String): Single<List<Comment>>
  fun getAllVoteEvent(eventId: String): Single<List<Vote>>
  fun getEventsInGroup(groupId: String): Single<List<Event>>
  fun addCommentInEvent(eventId: String, content: String): Single<Unit>
  fun getPaginationEvent(
    groupId: String,
    currentPage: Int = 1,
    pageSize: Int = 20
  ): Single<List<Event>>

  fun updateEvent(event: Event): Single<Unit>
  fun deleteEvent(event: Event): Single<Unit>
  fun completeEvent(event: Event): Single<Unit>
  fun cancelEvent(event: Event): Single<Unit>
  fun modifyEvent(eventId: String, type: String): Single<Unit>
}