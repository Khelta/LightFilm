package com.example.lightfilm.database.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.lightfilm.database.UserFilmModel
import com.example.lightfilm.database.repository.UserFilmRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserFilmViewmodel(application: Application) : AndroidViewModel(application) {
    private val repository: UserFilmRepository = UserFilmRepository(application)
    val allUserFilms: LiveData<List<UserFilmModel>> = repository.allUserFilms

    fun insert(userFilm: UserFilmModel) {
        CoroutineScope(Dispatchers.Main).launch {
            repository.insert(userFilm)
        }
    }

    fun update(userFilm: UserFilmModel) {
        CoroutineScope(Dispatchers.Main).launch {
            repository.update(userFilm)
        }
    }

    fun delete(userFilm: UserFilmModel?) {
        CoroutineScope(Dispatchers.Main).launch {
            repository.delete(userFilm)
        }
    }
}