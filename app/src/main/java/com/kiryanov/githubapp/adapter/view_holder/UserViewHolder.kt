package com.kiryanov.githubapp.adapter.view_holder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kiryanov.githubapp.R
import com.kiryanov.githubapp.model.User
import kotlinx.android.synthetic.main.item_user.view.*

class UserViewHolder private constructor(
    itemView: View,
    private val onItemClickListener: ((User, View) -> Unit)? = null
) : RecyclerView.ViewHolder(itemView) {
    companion object {
        fun create(
            parent: ViewGroup,
            onItemClickListener: ((User, View) -> Unit)? = null
        ) = UserViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false),
            onItemClickListener
        )
    }

    fun bind(user: User) {
        itemView.setOnClickListener { onItemClickListener?.invoke(user, itemView) }
        itemView.tv_username.text = user.login

        Glide.with(itemView.context)
            .load(user.avatarUrl)
            .apply(RequestOptions.circleCropTransform())
            .into(itemView.iv_avatar)

    }
}