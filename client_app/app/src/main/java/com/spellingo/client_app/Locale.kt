package com.spellingo.client_app

/**
 * Locale for word's definition and pronunciation
 */
enum class Locale {
    US,
    UK;
    companion object {
        /**
         * Get [Locale] by name
         * @param name locale name as string
         * @return corresponding [Locale]
         */
        fun getByName(name: String) = valueOf(name.uppercase())
    }
}