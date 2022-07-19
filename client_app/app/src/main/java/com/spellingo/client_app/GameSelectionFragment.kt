package com.spellingo.client_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class GameSelectionFragment : Fragment() {

    private val viewModel: GameSelectionViewModel by activityViewModels()
    private lateinit var navController: NavController
    private val navListener = NavController.OnDestinationChangedListener { _, destination, _ ->
        if(viewModel.previousDestination != destination.id
            && destination.id == R.id.fragment_game_selection) {
            // Reset all old livedata
            viewModel.resetLiveData()
        }
        viewModel.previousDestination = destination.id
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_game_selection, container, false)
        val choicesRecyclerView = root.findViewById<RecyclerView>(R.id.choices)
        val doneSelectionButton = root.findViewById<MaterialButton>(R.id.doneSelection)

        // NavController
        navController = findNavController()
        navController.addOnDestinationChangedListener(navListener)

        val isStandard = arguments == null || arguments!!.getBoolean("standard")
        viewModel.getCategories(isStandard)
        viewModel.categoryLiveData.observe(viewLifecycleOwner, Observer(fun(choicesParam) {
            if(choicesParam == null) return
            val selected = arrayOf("")
            val choices = choicesParam.map { choice ->
                choice.replaceFirstChar { it.uppercase() }
            }
            choicesRecyclerView.adapter = GameSelectionItemAdapter(this, choices, selected)
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
                    //TODO Nathan integrate sudden death in UI
//                    bundle.putString("suddenDeath", "SUDDEN_DEATH")
                    findNavController().navigate(R.id.action_fragmentGameSelection_to_fragmentGameSession, bundle)
                }
            }
        }))

        // TODO (Nathan) Rework this. Just go directly to game session on card click (no play button)

        return root
    }
}