package com.spellingo.client_app

/**
 * Sudden death gameplay mode
 */
enum class SuddenDeathMode {
    STANDARD,
    SUDDEN_DEATH,
    SD_CONTINUE;
    companion object {
        /**
         * Get [SuddenDeathMode] enum by name
         * @param name sudden death mode name as string
         * @return corresponding [SuddenDeathMode] enum
         */
        fun getByName(name: String) = valueOf(name.uppercase())
    }
}