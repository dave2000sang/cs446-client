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
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate


class SessionHistoryFragment: Fragment() {

    private val viewModel: StatisticsViewModel by activityViewModels()

    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_session_history, container, false)
        val sessionList = root.findViewById<LinearLayout>(R.id.scrollLinearLayout)
        val view = inflater.inflate(R.layout.session_entry_item, null)
        val view1 = inflater.inflate(R.layout.session_entry_item, null)
        val view2 = inflater.inflate(R.layout.session_entry_item, null)


        sessionList.addView(view)
        sessionList.addView(view1)
        sessionList.addView(view2)
        return root
    }
}