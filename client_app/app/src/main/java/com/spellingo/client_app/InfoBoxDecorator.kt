package com.spellingo.client_app

import android.text.SpannableStringBuilder

/**
 * Decorator to extend [InfoBox]
 */
abstract class InfoBoxDecorator(private val box: InfoBox) : InfoBox() {
    /**
     * Get [box]'s spannable string
     * @param word [Word] containing all information
     * @return spannable string
     */
    override fun getSpannable(word: Word): SpannableStringBuilder {
        return box.getSpannable(word)
    }
}