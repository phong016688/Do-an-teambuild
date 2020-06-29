package com.phong.teamcnpm.teambuilding.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.jakewharton.rxbinding4.material.selectionEvents
import com.jakewharton.rxbinding4.viewpager2.pageScrollEvents
import com.phong.teamcnpm.teambuilding.R
import com.phong.teamcnpm.teambuilding.service.ObserveSocketService
import com.phong.teamcnpm.teambuilding.ui.BaseActivity
import com.phong.teamcnpm.teambuilding.utils.extensions.addTo
import com.phong.teamcnpm.teambuilding.utils.extensions.show
import com.phong.teamcnpm.teambuilding.utils.extensions.toast
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_base.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.lang.reflect.Field
import java.util.concurrent.TimeUnit


class MainActivity : BaseActivity(), MainView {
  private val mainPresenter by inject<MainPresenter> { parametersOf(this) }
  private lateinit var mAdapterViewPage: MainViewPageAdapter

  override fun mainContentView(): Int = R.layout.activity_main

  override val disposables: CompositeDisposable
    get() = compositeDisposable

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    startService(Intent(this, ObserveSocketService::class.java))
    mainPresenter.getGroupAndEvent()
    initView()
  }

  private fun initView() {
    setupViewPages()
    setupToolBar()
    val mFlingDistance: Field
    val mMinimumVelocity: Field

    try {
      mFlingDistance = ViewPager::class.java.getDeclaredField("mFlingDistance")
      mMinimumVelocity = ViewPager::class.java.getDeclaredField("mMinimumVelocity")
      mFlingDistance.isAccessible = true
      mMinimumVelocity.isAccessible = true

      //customize the values
      mFlingDistance.set(viewPager, 10)
      mMinimumVelocity.set(viewPager, 400)
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  private fun setupToolBar() {
    toolbarView.show()
  }

  private fun setupViewPages() {
    mAdapterViewPage = MainViewPageAdapter(
      supportFragmentManager,
      lifecycle
    )
    viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
    viewPager.adapter = mAdapterViewPage

    tabs.getTabAt(2)?.orCreateBadge?.number = 5
    tabs.getTabAt(2)?.orCreateBadge?.isVisible = true
    tabs.getTabAt(1)?.orCreateBadge?.number = 5
    tabs.getTabAt(1)?.orCreateBadge?.isVisible = true

    viewPager.pageScrollEvents()
      .subscribe {
        tabs.getTabAt(it.viewPager2.currentItem)?.select()
      }.addTo(compositeDisposable)
    tabs.selectionEvents()
      .debounce(50L, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
      .subscribe {
        it.tab.badge?.isVisible = false
        viewPager.currentItem = it.tab.position
        toolbarView.title = it.tab.text
      }.addTo(compositeDisposable)
  }

  override fun onGetError(throwable: Throwable) {
    toast(throwable.message.toString())
    Log.d(TAG, throwable.message.toString())
  }
  companion object {
    private val TAG = this::class.java.simpleName
    fun getInstance(context: Context, isSingleTop: Boolean = false): Intent {
      return if (isSingleTop) {
        Intent(
          context,
          MainActivity::class.java
        ).apply { flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK }
      } else {
        Intent(context, MainActivity::class.java)
      }
    }
  }
}
