package com.kiryanov.githubapp.ui.user_info

import com.arellomobile.mvp.InjectViewState
import com.kiryanov.githubapp.data.Repository
import com.kiryanov.githubapp.di.REPOSITORY
import com.kiryanov.githubapp.model.UserMore
import com.kiryanov.githubapp.mvp.BasePresenter
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import org.koin.core.inject
import org.koin.core.qualifier.named

@InjectViewState
class UserInfoPresenter(private val login: String) : BasePresenter<UserInfoView>() {

    private val repository: Repository by inject(named(REPOSITORY))

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        loadingUserInfo()
    }

    private fun loadingUserInfo() {
        repository.getUserInfo(login).subscribe(object : SingleObserver<UserMore> {
            override fun onSubscribe(d: Disposable) {
                unsubscribeOnDestroy(d)
                viewState.setLoadingProgressVisibility(true)
            }

            override fun onSuccess(user: UserMore) {
                viewState.setUserInfo(user)
                viewState.setLoadingProgressVisibility(false)
            }

            override fun onError(e: Throwable) {
                viewState.showLoadingError()
            }
        })
    }
}