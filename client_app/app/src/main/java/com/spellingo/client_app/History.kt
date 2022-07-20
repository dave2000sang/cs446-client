package com.spellingo.client_app

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * History data structure in Room database
 */
@Entity(primaryKeys = ["id", "locale", "category"])
data class History(
    val id: String,
    val locale: Locale,
    val category: String,
    @ColumnInfo(name = "difficulty") val difficulty: Difficulty,
    @ColumnInfo(name = "correct") val score: Int,
    @ColumnInfo(name = "total") val total: Int
)
