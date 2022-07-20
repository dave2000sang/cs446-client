package com.spellingo.client_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

/**
 * Adapter for RecyclerView of game mode selection fragment. Displays modes.
 */
class GameSelectionItemAdapter(
    private val context: GameSelectionFragment,
    private val dataset: List<String>,
    private val isStandard: Boolean
) : RecyclerView.Adapter<GameSelectionItemAdapter.ItemViewHolder>() {

    class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val button : MaterialButton = view.findViewById(R.id.game_selection_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.game_selection_list_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun getItemCount() = dataset.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val mode = dataset[position]
        holder.button.text = mode

        val bundle = Bundle()
        holder.button.setOnClickListener {
            val bundle = Bundle()
            if (isStandard) {
                bundle.putString("category", "standard")
                bundle.putString("difficulty", mode.uppercase())
            } else {
                bundle.putString("category", mode.lowercase())
                bundle.putString("difficulty", "OTHER")
            }
            //TODO Nathan integrate sudden death in UI
//            bundle.putString("suddenDeath", "SUDDEN_DEATH")
            context.findNavController().navigate(R.id.action_fragmentGameSelection_to_fragmentGameSession, bundle)
        }
    }
}