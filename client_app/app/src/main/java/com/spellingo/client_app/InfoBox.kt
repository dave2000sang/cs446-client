package com.spellingo.client_app

import android.content.Context
import android.text.SpannableStringBuilder

abstract class InfoBox() {
    abstract fun getSpannable(word: Word): SpannableStringBuilder
}