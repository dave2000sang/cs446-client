package com.spellingo.client_app

enum class Difficulty {
    EASY,
    MEDIUM,
    HARD;
    companion object {
        fun getByName(name: String) = valueOf(name.uppercase())
    }
}