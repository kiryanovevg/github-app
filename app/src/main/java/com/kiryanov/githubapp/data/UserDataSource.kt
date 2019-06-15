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
import kotlin.reflect.KFunction2

class UserDataSource(
    private val request: KFunction2<
            @ParameterName(name = "since") Long,
            @ParameterName(name = "perPage") Int,
            Single<List<User>>>,
    private val toUserId: Long
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
        if (action == null) retryCompletable = null
        else retryCompletable = Completable.fromAction(action)
    }

    override fun loadInitial(params: LoadInitialParams<Long>, callback: LoadInitialCallback<User>) {
        val since = params.requestedInitialKey ?: throw IllegalArgumentException("requestedInitialKey not added")
        val size = params.requestedLoadSize

        initialState?.invoke(NetworkState.LOADING)
        disposables.add(request.invoke(since, size).subscribe(
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
        val since = params.key
        val size = params.requestedLoadSize

        if (since > toUserId) {
            callback.onResult(emptyList())
            return
        }

        loadingState?.invoke(NetworkState.LOADING)
        disposables.add(request.invoke(since, size).subscribe(
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
}