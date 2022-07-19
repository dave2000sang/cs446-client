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
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate


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
        val sessionList = root.findViewById<LinearLayout>(R.id.scrollLinearLayout)

        // NavController
        navController = findNavController()
        navController.addOnDestinationChangedListener(navListener)

        viewModel.loadSessionData()

        viewModel.listOfSessions.observe(viewLifecycleOwner, Observer(fun(sessions) {
            if(sessions == null) return
            // DEBUG START
            println("DEBUG ===================== session list") // DEBUG
            for(sess in sessions) { println(sess) }
            for(sess in sessions.takeLast(1)) {
                //TODO Nathan use below line to get info of a specific session
                viewModel.getSession(sess)
            }
            // DEBUG END
            //TODO Nathan display list of cards
        }))

        viewModel.guessLiveData.observe(viewLifecycleOwner, Observer(fun(guesses) {
            if(guesses == null) return
            // DEBUG START
            println("DEBUG ===================== session guesses") // DEBUG
            for(guess in guesses) { println(guess) }
            // DEBUG END
            //TODO Nathan display session guess info
            //TODO note that the livedata still exists after returning to session stats fragment,
            //     need to hide
        }))

//        val sessionExtract = HashMap<String, Boolean>()
//
//        sessionExtract.put("1234", false)
//
//        for (i in sessionExtract) {
//            val view = inflater.inflate(R.layout.session_entry_item, null)
//            sessionList.addView(view)
//        }

//        val view = inflater.inflate(R.layout.session_entry_item, null)
//        val view1 = inflater.inflate(R.layout.session_entry_item, null)
//        val view2 = inflater.inflate(R.layout.session_entry_item, null)
//        val view3 = inflater.inflate(R.layout.session_entry_item, null)
//        val view4 = inflater.inflate(R.layout.session_entry_item, null)
//        val view5 = inflater.inflate(R.layout.session_entry_item, null)
//        val view6 = inflater.inflate(R.layout.session_entry_item, null)
//
//
//
//        sessionList.addView(view)
//        sessionList.addView(view1)
//        sessionList.addView(view2)
//        sessionList.addView(view3)
//        sessionList.addView(view4)
//        sessionList.addView(view5)
//        sessionList.addView(view6)

        return root
    }
}