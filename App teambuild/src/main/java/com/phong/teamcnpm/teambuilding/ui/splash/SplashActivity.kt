package com.phong.teamcnpm.teambuilding.ui.splash

import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.phong.teamcnpm.teambuilding.R
import com.phong.teamcnpm.teambuilding.data.source.local.preference.SharedPrefsApi
import com.phong.teamcnpm.teambuilding.data.source.local.preference.SharedPrefsKey
import com.phong.teamcnpm.teambuilding.data.source.local.room.MyAppDao
import com.phong.teamcnpm.teambuilding.domain.repository.AuthenticationRepository
import com.phong.teamcnpm.teambuilding.ui.login.LoginActivity
import com.phong.teamcnpm.teambuilding.ui.main.MainActivity
import com.phong.teamcnpm.teambuilding.utils.extensions.addTo
import com.phong.teamcnpm.teambuilding.utils.extensions.toast
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.rx3.rxSingle
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import kotlin.coroutines.CoroutineContext

class SplashActivity : AppCompatActivity() {
  private val mCompositeDisposable = CompositeDisposable()
  private val mAuthenticationRepository: AuthenticationRepository by inject()
  private val mSharedPreferences: SharedPrefsApi by inject()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_splash)
    val anim = AnimationUtils.loadAnimation(this, R.anim.rotate_360)
    imageView.startAnimation(anim)
    mAuthenticationRepository.syncDataCurrentUser()
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({
        startActivity(MainActivity.getInstance(this, true))
        finish()
      }, {
        toast(it.message.toString())
        if (it.message == "HTTP 403 Forbidden" || mSharedPreferences.get(
            SharedPrefsKey.KEY_TOKEN,
            String::class.java
          ).isEmpty()
        ) {
          startActivity(LoginActivity.getInstance(this, true))
          finish()
        } else {
          startActivity(MainActivity.getInstance(this, true))
          finish()
        }
      }).addTo(mCompositeDisposable)
  }

  override fun onDestroy() {
    mCompositeDisposable.clear()
    super.onDestroy()
  }

  companion object {
    var TAG: String = SplashActivity::class.java.simpleName
    private const val TIME_DELAY = 1500L
  }
}