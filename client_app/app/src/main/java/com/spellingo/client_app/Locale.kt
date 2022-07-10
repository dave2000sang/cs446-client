package com.spellingo.client_app

enum class Locale {
    US,
    UK;
    companion object {
        fun getByName(name: String) = valueOf(name.uppercase())
    }
}