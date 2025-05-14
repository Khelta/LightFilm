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

    @Query(
        """
        SELECT * FROM picture  
        WHERE user_film_id = :userFilmId AND uid > :id
        ORDER BY uid ASC
        LIMIT 1
        """
    )
    fun getNextPicture(userFilmId: Int, id: Int): PictureModel?

    @Query(
        """
        SELECT * FROM picture  
        WHERE user_film_id = :userFilmId AND uid < :id
        ORDER BY uid DESC
        LIMIT 1
        """
    )
    fun getPreviousPicture(userFilmId: Int, id: Int): PictureModel?

    @Insert
    suspend fun insert(picture: PictureModel)

    @Update
    suspend fun update(picture: PictureModel)

    @Delete
    suspend fun delete(picture: PictureModel)
}