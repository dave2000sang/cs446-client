package com.spellingo.client_app

import android.app.AlertDialog
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
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
        var title = "Session " + session.id.toString() + "\n"
        if (session.category == "standard") {
            title += session.difficulty.toString().lowercase().replaceFirstChar { it.uppercase() }
        } else {
            title += session.category.lowercase().replaceFirstChar { it.uppercase() }
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
                    var s1 = ""
                    var s2 = ""
                    for(pair in t) {
                        s1 += pair.first + "\n"
                        s2 += pair.second + "\n"
                    }
                    s1 = s1.dropLast(1) // Remove extra newline
                    s2 = s2.dropLast(1) // Remove extra newline
                    wordList.text = s1
                    userInputList.text = s2
                    guesses.removeObserver(this)
                    context.clearGuesses()
                }
            })

            builder.setView(popup)
            builder.show()
        }
    }
}