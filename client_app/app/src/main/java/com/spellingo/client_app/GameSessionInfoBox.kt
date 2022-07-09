package com.spellingo.client_app

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat

class GameSessionInfoBox(private val context: Context) : InfoBox() {
    private val spannable = SpannableStringBuilder()
    override fun getSpannable(word: Word): SpannableStringBuilder {
        // Essential parts are part of speech and definition
        val part = word.part.replaceFirstChar { it.uppercase() }
        val definition = word.definition.replaceFirstChar { it.uppercase() }
        spannable.append(
            part,
            ForegroundColorSpan(ContextCompat.getColor(context, R.color.infobox_highlight1)),
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.append("\n\n$definition")
        return spannable
    }
}