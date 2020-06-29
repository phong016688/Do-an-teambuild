package com.phong.teamcnpm.teambuilding.ui.main.setting

import com.phong.teamcnpm.teambuilding.domain.repository.AuthenticationRepository
import com.phong.teamcnpm.teambuilding.utils.extensions.addTo
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class SettingPresenterImpl(
  private val view: SettingView,
  private val authRepo: AuthenticationRepository
) : SettingPresenter {
  override fun observeProfileCurrentUser() {
    authRepo.observeCurrentUserFormLocale()
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({
        view.getProfileSuccess(it)
      }, {
        view.onGetError(it)
      }).addTo(view.disposables)
  }

  override fun logout() {
    authRepo.logout().subscribe()
  }

  override fun onStart() = Unit

  override fun onStop() = Unit

  override fun onDestroy() = Unit
}