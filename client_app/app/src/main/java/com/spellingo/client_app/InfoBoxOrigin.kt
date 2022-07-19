package com.spellingo.client_app

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import androidx.core.content.ContextCompat

/**
 * Decorator to add word origin to [InfoBox]
 */
class InfoBoxOrigin(box: InfoBox, private val context: Context) : InfoBoxDecorator(box) {
    /**
     * Add origin to [box]'s spannable string
     * @param word [Word] containing all information
     * @return spannable string
     */
    override fun getSpannable(word: Word): SpannableStringBuilder {
        val spannable = super.getSpannable(word)
        val origin = word.origin.replaceFirstChar { it.uppercase() }
        spannable.append(
            "\n\nEtymology",
            ForegroundColorSpan(ContextCompat.getColor(context, R.color.infobox_highlight2)),
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.append(
            "\n\t$origin",
            RelativeSizeSpan(0.8f),
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return spannable
    }
}