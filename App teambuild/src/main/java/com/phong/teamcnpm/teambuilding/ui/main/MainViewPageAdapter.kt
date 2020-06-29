package com.phong.teamcnpm.teambuilding.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.phong.teamcnpm.teambuilding.R
import com.phong.teamcnpm.teambuilding.ui.main.event.FragmentEvent
import com.phong.teamcnpm.teambuilding.ui.main.group.FragmentGroup
import com.phong.teamcnpm.teambuilding.ui.main.home.FragmentHome
import com.phong.teamcnpm.teambuilding.ui.main.setting.FragmentSetting


class MainViewPageAdapter(
  fragmentManager: FragmentManager,
  lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {

  override fun getItemCount(): Int = FRAGMENT_COUNT

  override fun createFragment(position: Int): Fragment {
    return when (position) {
      0 -> FragmentHome.newInstance()
      1 -> FragmentGroup.newInstance()
      2 -> FragmentEvent.newInstance()
      3 -> FragmentSetting.newInstance()
      else -> Fragment()
    }
  }

  companion object {
    const val FRAGMENT_COUNT = 4
  }
}