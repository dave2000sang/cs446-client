package com.spellingo.client_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class GameSelectionFragment : Fragment() {

    private val viewModel: GameSessionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_game_selection, container, false)

        // TODO (Andrew) pass this variable in as a parameter to the fragment from main menu buttons
        // TODO (Nathan) could use an enum or something else, for now 0 is difficulty, 1 is category
        val type = 1

        val choices = mutableListOf<String>()
        if (type == 0) {
            choices.add("Easy")
            choices.add("Medium")
            choices.add("Hard")
        } else {
            // TODO (Andrew) dynamically get list of categories
            choices.addAll(listOf("Medical", "Law", "Anime"))
        }

        // TODO (Nathan) Rework this. Just go directly to game session on card click (no play button)
        val choicesRecyclerView = root.findViewById<RecyclerView>(R.id.choices)
        var selected = arrayOf("")
        choicesRecyclerView.adapter = GameSelectionItemAdapter(this, choices, selected)
        val doneSelectionButton = root.findViewById<MaterialButton>(R.id.doneSelection)
        doneSelectionButton.setOnClickListener {
            if (selected[0].isNotEmpty()) {
                if (type == 0) {
                    // TODO (Andrew) set the difficulty here
//                    viewModel.difficulty = selected[0]
                } else {
                    // TODO (Andrew) set the category here
                    viewModel.category = selected[0]
                }
                findNavController().navigate(R.id.action_fragmentGameSelection_to_fragmentGameSession)
            }
        }

        return root
    }
}