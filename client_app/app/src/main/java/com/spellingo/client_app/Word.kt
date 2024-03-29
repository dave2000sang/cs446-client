package com.spellingo.client_app

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Word data structure in Room database
 */
@Entity(primaryKeys = ["id", "locale", "category"])
data class Word(
    val id: String,
    val locale: Locale,
    val category: String,
    @ColumnInfo(name = "definition") val definition: String,
    @ColumnInfo(name = "usage") val usage: String,
    @ColumnInfo(name = "origin") val origin: String,
    @ColumnInfo(name = "part") val part: String,
    @ColumnInfo(name = "audio") val audio: String,
    @ColumnInfo(name = "difficulty") val difficulty: Difficulty,
    @ColumnInfo(name = "phonetic") val phonetic: String,
)
