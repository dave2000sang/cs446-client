package com.spellingo.client_app

enum class Difficulty {
    EASY,
    MEDIUM,
    HARD,
    OTHER;   // Used for non-standard categories
    companion object {
        fun getByName(name: String) = valueOf(name.uppercase())
    }
}