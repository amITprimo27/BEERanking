package com.example.beeranking.dao

import androidx.room.Room
import com.example.beeranking.base.MyApplication

object AppLocalDB {
    val db: AppLocalDbRepository by lazy {

        val context = MyApplication.appContext
            ?: throw IllegalStateException("Context is null")

        Room.databaseBuilder(
            context = context,
            klass = AppLocalDbRepository::class.java,
            name = "beeranking.db"
        )
        .fallbackToDestructiveMigration(true)
        .build()
    }
}