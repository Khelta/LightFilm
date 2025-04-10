package com.example.lightfilm.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.lightfilm.database.FilmModel

@Dao
interface FilmDao {

    @Query("SELECT * FROM film")
    fun getAll(): LiveData<List<FilmModel>>

    @Insert
    suspend fun insert(film: FilmModel)

    @Update
    suspend fun update(film: FilmModel)

    @Delete
    suspend fun delete(film: FilmModel)
}