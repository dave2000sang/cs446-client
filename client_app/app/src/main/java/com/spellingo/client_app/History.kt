package com.spellingo.client_app

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * History data structure in Room database
 */
@Entity
data class History(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "correct") val score: Int,
    @ColumnInfo(name = "total") val total: Int
)
