package com.phong.teamcnpm.teambuilding.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.phong.teamcnpm.teambuilding.R
import com.phong.teamcnpm.teambuilding.domain.repository.AuthenticationRepository
import com.phong.teamcnpm.teambuilding.ui.login.LoginActivity
import com.phong.teamcnpm.teambuilding.utils.extensions.addTo
import com.phong.teamcnpm.teambuilding.utils.extensions.gone
import com.phong.teamcnpm.teambuilding.utils.extensions.show
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.layout_base.*
import org.koin.android.ext.android.inject

abstract class BaseActivity : AppCompatActivity() {
  val compositeDisposable = CompositeDisposable()
  private var mCompositeDisposable = CompositeDisposable()
  private val mAuthenticationRepository: AuthenticationRepository by inject()

  @LayoutRes
  abstract fun mainContentView(): Int

  override fun onCreate(savedInstanceState: Bundle?) {
    Log.d("#####BASE", "onCreate ${this::class.java.simpleName}")
    super.onCreate(savedInstanceState)
    setContentView(R.layout.layout_base)
    val layout = LayoutInflater.from(this).inflate(mainContentView(), null)
    mainContentLayout.addView(layout)
  }

  override fun onStart() {
    Log.d("#####BASE", "onStart ${this::class.java.simpleName}")
    super.onStart()
    observeToken()
  }

  override fun onStop() {
    mCompositeDisposable.clear()
    super.onStop()
  }

  override fun onDestroy() {
    Log.d("#####", "onDestroy ${this::class.java.simpleName}")
    compositeDisposable.clear()
    super.onDestroy()
  }

  private fun observeToken() {
    mAuthenticationRepository.observeLogout()
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .onErrorReturn { Unit }
      .subscribe {
        logout()
      }.addTo(mCompositeDisposable)
  }

  private fun logout() {
    if (this is LoginActivity) return
    val intent = LoginActivity.getInstance(this, true)
    startActivity(intent)
  }

  companion object {
    private val TAG = this::class.java.simpleName
  }
}