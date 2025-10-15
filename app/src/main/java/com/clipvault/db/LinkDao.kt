package com.clipvault.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.clipvault.model.Link

@Dao
interface LinkDao {
    @Insert
    suspend fun insert(link: Link)

    @Update
    suspend fun update(link: Link)

    @Query("SELECT * FROM links ORDER BY title ASC")
    fun getAllLinks(): LiveData<List<Link>>

    @Query("SELECT * FROM links WHERE isFavorite = 1 ORDER BY title ASC")
    fun getFavoriteLinks(): LiveData<List<Link>>

    @Query("SELECT * FROM links WHERE category = :category ORDER BY title ASC")
    fun getLinksByCategory(category: String): LiveData<List<Link>>

    @Query("SELECT * FROM links WHERE title LIKE :searchQuery OR url LIKE :searchQuery ORDER BY title ASC")
    fun searchLinks(searchQuery: String): LiveData<List<Link>>
}
