package com.spellingo.client_app

/**
 * Strategy that decides how to fetch words for session
 */
enum class GameStrategy {
    STANDARD,
    WOTD,
}