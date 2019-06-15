package com.kiryanov.githubapp.adapter.view_holder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kiryanov.githubapp.R
import com.kiryanov.githubapp.model.Repo
import kotlinx.android.synthetic.main.item_user_repo.view.*

class UserRepoViewHolder private constructor(
    itemView: View
): RecyclerView.ViewHolder(itemView) {

    companion object {
        fun create(parent: ViewGroup) = UserRepoViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_user_repo, parent, false)
        )
    }

    fun bind(repo: Repo) {
        itemView.tv_name.text = repo.name
    }
}