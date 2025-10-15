package com.clipvault.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "links")
data class Link(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val url: String,
    val notes: String?,
    val thumbnailUrl: String,
    val category: String,
    var isFavorite: Boolean
)
