package com.kiryanov.githubapp.adapter.view_holder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kiryanov.githubapp.R
import com.kiryanov.githubapp.adapter.NetworkState
import kotlinx.android.synthetic.main.item_network_state.view.*

class NetworkStateViewHolder private constructor(
    itemView: View,
    private val retryCallback: (() -> Unit)? = null
) : RecyclerView.ViewHolder(itemView) {

    companion object {
        fun create(parent: ViewGroup, retryCallback: (() -> Unit)? = null) = NetworkStateViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_network_state, parent, false),
            retryCallback
        )
    }

    fun bind(networkState: NetworkState) = when(networkState.status) {
        NetworkState.Status.SUCCESS -> {}
        NetworkState.Status.LOADING -> loading()
        NetworkState.Status.FAILED -> failed(networkState.message)
        else -> throw IllegalArgumentException("Unknown state")
    }

    private fun loading() {
        itemView.progress_bar.visibility = View.VISIBLE
        itemView.btn_retry.visibility = View.GONE
        itemView.tv_message.visibility = View.GONE
    }

    private fun failed(msg: String?) {
        itemView.progress_bar.visibility = View.GONE
        itemView.btn_retry.visibility = View.VISIBLE
        itemView.btn_retry.setOnClickListener { retryCallback?.invoke() }
        itemView.tv_message.visibility =  if (msg != null) {
            itemView.tv_message.text = msg
            View.VISIBLE
        } else View.GONE
    }
}