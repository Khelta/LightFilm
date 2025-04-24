package com.example.lightfilm.database.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.lightfilm.database.AppDatabase
import com.example.lightfilm.database.FilmModel
import com.example.lightfilm.database.dao.FilmDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FilmRepository(application: Application) {
    private val filmDao: FilmDao = AppDatabase.getDatabase(application).filmDao()
    val allFilms: LiveData<List<FilmModel>> = filmDao.getAll()

    suspend fun insert(film: FilmModel) {
        withContext(Dispatchers.IO) {
            filmDao.insert(film)
        }
    }

    suspend fun update(film: FilmModel) {
        withContext(Dispatchers.IO) {
            filmDao.update(film)
        }
    }

    suspend fun delete(film: FilmModel) {
        withContext(Dispatchers.IO) {
            filmDao.delete(film)
        }
    }
}