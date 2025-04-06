package com.example.lightfilm.database.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.lightfilm.database.AppDatabase
import com.example.lightfilm.database.PictureModel
import com.example.lightfilm.database.dao.PictureDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PictureRepository(application: Application) {
    private val pictureDao: PictureDao = AppDatabase.getDatabase(application).pictureDao()
    val allPictures: LiveData<List<PictureModel>> = pictureDao.getAll()

    suspend fun insert(picture: PictureModel) {
        withContext(Dispatchers.IO) {
            pictureDao.insert(picture)
        }
    }

    suspend fun update(picture: PictureModel) {
        withContext(Dispatchers.IO) {
            pictureDao.update(picture)
        }
    }

    suspend fun delete(picture: PictureModel) {
        withContext(Dispatchers.IO) {
            pictureDao.delete(picture)
        }
    }
}