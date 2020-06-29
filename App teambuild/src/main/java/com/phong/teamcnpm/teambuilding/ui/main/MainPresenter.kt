package com.phong.teamcnpm.teambuilding.ui.main

import android.util.Log
import com.phong.teamcnpm.teambuilding.data.source.local.preference.SharedPrefsApi
import com.phong.teamcnpm.teambuilding.data.source.local.preference.SharedPrefsKey
import com.phong.teamcnpm.teambuilding.domain.repository.EventRepository
import com.phong.teamcnpm.teambuilding.domain.repository.GroupRepository
import com.phong.teamcnpm.teambuilding.utils.extensions.addTo
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class MainPresenterImpl(
  private val view: MainView,
  private val groupRepo: GroupRepository,
  private val eventRepo: EventRepository,
  private val prefsApi: SharedPrefsApi
) : MainPresenter {

  override fun getGroupAndEvent() {
    getGroupContainsUser()
    getEventsContainsUser()
  }

  private fun getEventsContainsUser() {
    val userId = prefsApi.get(SharedPrefsKey.CURRENT_USER_ID, String::class.java)
    if (userId.isEmpty()) return
    eventRepo.getEventsByUser(userId)
      .flatMap {
        prefsApi.put(SharedPrefsKey.HOME_FRAGMENT_1, it.size)
        eventRepo.saveEventsToLocale(it)
      }
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({
      }, {
      })
      .addTo(view.disposables)
  }

  private fun getGroupContainsUser() {
    val groupId = prefsApi.get(SharedPrefsKey.CURRENT_GROUP_ID, String::class.java)
    if (groupId.isEmpty()) return
    groupRepo.getGroupById(groupId)
      .flatMap {
        prefsApi.put(SharedPrefsKey.HOME_FRAGMENT_2, it.eventCount)
        groupRepo.saveGroupToLocale(it)
      }
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({
        Log.d("####", "group success")
      }, {
        Log.d("####", "error: ${it.message.toString()}")
      })
      .addTo(view.disposables)
  }

  override fun onStart() = Unit

  override fun onStop() = Unit

  override fun onDestroy() = Unit
}