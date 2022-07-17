package com.spellingo.client_app

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

data class SessionDate(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "category") val category: String,
    @ColumnInfo(name = "difficulty") val difficulty: Difficulty,
)
