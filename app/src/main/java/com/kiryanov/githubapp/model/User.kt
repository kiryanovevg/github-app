package com.kiryanov.githubapp.model

import androidx.recyclerview.widget.DiffUtil
import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("avatar_url") val avatarUrl: String,
    val id: Long,
    val login: String
) {
    companion object {
        val diffUtil get() = object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(oldItem: User, newItem: User): Boolean = oldItem === newItem

            override fun areContentsTheSame(oldItem: User, newItem: User): Boolean = oldItem.id == newItem.id
        }
    }
}

data class UserMore(
    @SerializedName("avatar_url") val avatarUrl: String,
    @SerializedName("public_repos") val repositories: Int,
    val id: Long,
    val login: String,
    val name: String,
    val location: String?,
    val company: String?,
    val followers: Int,
    val following: Int,
    var repos: List<Repo> = emptyList()
)
data class Repo(
    @SerializedName("full_name") val name: String
)