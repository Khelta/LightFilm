package com.example.lightfilm.database.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.lightfilm.database.PictureModel
import com.example.lightfilm.database.repository.PictureRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PictureViewmodel(application: Application) : AndroidViewModel(application) {
    private val repository: PictureRepository = PictureRepository(application)
    val allPictures: LiveData<List<PictureModel>> = repository.allPictures

    fun insert(picture: PictureModel) {
        CoroutineScope(Dispatchers.Main).launch {
            repository.insert(picture)
        }
    }

    fun update(picture: PictureModel) {
        CoroutineScope(Dispatchers.Main).launch {
            repository.update(picture)
        }
    }

    fun delete(picture: PictureModel?) {
        CoroutineScope(Dispatchers.Main).launch {
            repository.delete(picture)
        }
    }
}