package com.kiryanov.githubapp.adapter

import androidx.annotation.IntDef

class NetworkState private constructor(
    @Status val status: Int,
    val message: String? = null
) {
    companion object {
        val SUCCESS = NetworkState(Status.SUCCESS)
        val LOADING = NetworkState(Status.LOADING)
        fun error(message: String? = null) = NetworkState(Status.FAILED, message)
    }

    @IntDef(Status.LOADING, Status.SUCCESS, Status.FAILED)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Status {
        companion object {
            const val LOADING = 0
            const val SUCCESS = 1
            const val FAILED = 2
        }
    }
}