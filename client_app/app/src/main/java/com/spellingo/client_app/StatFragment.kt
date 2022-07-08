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
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate


class StatisticsFragment: Fragment() {

    private val viewModel: StatViewModel by activityViewModels()
    lateinit private var pieChart: PieChart
    lateinit private var barChart: BarChart
    lateinit private var barList: ArrayList<BarEntry>
    lateinit private var barDataSet: BarDataSet
    lateinit private var barData: BarData

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_statistics_page, container, false)

        // Initialize and draw PieChart Here
        pieChart = root.findViewById(R.id.pieChart)
        setupPieChart()
        loadPieChartData()

        // Initialize Bar Chart Here
        barChart = root.findViewById(R.id.barChart)
        barList = arrayListOf<BarEntry>()
        barList.add(BarEntry (1f, 2f))
        barList.add(BarEntry (1f, 30f))
        barList.add(BarEntry (2f, 40f))
        barList.add(BarEntry (3f, 50f))
        barList.add(BarEntry (4f, 60f))
        barDataSet = BarDataSet(barList, "Words Summary")
        barData = BarData(barDataSet)
        barDataSet.setColors(ColorTemplate.JOYFUL_COLORS, 250)
        barDataSet.valueTextColor = Color.BLACK
        barDataSet.valueTextSize = 15f
        barChart.data = barData

        val pieChartButton = root.findViewById<Button>(R.id.showPieGraph)
        val barChartButton = root.findViewById<Button>(R.id.showBarGraph)

        pieChartButton.setOnClickListener {
            pieChart.isVisible = true
            barChart.isVisible = false
        }

        barChartButton.setOnClickListener {
            pieChart.isVisible = false
            barChart.isVisible = true
        }

        barChart.isVisible = false // set barChart to be invisible on entry.
        return root
    }

    private fun setupPieChart() {
        var labelSize = 12
        var centerLabelSize = 24
        pieChart.isDrawHoleEnabled = true
        pieChart.setUsePercentValues(true)
        pieChart.setEntryLabelTextSize(labelSize.toFloat())
        pieChart.setEntryLabelColor(Color.BLACK)
        pieChart.setCenterText("Spelling Staistics")
        pieChart.setCenterTextSize(centerLabelSize.toFloat())
        pieChart.description.isEnabled = false

        // Legend
        val legend: Legend = pieChart.legend
        legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        legend.orientation = Legend.LegendOrientation.VERTICAL
        legend.setDrawInside(false)
        legend.isEnabled = true

    }

    private fun loadPieChartData () {
        val dataEntry = arrayListOf<PieEntry>()
        dataEntry.add(PieEntry(0.40f, "Correct"))
        dataEntry.add(PieEntry(0.50f, "Incorrect"))
        dataEntry.add(PieEntry(0.10f, "Gave Up"))

        var colors = arrayListOf<Int>()

        for (i in ColorTemplate.MATERIAL_COLORS) {
            colors.add(i)
        }

        for (i in ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(i)
        }


        val dataSet = PieDataSet(dataEntry, "")
        dataSet.setColors(colors)

        val data: PieData = PieData(dataSet)
        data.setDrawValues(true)
        data.setValueFormatter(PercentFormatter(pieChart))
        data.setValueTextSize(12f)
        data.setValueTextColor(Color.BLACK)

        pieChart.data = data
        pieChart.invalidate()
    }


}