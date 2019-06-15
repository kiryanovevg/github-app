package com.kiryanov.githubapp.ui.user_list

import androidx.paging.PagedList
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.kiryanov.githubapp.adapter.NetworkState
import com.kiryanov.githubapp.model.User

@StateStrategyType(AddToEndSingleStrategy::class)
interface UserListView : MvpView {

    fun setUserList(pagedList: PagedList<User>)

    fun setRefreshing(loading: Boolean)
    fun setRefreshingErrorVisibility(visibility: Boolean, message: String? = null)

    fun setLoadingState(networkState: NetworkState?)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showSettingsDialog()
}