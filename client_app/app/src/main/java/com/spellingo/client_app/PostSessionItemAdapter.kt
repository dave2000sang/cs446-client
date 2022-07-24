package com.spellingo.client_app

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class PostSessionItemAdapter(
    private val context: PostSessionFragment,
    private val dataset: List<Pair<String, String>>,
) : RecyclerView.Adapter<PostSessionItemAdapter.ItemViewHolder>() {

    class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val correctSpellingTextView: TextView = view.findViewById(R.id.post_session_correct_spelling)
        val userSpellingTextView: TextView = view.findViewById(R.id.post_session_user_spelling)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.post_session_list_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun getItemCount() = dataset.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val correctSpelling = dataset[position].first
        val userSpelling = dataset[position].second
        holder.correctSpellingTextView.text = correctSpelling
        if(userSpelling.isEmpty()) {
            holder.userSpellingTextView.text = ""
            return
        }
        val spannable = SpannableStringBuilder()
        if(correctSpelling == userSpelling) {
            spannable.append(
                "✓ ",
                ForegroundColorSpan(ContextCompat.getColor(context.requireContext(), R.color.monokai_green)),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        else {
            spannable.append(
                "✕ ",
                ForegroundColorSpan(ContextCompat.getColor(context.requireContext(), R.color.monokai_red)),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        spannable.append(userSpelling)
        holder.userSpellingTextView.text = spannable
    }
}