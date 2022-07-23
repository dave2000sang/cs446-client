package com.spellingo.client_app

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.button.MaterialButton


class SessionHistoryFragment: Fragment() {
    private val viewModel: SessionHistoryViewModel by activityViewModels()
    private lateinit var navController: NavController
    private val navListener = NavController.OnDestinationChangedListener { _, destination, _ ->
        if(viewModel.previousDestination != destination.id
            && destination.id == R.id.sessionHistoryFragment) {
            // Reset all old livedata
            viewModel.resetLiveData()
        }
        viewModel.previousDestination = destination.id
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_session_history, container, false)

        // NavController
        navController = findNavController()
        navController.addOnDestinationChangedListener(navListener)

        viewModel.loadSessionData()

        viewModel.listOfSessions.observe(viewLifecycleOwner, Observer(fun(sessions) {
            if(sessions == null) return
            val recyclerView = root.findViewById<RecyclerView>(R.id.session_history_list)
            recyclerView.adapter = SessionHistoryItemAdapter(this, sessions)
        }))

        // Return to main menu
        val mainMenuButton = root.findViewById<MaterialButton>(R.id.session_history_to_main_menu_button)
        mainMenuButton.setOnClickListener {
            findNavController().navigate(R.id.action_sessionHistoryFragment_to_fragment_main_menu)
        }

        return root
    }

    fun getSession(session: SessionDate): LiveData<List<Pair<String, String>>?> {
        viewModel.getSession(session)
        return viewModel.guessLiveData
    }

    fun clearGuesses() {
        viewModel.clearGuesses()
    }
}