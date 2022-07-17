package com.spellingo.client_app

enum class SuddenDeathMode {
    STANDARD,
    SUDDEN_DEATH,
    SD_CONTINUE;
    companion object {
        fun getByName(name: String) = valueOf(name.uppercase())
    }
}