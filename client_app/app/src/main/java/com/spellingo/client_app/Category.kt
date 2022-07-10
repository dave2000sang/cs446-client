package com.spellingo.client_app

enum class Category {
    STANDARD;
    companion object {
        fun getByName(name: String) = valueOf(name.uppercase())
    }
}