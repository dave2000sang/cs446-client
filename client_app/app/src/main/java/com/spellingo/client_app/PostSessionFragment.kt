package com.spellingo.client_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class PostSessionFragment : Fragment() {

    private val viewModel: GameSessionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_post_session, container, false)

        // List of session words with correct spelling and user spelling
        val dataset = viewModel.getSessionResults()
        val recyclerView = root.findViewById<RecyclerView>(R.id.post_session_word_list)
        recyclerView.adapter = PostSessionItemAdapter(this, dataset)

        // Back button
        val backCallback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigate(R.id.fragment_main_menu)
        }

        // TODO Play again

        // Return to main menu
        val mainMenuButton = root.findViewById<MaterialButton>(R.id.post_session_to_main_menu_button)
        mainMenuButton.setOnClickListener {
            findNavController().navigate(R.id.action_postSessionFragment_to_fragment_main_menu)
        }

        return root
    }
}