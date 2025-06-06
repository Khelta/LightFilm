package com.example.lightfilm.database.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.lightfilm.Helper.apertureStringToValue
import com.example.lightfilm.Helper.deleteFile
import com.example.lightfilm.Helper.shutterSpeedStringToValue
import com.example.lightfilm.database.PictureModel
import com.example.lightfilm.database.repository.PictureRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PictureViewmodel(application: Application) : AndroidViewModel(application) {
    private val repository: PictureRepository = PictureRepository(application)
    val allPictures: LiveData<List<PictureModel>> = repository.allPictures

    var currentPicture by mutableStateOf<PictureModel?>(null)

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

    fun delete(picture: PictureModel?, context: Context) {
        CoroutineScope(Dispatchers.Main).launch {
            picture?.pathToFile?.let {
                deleteFile(it, context)
            }
            repository.delete(picture)
        }
    }

    fun edit(title: String, aperture: String, shutterSpeed: String) {
        currentPicture?.let { currentPicture ->
            this.update(
                currentPicture.copy(
                    title = if (title != "") title else null,
                    selectedAperture = apertureStringToValue(aperture),
                    selectedShutterSpeed = shutterSpeedStringToValue(shutterSpeed)
                )
            )
        }
    }

    fun getNextPicture() {
        CoroutineScope(Dispatchers.Main).launch {
            currentPicture?.let { picture ->
                val nextPicture = repository.getNextPicture(picture)
                nextPicture?.let {
                    currentPicture = nextPicture
                }
            }
        }
    }

    fun getPreviousPicture() {
        CoroutineScope(Dispatchers.Main).launch {
            currentPicture?.let { picture ->
                val previousPicture = repository.getPreviousPicture(picture)
                previousPicture?.let {
                    currentPicture = previousPicture
                }
            }
        }
    }

    fun setPicture(picture: PictureModel?) {
        currentPicture = picture
    }

    fun rotatePicture() {
        currentPicture = currentPicture?.let{currentPicture ->
            val pic = currentPicture.copy(rotation = (currentPicture.rotation - 1) % 4)
            this.update(pic)
            pic
        }
    }
}