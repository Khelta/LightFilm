package com.example.lightfilm.database


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.lightfilm.database.dao.PictureDao

@Database(entities = [PictureModel::class,
                      FilmModel::class,
                      UserFilmModel::class,
                     ], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pictureDao(): PictureDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cool-database-name.db"
                ).createFromAsset("database/prepackaged.db")
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
