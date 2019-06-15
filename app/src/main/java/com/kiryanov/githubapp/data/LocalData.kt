package com.kiryanov.githubapp.data

import android.content.SharedPreferences

class LocalData(private val sp: SharedPreferences) {

    companion object {
        private const val FROM_ID = "from_id"
        private const val TO_ID = "to_id"
        private const val INITIAL_SIZE = "init_size"
        private const val PAGE_SIZE = "page_size"
    }

    var fromUserId: Long
        get() = sp.getLong(FROM_ID, 0)
        set(value) = sp.edit().putLong(FROM_ID, value).apply()

    var toUserId: Long
        get() = sp.getLong(TO_ID, 300)
        set(value) = sp.edit().putLong(TO_ID, value).apply()

    var initialSize: Int
        get() = sp.getInt(INITIAL_SIZE, 50)
        set(value) = sp.edit().putInt(INITIAL_SIZE, value).apply()

    var pageSize: Int
        get() = sp.getInt(PAGE_SIZE, 10)
        set(value) = sp.edit().putInt(PAGE_SIZE, value).apply()
}