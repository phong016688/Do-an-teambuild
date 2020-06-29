package com.phong.teamcnpm.teambuilding.ui.main

import com.phong.teamcnpm.teambuilding.ui.BasePresenter
import com.phong.teamcnpm.teambuilding.ui.BaseView
import io.reactivex.rxjava3.core.Single

interface MainView : BaseView {
}

interface MainPresenter : BasePresenter {
  fun getGroupAndEvent()
}