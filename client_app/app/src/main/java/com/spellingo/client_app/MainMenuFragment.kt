package com.spellingo.client_app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class MainMenuFragment : Fragment() {

    private val viewModel: MainMenuViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_main_menu, container, false)

        val playButton =  root.findViewById<Button>(R.id.playGameButton)
        playButton.setOnClickListener {
            findNavController().navigate(R.id.mainMenuFragment_to_gameSessionFragment)
        }

        val settingsButton = root.findViewById<Button>(R.id.settingsButton)
        settingsButton.setOnClickListener {
            findNavController().navigate(R.id.mainMenuFragment_to_settingsFragment)
        }

        val statsButton = root.findViewById<Button>(R.id.statsButton)
        statsButton.setOnClickListener {
            findNavController().navigate(R.id.action_fragment_main_menu_to_statisticsSelectorFragment)
        }


        return root
    }

}