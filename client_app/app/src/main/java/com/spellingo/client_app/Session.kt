package com.spellingo.client_app

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Session data structure in Room database
 */
@Entity
data class Session(
    @PrimaryKey val id: Int,
    // For sorting sessions
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "category") val category: String,
    @ColumnInfo(name = "difficulty") val difficulty: Difficulty,
    // Words used in this session
    // Format as "word;word;word;word"
    @ColumnInfo(name = "sessionWords") var sessionWords: String,
    // Correct or incorrect result of the word
    // Format as "1;0;1;0;1", same split length as sessionWords
    @ColumnInfo(name = "sessionGuess") var sessionGuess: String
)
