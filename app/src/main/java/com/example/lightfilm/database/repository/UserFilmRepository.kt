package com.example.lightfilm.database.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.lightfilm.database.AppDatabase
import com.example.lightfilm.database.UserFilmModel
import com.example.lightfilm.database.dao.UserFilmDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserFilmRepository(application: Application) {
    private val userFilmDao: UserFilmDao = AppDatabase.getDatabase(application).userFilmDao()
    val allUserFilms: LiveData<List<UserFilmModel>> = userFilmDao.getAll()

    suspend fun insert(userFilm: UserFilmModel) {
        withContext(Dispatchers.IO) {
            userFilmDao.insert(userFilm)
        }
    }

    suspend fun update(userFilm: UserFilmModel) {
        withContext(Dispatchers.IO) {
            userFilmDao.update(userFilm)
        }
    }

    suspend fun delete(userFilm: UserFilmModel) {
        withContext(Dispatchers.IO) {
            userFilmDao.delete(userFilm)
        }
    }
}