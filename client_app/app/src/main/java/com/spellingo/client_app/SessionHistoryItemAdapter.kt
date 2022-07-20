package com.spellingo.client_app

import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SessionHistoryItemAdapter(
    private val context: SessionHistoryFragment,
    private val dataset: List<SessionDate>,
) : RecyclerView.Adapter<SessionHistoryItemAdapter.ItemViewHolder>() {

    class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val linearLayout: LinearLayout = view.findViewById(R.id.session_item_click_to_expand)
        val sessionTitle: TextView = view.findViewById(R.id.session_item_title)
        val sessionDate: TextView = view.findViewById(R.id.session_item_date)
        val sessionBody: TextView = view.findViewById(R.id.session_item_body)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.session_history_list_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun getItemCount() = dataset.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.sessionTitle.text = "Session 1 - Medical"                          // TODO set dynamically
        holder.sessionDate.text = "2022-07-20"                                    // TODO set dynamically
        holder.linearLayout.setOnClickListener {
            holder.sessionBody.text = "Test session body.\nThis is another line." // TODO set dynamically
            if (holder.sessionBody.visibility == View.VISIBLE) {
                holder.sessionBody.visibility = View.GONE
            } else {
                holder.sessionBody.visibility = View.VISIBLE
            }
        }
    }
}