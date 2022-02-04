package ru.edinros.agitator.core.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.edinros.agitator.core.local.dao.AppDao

abstract class AppDB : RoomDatabase() {
    abstract fun dao(): AppDao
    companion object {
        @Volatile
        private var instance: AppDB? = null
        fun getInstance(context: Context): AppDB {
            return instance ?: synchronized(this) {
                instance
                    ?: buildDatabase(
                        context
                    ).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDB {
            return Room.databaseBuilder(context, AppDB::class.java, DB_NAME)
                .fallbackToDestructiveMigration()
                .build()
        }

        private const val DB_NAME = "agitator.db"
    }
}