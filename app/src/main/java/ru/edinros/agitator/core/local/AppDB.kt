package ru.edinros.agitator.core.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.edinros.agitator.core.local.dao.AppDao
import ru.edinros.agitator.core.local.entities.TaskEntity

@Database(entities = [TaskEntity::class], version = 2)
@TypeConverters(
    ListOfStringConverter::class,
    TaskLinksConverter::class,
    TaskTypeConverter::class,
    TaskAttachmentsConverter::class,
    AttachmentTypeConverter::class,
    RejectedReportsConverter::class
)
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