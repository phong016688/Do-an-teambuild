package com.phong.teamcnpm.teambuilding.ui.main.event

import android.util.Log
import com.phong.teamcnpm.teambuilding.data.source.local.preference.SharedPrefsApi
import com.phong.teamcnpm.teambuilding.data.source.local.preference.SharedPrefsKey
import com.phong.teamcnpm.teambuilding.domain.repository.EventRepository
import com.phong.teamcnpm.teambuilding.domain.repository.GroupRepository
import com.phong.teamcnpm.teambuilding.utils.extensions.addTo
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class EventPresenterImpl(
  private val view: EventView,
  private val eventRepo: EventRepository,
  private val prefsApi: SharedPrefsApi
) : EventPresenter {

  override fun observerEventsFromLocale() {
    eventRepo.observerEventFromLocale()
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({
        view.getAllEventsSuccess(it)
      }, {
        view.onGetError(it)
      }).addTo(view.disposables)
  }

  override fun getEventsByUser() {
    val userId = prefsApi.get(SharedPrefsKey.CURRENT_USER_ID, String::class.java)
    if (userId.isEmpty()) return
    eventRepo.getEventsByUser(userId)
      .flatMap { eventRepo.saveEventsToLocale(it) }
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({
        Log.d("####", "success")
      }, {
        Log.d("####", "error: ${it.message.toString()}")
      })
      .addTo(view.disposables)
  }

  override fun onStart() = Unit

  override fun onStop() = Unit

  override fun onDestroy() = Unit
}