package com.spellingo.client_app

/**
 * Difficulties for standard mode
 */
enum class Difficulty {
    EASY,
    MEDIUM,
    HARD,
    OTHER;   // Used for non-standard categories
    companion object {
        /**
         * Get [Difficulty] enum by name
         * @param name difficulty name as string
         * @return corresponding [Difficulty] enum
         */
        fun getByName(name: String) = valueOf(name.uppercase())
    }
}