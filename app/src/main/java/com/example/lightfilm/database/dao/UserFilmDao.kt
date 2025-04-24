package com.example.lightfilm.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.lightfilm.database.UserFilmModel

@Dao
interface UserFilmDao {

    @Query("SELECT * FROM user_film")
    fun getAll(): LiveData<List<UserFilmModel>>

    @Insert
    suspend fun insert(film: UserFilmModel)

    @Update
    suspend fun update(film: UserFilmModel)

    @Delete
    suspend fun delete(film: UserFilmModel)
}