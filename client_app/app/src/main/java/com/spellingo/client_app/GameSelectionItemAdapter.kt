package com.spellingo.client_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

/**
 * Adapter for RecyclerView of game mode selection fragment. Displays modes.
 */
class GameSelectionItemAdapter(
    private val context: CategorySelectionFragment,
    private val dataset: List<String>,
    private var selected: Array<String>
) : RecyclerView.Adapter<GameSelectionItemAdapter.ItemViewHolder>() {

    class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.game_selection_textview)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun getItemCount() = dataset.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val mode = dataset[position]
        holder.textView.text = mode
        holder.textView.setOnClickListener{
            selected[0] = mode
        }
    }
}