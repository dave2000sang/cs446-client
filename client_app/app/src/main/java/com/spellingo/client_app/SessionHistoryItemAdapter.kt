package com.spellingo.client_app

import android.app.AlertDialog
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView

class SessionHistoryItemAdapter(
    private val context: SessionHistoryFragment,
    private val dataset: List<SessionDate>,
) : RecyclerView.Adapter<SessionHistoryItemAdapter.ItemViewHolder>() {

    class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val linearLayout: LinearLayout = view.findViewById(R.id.session_item_click_to_expand)
        val sessionTitle: TextView = view.findViewById(R.id.session_item_title)
        val sessionDate: TextView = view.findViewById(R.id.session_item_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.session_history_list_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun getItemCount() = dataset.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val session = dataset[position]
        val title = if (session.category == "standard") {
            session.difficulty.toString().lowercase().replaceFirstChar { it.uppercase() }
        } else {
            session.category.lowercase().replaceFirstChar { it.uppercase() }
        }
        holder.sessionTitle.text = title
        holder.sessionDate.text = session.date

        holder.linearLayout.setOnClickListener {
            val builder = AlertDialog.Builder(context.requireContext())
            val popup = LayoutInflater.from(context.requireContext()).inflate(R.layout.session_history_popup, null)
            val wordList: TextView = popup.findViewById<TextView>(R.id.session_history_popup_word_list)
            val userInputList: TextView = popup.findViewById<TextView>(R.id.session_history_popup_user_input_list)

            val guesses = context.getSession(session)
            guesses.observe(context.viewLifecycleOwner, object : Observer<List<Pair<String, String>>?> {
                override fun onChanged(t: List<Pair<String, String>>?) {
                    if(t == null) return
                    val spannable1 = SpannableStringBuilder()
                    val spannable2 = SpannableStringBuilder()
                    spannable1.append(
                        "Word",
                        StyleSpan(Typeface.BOLD),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    spannable2.append(
                        "Your Guess",
                        StyleSpan(Typeface.BOLD),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    for(pair in t) {
                        if(pair.first.isEmpty() || pair.second.isEmpty()) continue
                        spannable1.append("\n" + pair.first)
                        spannable2.append("\n")
                        val relSizeSpan = RelativeSizeSpan(0.8f)
                        if(pair.second == pair.first) {
                            spannable2.append(
                                "✓ ",
                                relSizeSpan,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                            spannable2.setSpan(
                                ForegroundColorSpan(ContextCompat.getColor(context.requireContext(), R.color.monokai_green)),
                                spannable2.getSpanStart(relSizeSpan),
                                spannable2.getSpanEnd(relSizeSpan),
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                        }
                        else {
                            spannable2.append(
                                "✕ ",
                                relSizeSpan,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                            spannable2.setSpan(
                                ForegroundColorSpan(ContextCompat.getColor(context.requireContext(), R.color.monokai_red)),
                                spannable2.getSpanStart(relSizeSpan),
                                spannable2.getSpanEnd(relSizeSpan),
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                        }
                        spannable2.append(pair.second)
                    }
                    wordList.text = spannable1
                    userInputList.text = spannable2
                    guesses.removeObserver(this)
                    context.clearGuesses()
                }
            })

            builder.setView(popup)
            builder.show()
        }
    }
}