package com.example.thinkingdobby.fishtracker.data

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

@Database(entities = [Fish::class], version = 5)
abstract class FishDB : RoomDatabase() {
    abstract fun fishDao(): FishDao

    companion object {
        private var INSTANCE: FishDB? = null

        fun getInstance(context: Context): FishDB? {
            if (INSTANCE == null) {
                synchronized(FishDB::class) {
                    INSTANCE = Room.databaseBuilder(
                            context.applicationContext,
                            FishDB::class.java, "Fish.db"
                    )
                            .fallbackToDestructiveMigration()
                            .build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}
