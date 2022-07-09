package com.spellingo.client_app

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import androidx.core.content.ContextCompat

class InfoBoxOrigin(box: InfoBox, private val context: Context) : InfoBoxDecorator(box) {
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