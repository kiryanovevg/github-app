package com.kiryanov.githubapp.ui.user_list

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.arellomobile.mvp.InjectViewState
import com.kiryanov.githubapp.adapter.NetworkState
import com.kiryanov.githubapp.data.LocalData
import com.kiryanov.githubapp.data.Repository
import com.kiryanov.githubapp.data.UserDataSource
import com.kiryanov.githubapp.di.REPOSITORY
import com.kiryanov.githubapp.di.SHARED_PREFERENCES
import com.kiryanov.githubapp.model.User
import com.kiryanov.githubapp.mvp.BasePresenter
import org.koin.core.inject
import org.koin.core.qualifier.named

@InjectViewState
class UserListPresenter : BasePresenter<UserListView>() {

    private val repository: Repository by inject(named(REPOSITORY))
    private val localData: LocalData by inject(named(SHARED_PREFERENCES))
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var dataSource: UserDataSource

    private var query: String? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        loadUsers()
    }

    fun loadUsers() {
        dataSource = repository.getUsers(localData.toUserId, query ?: "")
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

    fun setSearchQuery(query: String?) {
        this.query = query
        loadUsers()
    }
}