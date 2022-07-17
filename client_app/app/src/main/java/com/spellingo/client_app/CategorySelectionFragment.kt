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

class CategorySelectionFragment : Fragment() {

    private val viewModel: GameSessionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_category_selection, container, false)

        // TODO dynamically get list of categories
        // TODO get rid of the play button, just click a category / difficulty
        // TODO merge difficulties and categories? makes the coding a lot easier
        // TODO rename variables, refactor, clean up
        // TODO make UI pretty
        val categories = listOf("Medical", "Law", "Anime")

        val categoryRecyclerView = root.findViewById<RecyclerView>(R.id.categoryList)
        var selected = arrayOf("")
        categoryRecyclerView.adapter = GameSelectionItemAdapter(this, categories, selected)

        val doneSelectionButton = root.findViewById<MaterialButton>(R.id.doneSelection)
        doneSelectionButton.setOnClickListener {
            if (!selected.isEmpty()) {
                viewModel.category = selected[0]
                findNavController().navigate(R.id.action_fragmentCategorySelection_to_fragmentGameSession)
            }
        }

        return root
    }
}