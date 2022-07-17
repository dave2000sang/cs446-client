package com.spellingo.client_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class GameSelectionFragment : Fragment() {

    private val viewModel: GameSelectionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_game_selection, container, false)

        // TODO (Nathan) could use an enum or something else, for now 0 is difficulty, 1 is category

        val isStandard = arguments == null || arguments!!.getBoolean("standard")
        viewModel.getCategories(isStandard)
        viewModel.categoryLiveData.observe(viewLifecycleOwner, Observer(fun(choicesParam) {
            val choices = choicesParam.map { choice ->
                choice.replaceFirstChar { it.uppercase() }
            }
            println("DEBUG ============================ $choices")
            val choicesRecyclerView = root.findViewById<RecyclerView>(R.id.choices)
            var selected = arrayOf("")
            choicesRecyclerView.adapter = GameSelectionItemAdapter(this, choices, selected)
            val doneSelectionButton = root.findViewById<MaterialButton>(R.id.doneSelection)
            doneSelectionButton.setOnClickListener {
                if (selected[0].isNotEmpty()) {
                    val bundle = Bundle()
                    if (isStandard) {
                        bundle.putString("category", "standard")
                        bundle.putString("difficulty", selected[0].uppercase())
                    } else {
                        bundle.putString("category", selected[0].lowercase())
                        bundle.putString("difficulty", "OTHER")
                    }
                    println("DEBUG after select adapter ================================ " + selected[0])
                    findNavController().navigate(R.id.action_fragmentGameSelection_to_fragmentGameSession, bundle)
                }
            }
        }))

        // TODO (Nathan) Rework this. Just go directly to game session on card click (no play button)

        return root
    }
}