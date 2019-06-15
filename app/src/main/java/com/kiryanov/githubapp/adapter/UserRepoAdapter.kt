package com.kiryanov.githubapp.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kiryanov.githubapp.adapter.view_holder.UserRepoViewHolder
import com.kiryanov.githubapp.model.Repo

class UserRepoAdapter : RecyclerView.Adapter<UserRepoViewHolder>() {

    private val items = ArrayList<Repo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = UserRepoViewHolder.create(parent)

    override fun onBindViewHolder(holder: UserRepoViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun addAll(items: List<Repo>) {
        this.items.addAll(items)
        notifyDataSetChanged()
    }
}