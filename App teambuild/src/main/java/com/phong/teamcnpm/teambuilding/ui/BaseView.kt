package com.phong.teamcnpm.teambuilding.ui

import io.reactivex.rxjava3.disposables.CompositeDisposable

interface BaseView {
  val disposables: CompositeDisposable
  fun onGetError(throwable: Throwable)
  fun <T> onLoading(value: T) = Unit
  fun onComplete() = Unit
}