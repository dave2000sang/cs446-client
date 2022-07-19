package com.spellingo.client_app

import android.content.Context
import android.text.SpannableStringBuilder

/**
 * Information box for displaying [Word] on UI
 */
abstract class InfoBox() {
    /**
     * Get spannable string
     * @param word [Word] containing all information
     * @return spannable string
     */
    abstract fun getSpannable(word: Word): SpannableStringBuilder
}