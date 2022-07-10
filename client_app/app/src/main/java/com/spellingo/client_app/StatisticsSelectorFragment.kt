package com.spellingo.client_app

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate


class StatisticsSelectorFragment: Fragment() {

    private val viewModel: StatisticsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_stat_selector, container, false)

        // This class is for the UI Statistics Selector Page.

        // Linking the UI Statistics Selector Page to the Overall Stats Page
        val myStatsButton = root.findViewById<Button>(R.id.myStats)
        myStatsButton.setOnClickListener {
            findNavController().navigate(R.id.action_statisticsSelectorFragment_to_statisticsFragment)
        }

        // Linking the UI Statistics Selector Page to the Session History Page.
        val mySessionStatsButton = root.findViewById<Button>(R.id.mySession)
        mySessionStatsButton.setOnClickListener {
            findNavController().navigate((R.id.action_statisticsSelectorFragment_to_sessionHistoryFragment))
        }
        return root
    }
}