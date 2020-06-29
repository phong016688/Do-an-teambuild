package com.phong.teamcnpm.teambuilding.data.source.remote.service

import com.phong.teamcnpm.teambuilding.data.BaseResponse
import com.phong.teamcnpm.teambuilding.data.source.remote.request.NewEventRequest
import com.phong.teamcnpm.teambuilding.data.source.remote.request.UpdateEventRequest
import com.phong.teamcnpm.teambuilding.data.source.remote.response.*
import com.phong.teamcnpm.teambuilding.domain.entities.User
import com.phong.teamcnpm.teambuilding.domain.entities.Vote
import io.reactivex.rxjava3.core.Single
import retrofit2.http.*

interface RestfulApi {
  @POST("auth/login")
  @FormUrlEncoded
  fun login(
    @Field("email") username: String,
    @Field("password") password: String
  ): Single<LoginResponse>

  @POST("event/add-event")
  fun addEventToGroup(@Body eventRequest: NewEventRequest): Single<Unit>

  @POST("event/update-event")
  fun updateEvent(@Body updateEventRequest: UpdateEventRequest): Single<Unit>

  @POST("event/delete-event")
  @FormUrlEncoded
  fun deleteEvent(@Field("_id") id: String): Single<Unit>

  @POST("event/complete-event")
  @FormUrlEncoded
  fun completeEvent(@Field("_id") eventId: String): Single<Unit>

  @POST("event/cancel-event")
  @FormUrlEncoded
  fun cancelEvent(@Field("_id") eventId: String): Single<Unit>

  @POST("feedback/add-feedback")
  @FormUrlEncoded
  fun addCommentInEvent(
    @Field("idEvent") eventId: String,
    @Field("content") content: String
  ): Single<Unit>

  @POST("vote/modifyvote")
  @FormUrlEncoded
  fun modifyEvent(@Field("idEvent") eventId: String, @Field("type") type: String): Single<Unit>

  @GET("user/profile")
  fun getProfileCurrentUser(): Single<BaseResponse<ProfileResponse>>

  @GET("group/group")
  fun getGroupById(@Query("id") groupId: String): Single<BaseResponse<GroupResponse>>

  @GET("feedback/feedback-by-event")
  fun getCommentsByEvent(@Query("id") eventId: String): Single<BaseResponse<List<CommentResponse>>>

  @GET("event/get-event-by-user")
  fun getEventsByUser(@Query("id") userId: String): Single<BaseResponse<List<EventResponse>>>

  @GET("event/get-event-by-group")
  fun getEventsInGroup(@Query("id") groupId: String): Single<BaseResponse<List<EventResponse>>>

  @GET("event/pagination-event")
  fun getPaginationEvent(
    @Query("idGroup") groupId: String,
    @Query("currentPage") currentPage: Int,
    @Query("pageSize") pageSize: Int
  ): Single<BaseResponse<List<EventResponse>>>

  @POST("group/get-user-in-group")
  fun getUserInGroup(@Query("id") groupId: String): Single<BaseResponse<List<User>>>

  @GET("vote/get-all-vote-by-event")
  fun getVoteByEvent(@Query("id") eventId: String): Single<BaseResponse<List<Vote>>>
}