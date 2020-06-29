package com.phong.teamcnpm.teambuilding.ui.main.setting

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jakewharton.rxbinding4.view.clicks
import com.phong.teamcnpm.teambuilding.R
import com.phong.teamcnpm.teambuilding.domain.entities.User
import com.phong.teamcnpm.teambuilding.utils.extensions.loadFromUrl
import com.phong.teamcnpm.teambuilding.utils.extensions.toast
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_setting.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.util.concurrent.TimeUnit


class FragmentSetting : Fragment(), SettingView {
  private val mPresenter: SettingPresenter by inject { parametersOf(this) }
  private val mCompositeDisposable = CompositeDisposable()

  override val disposables: CompositeDisposable
    get() = mCompositeDisposable

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_setting, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    mPresenter.observeProfileCurrentUser()
    logoutButton.clicks()
      .delay(50L, TimeUnit.MILLISECONDS)
      .subscribe {
        mPresenter.logout()
      }
    Log.d("####", "create ${this::class.java.simpleName}")
  }

  override fun getProfileSuccess(user: User) {
    avatarImageView.loadFromUrl(user.avatar)
    nameTextView.text = user.name
    emailTextView.text = user.email
    phoneTextView.text = user.phoneNumber
    birthdayTextView.text = user.birthday
  }

  override fun onGetError(throwable: Throwable) {
    Log.d(TAG, throwable.message.toString())
  }

  companion object {
    private val TAG = this::class.java.simpleName

    @JvmStatic
    fun newInstance() =
      FragmentSetting().apply {
        arguments = Bundle().apply {}
      }
  }

}