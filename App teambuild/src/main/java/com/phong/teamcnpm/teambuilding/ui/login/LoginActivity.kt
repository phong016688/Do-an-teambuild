package com.phong.teamcnpm.teambuilding.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.jakewharton.rxbinding4.view.clicks
import com.phong.teamcnpm.teambuilding.R
import com.phong.teamcnpm.teambuilding.ui.BaseActivity
import com.phong.teamcnpm.teambuilding.ui.main.MainActivity
import com.phong.teamcnpm.teambuilding.utils.extensions.*
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_login.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.util.concurrent.TimeUnit

class LoginActivity : BaseActivity(), LoginView {
  private val presenter: LoginPresenter by inject { parametersOf(this) }

  override fun mainContentView(): Int = R.layout.activity_login

  override val disposables: CompositeDisposable
    get() = compositeDisposable

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    handleEvents()
  }

  private fun handleEvents() {
    loginButton.clicks()
      .debounce(200L, TimeUnit.MILLISECONDS)
      .filter { usernameField.text.isNotEmpty() || passwordField.text.isNotEmpty() }
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({
        presenter.login(
          usernameField.text.toString(),
          passwordField.text.toString()
        )
        hideSoftKeyboardFromWindow(loginButton)
        loginProgressBar.show()
      }, {
        Log.d(this::class.simpleName.toString(), it.message.toString())
      }).addTo(compositeDisposable)

    imageView.clicks()
      .debounce(1L, TimeUnit.SECONDS)
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe {
        hideSoftKeyboardFromWindow(imageView)
      }.addTo(compositeDisposable)
  }

  override fun onLoginSuccess() {
    loginProgressBar.hide()
    MainActivity.getInstance(this).also {
      startActivity(it)
    }
    finish()
  }

  override fun onGetError(throwable: Throwable) {
    loginProgressBar.gone()
    toast("Email or password invalid")
    Log.d(this::class.simpleName.toString(), throwable.message.toString())
  }

  companion object {
    fun getInstance(context: Context, isClearTop: Boolean = false): Intent =
      Intent(context, LoginActivity::class.java).apply {
        if (isClearTop) {
          addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
      }
  }
}
