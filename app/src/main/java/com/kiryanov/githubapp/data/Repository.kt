package com.kiryanov.githubapp.data

import com.kiryanov.githubapp.model.Repo
import com.kiryanov.githubapp.model.User
import com.kiryanov.githubapp.model.UserMore
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class Repository(private val api: Api) {

    fun getUsersByPage(since: Long, perPage: Int, query: String): Single<List<User>> = api
        .getUsers(since, perPage)
        .delay(1, TimeUnit.SECONDS)
        .map { it.filter { user -> user.login.contains(query) || query.contains(user.login)} }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    fun getUsers(
        toUserId: Long, query: String
    ): UserDataSource = UserDataSource(
        this, toUserId, query
    )

    fun getUserInfo(login: String): Single<UserMore> = Single
        .zip<UserMore, List<Repo>, UserMore>(
            api.getUser(login),
            api.getUserRepos(login),
            BiFunction { t1, t2 -> t1.apply { repos = t2 } }
        )
        .delay(1, TimeUnit.SECONDS)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}