package com.phong.teamcnpm.teambuilding.ui.main.group

import com.phong.teamcnpm.teambuilding.domain.entities.Group
import com.phong.teamcnpm.teambuilding.ui.BasePresenter
import com.phong.teamcnpm.teambuilding.ui.BaseView

interface GroupPresenter : BasePresenter {
  fun observeGroupFromLocale()
  fun getGroupByUser()
}

interface GroupView : BaseView {
  fun getGroupSuccess(group: Group)
}