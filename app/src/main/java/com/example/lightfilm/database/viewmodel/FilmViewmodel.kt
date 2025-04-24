package com.example.lightfilm.database.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.lightfilm.database.FilmModel
import com.example.lightfilm.database.repository.FilmRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FilmViewmodel(application: Application) : AndroidViewModel(application) {
    private val repository: FilmRepository = FilmRepository(application)
    val allFilms: LiveData<List<FilmModel>> = repository.allFilms

    fun insert(film: FilmModel) {
        CoroutineScope(Dispatchers.Main).launch {
            repository.insert(film)
        }
    }

    fun update(film: FilmModel) {
        CoroutineScope(Dispatchers.Main).launch {
            repository.update(film)
        }
    }

    fun delete(film: FilmModel) {
        CoroutineScope(Dispatchers.Main).launch {
            repository.delete(film)
        }
    }
}