package com.phong.teamcnpm.teambuilding.ui.main.group

import android.util.Log
import com.phong.teamcnpm.teambuilding.data.source.local.preference.SharedPrefsApi
import com.phong.teamcnpm.teambuilding.data.source.local.preference.SharedPrefsKey
import com.phong.teamcnpm.teambuilding.domain.repository.GroupRepository
import com.phong.teamcnpm.teambuilding.utils.extensions.addTo
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class GroupPresenterImpl(
  private val view: GroupView,
  private val groupRepo: GroupRepository,
  private val prefsApi: SharedPrefsApi
) : GroupPresenter {

  override fun observeGroupFromLocale() {
    groupRepo.observeGroupFromLocale()
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({
        view.getGroupSuccess(it)
      }, {
        view.onGetError(it)
      })
      .addTo(view.disposables)
  }

  override fun getGroupByUser() {

  }

  override fun onStart() = Unit

  override fun onStop() = Unit

  override fun onDestroy() = Unit
}