package com.phong.teamcnpm.teambuilding.ui.login

import com.phong.teamcnpm.teambuilding.domain.repository.AuthenticationRepository
import com.phong.teamcnpm.teambuilding.utils.extensions.addTo
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class LoginPresenterImpl(
    private val view: LoginView,
    private val authRepo: AuthenticationRepository
) : LoginPresenter {

    override fun login(email: String, password: String) {
        authRepo.login(email, password)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                {
                    view.onLoginSuccess()
                }, {
                    view.onGetError(it)
                }
            ).addTo(view.disposables)
    }

    override fun onStart() = Unit

    override fun onStop() = Unit

    override fun onDestroy() = Unit
}
