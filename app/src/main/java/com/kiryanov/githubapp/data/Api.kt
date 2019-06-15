package com.kiryanov.githubapp.data

import com.kiryanov.githubapp.model.Repo
import com.kiryanov.githubapp.model.User
import com.kiryanov.githubapp.model.UserMore
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface Api {

    @Headers("Authorization: token 5b4d3d1b92740358f9fba3eb4887729d74f47c93")
    @GET("/users")
    fun getUsers(
        @Query("since") since: Long,
        @Query("per_page") perPage: Int
    ): Single<List<User>>

    @Headers("Authorization: token 5b4d3d1b92740358f9fba3eb4887729d74f47c93")
    @GET("/users/{login}")
    fun getUser(@Path("login") login: String): Single<UserMore>

    @Headers("Authorization: token 5b4d3d1b92740358f9fba3eb4887729d74f47c93")
    @GET("/users/{login}/repos")
    fun getUserRepos(@Path("login") login: String): Single<List<Repo>>
}