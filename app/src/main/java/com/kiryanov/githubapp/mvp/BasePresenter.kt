package com.kiryanov.githubapp.mvp

import com.arellomobile.mvp.MvpPresenter
import com.arellomobile.mvp.MvpView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.koin.standalone.KoinComponent

open class BasePresenter<View : MvpView> : MvpPresenter<View>(), KoinComponent {

    private val mCompositeDisposable = CompositeDisposable()

    protected open fun unsubscribeOnDestroy(disposable: Disposable) =
        mCompositeDisposable.add(disposable)

    override fun onDestroy() {
        mCompositeDisposable.clear()
        super.onDestroy()
    }
}