package com.kiryanov.githubapp.data

import androidx.paging.ItemKeyedDataSource
import com.kiryanov.githubapp.adapter.NetworkState
import com.kiryanov.githubapp.model.User
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import io.reactivex.schedulers.Schedulers

class UserDataSource(
    private val repository: Repository,
    private val toUserId: Long,
    private val query: String
) : ItemKeyedDataSource<Long, User>() {

    var initialState: ((NetworkState) -> Unit)? = null
    var loadingState: ((NetworkState) -> Unit)? = null
    val disposables = CompositeDisposable()

    private var retryCompletable: Completable? = null

    fun loadRetry() {
        retryCompletable?.let {
            it.subscribeOn(Schedulers.io())
            it.observeOn(AndroidSchedulers.mainThread())
            it.subscribe()
        }?.also { disposables.add(it) }
    }

    private fun setRetryAction(action: Action?) {
        retryCompletable = action?.let { Completable.fromAction(it) }
    }

    override fun loadInitial(params: LoadInitialParams<Long>, callback: LoadInitialCallback<User>) {
        val size = params.requestedLoadSize
        val since = params.requestedInitialKey
            ?: throw IllegalArgumentException("requestedInitialKey not added")

        initialState?.invoke(NetworkState.LOADING)
        disposables.add(request(since,size).subscribe(
            { users ->
                setRetryAction(null)
                callback.onResult(users.filter { user -> user.id <= toUserId })
                loadingState?.invoke(NetworkState.SUCCESS)
                initialState?.invoke(NetworkState.SUCCESS)
            },
            { throwable ->
                setRetryAction(Action { loadInitial(params, callback) })
                initialState?.invoke(NetworkState.error(throwable.message))
            }
        ))
    }

    override fun loadAfter(params: LoadParams<Long>, callback: LoadCallback<User>) {
        val size = params.requestedLoadSize
        val since = params.key

        if (since > toUserId) {
            callback.onResult(emptyList())
            return
        }

        loadingState?.invoke(NetworkState.LOADING)
        disposables.add(request(since,size).subscribe(
            { users ->
                setRetryAction(null)
                callback.onResult(users.filter { user -> user.id <= toUserId })
                loadingState?.invoke(NetworkState.SUCCESS)
            },
            { throwable ->
                setRetryAction(Action { loadAfter(params, callback) })
                loadingState?.invoke(NetworkState.error(throwable.message))
            }
        ))
    }

    override fun loadBefore(params: LoadParams<Long>, callback: LoadCallback<User>) {
        //Not needed
    }

    override fun getKey(item: User): Long  = item.id

    private fun request(since: Long, size: Int): Single<List<User>> =
        repository.getUsersByPage(since, size, query)
}