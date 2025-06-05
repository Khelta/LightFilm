package com.example.lightfilm.database


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.lightfilm.database.converter.ContrastConverter
import com.example.lightfilm.database.converter.FilmBrandConverter
import com.example.lightfilm.database.converter.FilmTypeConverter
import com.example.lightfilm.database.converter.GrainConverter
import com.example.lightfilm.database.dao.FilmDao
import com.example.lightfilm.database.dao.PictureDao
import com.example.lightfilm.database.dao.UserFilmDao

@Database(
    version = 2,
    entities = [
        PictureModel::class,
        FilmModel::class,
        UserFilmModel::class,
    ],
)
@TypeConverters(
    FilmTypeConverter::class,
    FilmBrandConverter::class,
    GrainConverter::class,
    ContrastConverter::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pictureDao(): PictureDao
    abstract fun filmDao(): FilmDao
    abstract fun userFilmDao(): UserFilmDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cool-database-name.db"
                ).createFromAsset(
                    "database/prepackaged.db"
                ).addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
