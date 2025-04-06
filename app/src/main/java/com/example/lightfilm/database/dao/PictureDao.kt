package com.example.lightfilm.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.lightfilm.database.PictureModel

@Dao
interface PictureDao {

    @Query("SELECT * FROM picture")
    fun getAll(): LiveData<List<PictureModel>>

    @Insert
    suspend fun insert(picture: PictureModel)

    @Update
    suspend fun update(picture: PictureModel)

    @Delete
    suspend fun delete(picture: PictureModel)
}