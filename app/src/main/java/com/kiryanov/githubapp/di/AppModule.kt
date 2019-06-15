package com.kiryanov.githubapp.di

import android.content.Context
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.kiryanov.githubapp.data.Api
import com.kiryanov.githubapp.data.LocalData
import com.kiryanov.githubapp.data.Repository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

const val APP_CONTEXT = "app_context"
const val REPOSITORY = "repository"
const val HTTP_CLIENT = "http_client"
const val GSON = "gson"
const val RETROFIT = "retrofit"
const val SHARED_PREFERENCES = "shared_prefs"

fun createMainModule(context: Context) = module {

    single(name = APP_CONTEXT) { context }

    single(name = REPOSITORY) { Repository(get(RETROFIT)) }

    single(name = SHARED_PREFERENCES) { LocalData(context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)) }
}

fun createNetworkModule(baseUrl: String) = module {

    //HttpClient
    single(name = HTTP_CLIENT) {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        OkHttpClient.Builder()
            .addInterceptor(logging)
            .callTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    //Gson
    single(name = GSON) {
        GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()
//            .registerTypeAdapter()
//            .disableHtmlEscaping()
    }

    //Retrofit
    single(name = RETROFIT) {
        Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(get(GSON)))
            .baseUrl(baseUrl)
            .client(get(HTTP_CLIENT))
            .build()
            .create(Api::class.java)
    }
}