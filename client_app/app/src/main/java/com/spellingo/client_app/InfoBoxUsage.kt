package com.spellingo.client_app

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.TypedValue
import androidx.core.content.ContextCompat

/**
 * Decorator to add word usage to [InfoBox]
 */
class InfoBoxUsage(box: InfoBox, private val context: Context) : InfoBoxDecorator(box) {
    /**
     * Add usage to [box]'s spannable string
     * @param word [Word] containing all information
     * @return spannable string
     */
    override fun getSpannable(word: Word): SpannableStringBuilder {
        val spannable = super.getSpannable(word)
        val usage = word.usage.replaceFirstChar { it.uppercase() }
        val a = TypedValue()
        context.theme.resolveAttribute(R.attr.gray_foreground, a, true)
        spannable.append(
            "\n\nUsage",
            ForegroundColorSpan(a.data),
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        val italic = StyleSpan(Typeface.ITALIC)
        spannable.append(
            "\n\t$usage",
            italic,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            RelativeSizeSpan(0.8f),
            spannable.getSpanStart(italic),
            spannable.getSpanEnd(italic),
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return spannable
    }
}