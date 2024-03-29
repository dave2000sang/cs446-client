package com.spellingo.client_app

import `in`.codeshuffle.typewriterview.TypeWriterView
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import kotlin.math.roundToInt


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class MainMenuFragment : Fragment() {

    private val viewModel: MainMenuViewModel by activityViewModels()
    private lateinit var animatedTitle: TypeWriterView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_main_menu, container, false)

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

        animatedTitle = root.findViewById<TypeWriterView>(R.id.typeWriterText)
        animatedTitle.setWithMusic(false)
        animatedTitle.setDelay(100)
        animatedTitle.animateText("Spellingo!")
        if(viewModel.startupLock) {
            animatedTitle.removeAnimation()
            animatedTitle.setPaddingRelative(0, 0, 0, 0)
        }
        else {
            val pad = 40 * (requireContext().resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
            animatedTitle.setPaddingRelative(pad.roundToInt(), 0, 0, 0)
        }

        val playDifficultyButton = root.findViewById<Button>(R.id.playDifficultyButton)
        playDifficultyButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putBoolean("standard", true)
            findNavController().navigate(R.id.action_mainMenuFragment_to_categorySelectionFragment, bundle)
        }

        val playCategoryButton = root.findViewById<Button>(R.id.playCategoryButton)
        playCategoryButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putBoolean("standard", false)
            findNavController().navigate(R.id.action_mainMenuFragment_to_categorySelectionFragment, bundle)
        }

        val settingsButton = root.findViewById<Button>(R.id.settingsButton)
        settingsButton.setOnClickListener {
            findNavController().navigate(R.id.action_mainMenuFragment_to_settingsFragment)
        }

        val statsButton = root.findViewById<Button>(R.id.statsButton)
        statsButton.setOnClickListener {
            findNavController().navigate(R.id.action_mainMenuFragment_to_statisticsFragment)
        }

        val sessionHistoryButton = root.findViewById<Button>(R.id.sessionHistoryButton)
        sessionHistoryButton.setOnClickListener {
            findNavController().navigate(R.id.action_mainMenuFragment_to_SessionHistoryFragment)
        }

        // Back button
        val backCallback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            requireActivity().finish()
        }

        if(sharedPreferences.getBoolean("show_statistics", true)) {
            statsButton.visibility = View.VISIBLE
            sessionHistoryButton.visibility = View.VISIBLE
        }
        else {
            statsButton.visibility = View.GONE
            sessionHistoryButton.visibility = View.GONE
        }

        viewModel.startApp()

        return root
    }

    override fun onPause() {
        super.onPause()
        animatedTitle.removeAnimation()
        animatedTitle.setPaddingRelative(0, 0, 0, 0)
    }


}