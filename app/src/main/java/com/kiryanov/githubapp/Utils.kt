package com.kiryanov.githubapp

import android.view.View

const val LOGIN = "login_key"
const val TRANSITION_ITEM = "transition_item"

fun View.visibility(visibility: Boolean) {
    this.visibility = if (visibility) View.VISIBLE else View.GONE
}