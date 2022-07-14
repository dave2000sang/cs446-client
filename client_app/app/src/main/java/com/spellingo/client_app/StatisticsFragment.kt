package com.spellingo.client_app

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate


class StatisticsFragment: Fragment() {

    private val viewModel: StatisticsViewModel by activityViewModels()
    private lateinit var pieChart: PieChart
    private lateinit var barChart: BarChart
    private lateinit var barList: ArrayList<BarEntry>
    private lateinit var barDataSet: BarDataSet
    private lateinit var barData: BarData

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_statistics_page, container, false)
        var correctWordCounter = 0
        var incorrectWordCounter = 0
        var totalWordCounter = 0
        var correctTextView = root.findViewById<TextView>(R.id.correctCounter)
        var incorrectTextView = root.findViewById<TextView>(R.id.incorrectCounter)
        var totalTextView = root.findViewById<TextView>(R.id.totalCounter)

        // Initialize and draw PieChart Here
        pieChart = root.findViewById(R.id.pieChart)
        setupPieChart()

        // Initialize and draw BarChart Here
        barChart = root.findViewById(R.id.barChart)
        setupBarChart()



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
        pieChart = root.findViewById(R.id.pieChart)
        viewModel.requestGlobalStats()
        setupPieChart()
        viewModel.ratioLiveData.observe(viewLifecycleOwner, Observer(fun(ratio) {
            correctWordCounter = ratio.first
            totalWordCounter = ratio.second
            incorrectWordCounter = totalWordCounter-correctWordCounter
            loadPieChartData(correctWordCounter, totalWordCounter)

            // Setting Counter Information
            correctTextView.text = correctWordCounter.toString()
            incorrectTextView.text = incorrectWordCounter.toString()
            totalTextView.text = totalWordCounter.toString()
        }))





        return root
    }

    private fun setupBarChart () {
        // Initialize Bar Chart Here
        barList = arrayListOf<BarEntry>()

        // Enter your bar chart entries here. The first element of the pair leave it as 1, 2, 3, ...
        // this is the X-Value labels which will be replaced with Strings below. It doesn't allow
        // you to set them as strings. The strings will be your categories later.
        // For the second element of the pair this is your y-axis counter. So number of times you
        // tried each category.
        var barChartEntries = mutableListOf<Pair<Float, Float>>()
        barChartEntries.add(Pair(1f, 5f))
        barChartEntries.add(Pair(2f, 8f))

        for (i in barChartEntries) {
            barList.add(BarEntry(i.first, i.second))
        }

        // Setting Labels, Colours, and Data Linkages here.
        barDataSet = BarDataSet(barList, "Words Summary")
        barData = BarData(barDataSet)
        barDataSet.setColors(ColorTemplate.JOYFUL_COLORS, 250)
        barDataSet.valueTextColor = Color.BLACK
        barDataSet.valueTextSize = 15f
        barChart.data = barData

        // Changing X - Axis to String (I have 0 clue how this works it just does)
        // Enter your category String Names below
        var categoryListLabel = mutableListOf<String>("Category 1", "Category 2")

        // Modifying X-Axis Labels
        val xAxis = barChart.xAxis
        xAxis.granularity = 1f
        xAxis.setCenterAxisLabels(true)
        xAxis.setDrawGridLines(false)
        xAxis.labelRotationAngle = -45f
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.axisMinimum = barChart.xChartMin-.5f;
        xAxis.axisMaximum = barChart.xChartMax+.5f;
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(categoryListLabel)

        // Removing Legend and Description
        barChart.legend.isEnabled = false
        barChart.description.isEnabled = false

    }


    private fun setupPieChart() {
        //TODO change to our own color palette
        var labelSize = 12
        var centerLabelSize = 24
        pieChart.isDrawHoleEnabled = true
        pieChart.setUsePercentValues(true)
        pieChart.setEntryLabelTextSize(labelSize.toFloat())
        pieChart.setEntryLabelColor(Color.BLACK)
        pieChart.setCenterText("Spelling Statistics")
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

    private fun loadPieChartData (correct: Int, total: Int) {
        //TODO change to our own color palette
        val dataEntry = arrayListOf<PieEntry>()
        if(total > 0 && correct >= 0 && correct <= total) {
            val percentCorrect = correct.toFloat() / total
            dataEntry.add(PieEntry(percentCorrect, "Correct"))
            dataEntry.add(PieEntry(1 - percentCorrect, "Incorrect"))
        }
        else {
            //TODO write a message like "No word statistics available"
        }

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