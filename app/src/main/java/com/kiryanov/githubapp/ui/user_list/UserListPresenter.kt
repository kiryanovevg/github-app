package com.kiryanov.githubapp.ui.user_list

import android.os.Handler
import android.os.Looper
import androidx.paging.PagedList
import com.arellomobile.mvp.InjectViewState
import com.kiryanov.githubapp.adapter.NetworkState
import com.kiryanov.githubapp.data.LocalData
import com.kiryanov.githubapp.data.Repository
import com.kiryanov.githubapp.data.UserDataSource
import com.kiryanov.githubapp.model.User
import com.kiryanov.githubapp.mvp.BasePresenter
import org.koin.standalone.inject

@InjectViewState
class UserListPresenter : BasePresenter<UserListView>() {

    private val repository: Repository by inject()
    private val localData: LocalData by inject()
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var dataSource: UserDataSource

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        loadUsers()
    }

    fun loadUsers() {
        dataSource = repository.getUsers(localData.toUserId)
        unsubscribeOnDestroy(dataSource.disposables)
        handleInitialState()
        handleLoadingState()

        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(localData.pageSize)
            .setInitialLoadSizeHint(localData.initialSize)
            .build()

        val pagedList = PagedList.Builder<Long, User>(dataSource, config)
            .setFetchExecutor { handler.post(it) }
            .setNotifyExecutor { handler.post(it) }
            .setInitialKey(localData.fromUserId)
            .build()

        viewState.setUserList(pagedList)
    }

    fun loadRetry() {
        dataSource.loadRetry()
    }

    private fun handleInitialState() {
        dataSource.initialState = { when(it.status) {
            NetworkState.Status.LOADING -> {
                viewState.setLoadingState(null)
                viewState.setRefreshing(true)
                viewState.setRefreshingErrorVisibility(false)
            }
            NetworkState.Status.SUCCESS -> {
                viewState.setRefreshing(false)
            }
            NetworkState.Status.FAILED -> {
                viewState.setRefreshing(false)
                viewState.setRefreshingErrorVisibility(true, it.message)
            }
        } }
    }

    private fun handleLoadingState() {
        dataSource.loadingState = { viewState.setLoadingState(it) }
    }
}