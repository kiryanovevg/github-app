package com.kiryanov.githubapp.adapter

import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kiryanov.githubapp.R
import com.kiryanov.githubapp.adapter.view_holder.NetworkStateViewHolder
import com.kiryanov.githubapp.adapter.view_holder.UserViewHolder
import com.kiryanov.githubapp.model.User

class UserAdapter : PagedListAdapter<User, RecyclerView.ViewHolder>(User.diffUtil) {

    var retryCallback: (() -> Unit)? = null
    var onItemClickListener : ((User, View) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = when(viewType) {
        R.layout.item_user -> UserViewHolder.create(parent, onItemClickListener)
        R.layout.item_network_state -> NetworkStateViewHolder.create(parent, retryCallback)
        else -> throw IllegalArgumentException("Unknown view type")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is UserViewHolder -> getItem(position)?.also { holder.bind(it) }
            is NetworkStateViewHolder -> networkState?.also { holder.bind(it) }
            else -> throw IllegalArgumentException("Unknown type View Holder")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (hasNetworkStateItem && position == itemCount - 1) R.layout.item_network_state
        else R.layout.item_user
    }

    override fun getItemCount(): Int = (super.getItemCount() + if (hasNetworkStateItem) 1 else 0)

    var networkState: NetworkState? = null
        set(newNetworkState) {
            currentList?.also { list ->
                if (list.size != 0) {
                    val prevState = field
                    val hadNetworkStateItem = hasNetworkStateItem
                    field = newNetworkState

                    if (hadNetworkStateItem != hasNetworkStateItem) {
                        if (hadNetworkStateItem) notifyItemRemoved(itemCount)
                        else notifyItemInserted(itemCount)
                    } else if (hasNetworkStateItem && prevState != newNetworkState)
                        notifyItemChanged(itemCount - 1)
                }
            }
        }

    private val hasNetworkStateItem get() = networkState != null && networkState != NetworkState.SUCCESS
}