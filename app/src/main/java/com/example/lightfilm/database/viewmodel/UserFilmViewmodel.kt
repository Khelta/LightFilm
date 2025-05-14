package com.example.lightfilm.database.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.lightfilm.Helper.deleteFile
import com.example.lightfilm.database.UserFilmModel
import com.example.lightfilm.database.repository.UserFilmRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserFilmViewmodel(application: Application) : AndroidViewModel(application) {
    private val repository: UserFilmRepository = UserFilmRepository(application)
    val allUserFilms: LiveData<List<UserFilmModel>> = repository.allUserFilms

    var currentUserFilm by mutableStateOf<UserFilmModel?>(null)

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

    fun delete(userFilm: UserFilmModel?, pictureViewmodel: PictureViewmodel, context: Context) {
        CoroutineScope(Dispatchers.Main).launch {
            userFilm?.let {
                val pictures =
                    pictureViewmodel.allPictures.value?.filter { it.userFilmId == userFilm.uid }
                        ?: listOf(
                            null
                        )
                for (picture in pictures) {
                    pictureViewmodel.delete(picture, context)
                    picture?.pathToFile?.let {
                        deleteFile(it, context)
                    }
                }
            }
            currentUserFilm = null
            repository.delete(userFilm)
        }
    }

    fun setUserFilm(userFilm: UserFilmModel?) {
        currentUserFilm = userFilm
    }
}