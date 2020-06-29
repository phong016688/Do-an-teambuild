package com.phong.teamcnpm.teambuilding.ui.login

import com.phong.teamcnpm.teambuilding.ui.BasePresenter
import com.phong.teamcnpm.teambuilding.ui.BaseView
import io.reactivex.rxjava3.disposables.CompositeDisposable

interface LoginView: BaseView{
    fun onLoginSuccess()
}
interface LoginPresenter : BasePresenter{
    fun login(email: String, password: String)
}
