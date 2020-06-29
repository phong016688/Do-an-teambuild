package com.phong.teamcnpm.teambuilding.ui.main.setting

import com.phong.teamcnpm.teambuilding.domain.entities.User
import com.phong.teamcnpm.teambuilding.ui.BasePresenter
import com.phong.teamcnpm.teambuilding.ui.BaseView

interface SettingPresenter : BasePresenter {
  fun observeProfileCurrentUser()
  fun logout()
}

interface SettingView : BaseView {
  fun getProfileSuccess(user: User)
}