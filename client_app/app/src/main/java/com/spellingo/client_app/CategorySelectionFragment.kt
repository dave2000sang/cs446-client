package com.spellingo.client_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton

class CategorySelectionFragment : Fragment() {

    private val viewModel: CategorySelectionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_category_selection, container, false)

        val doneSelectionButton = root.findViewById<MaterialButton>(R.id.doneSelection)
        doneSelectionButton.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentCategorySelection_to_fragmentGameSession)
        }

        return root
    }
}