package com.spellingo.client_app

import android.text.SpannableStringBuilder

abstract class InfoBoxDecorator(private val box: InfoBox) : InfoBox() {
    override fun getSpannable(word: Word): SpannableStringBuilder {
        return box.getSpannable(word)
    }
}