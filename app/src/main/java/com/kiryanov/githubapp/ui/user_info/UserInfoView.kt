package com.kiryanov.githubapp.ui.user_info

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.kiryanov.githubapp.model.UserMore

@StateStrategyType(AddToEndSingleStrategy::class)
interface UserInfoView : MvpView {

    fun setUserInfo(user: UserMore)
    fun setLoadingProgressVisibility(visibility: Boolean)
    fun showLoadingError()
}