package com.spellingo.client_app

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate


class StatisticsFragment: Fragment() {

    private val viewModel: StatisticsViewModel by activityViewModels()
    private lateinit var pieChart: PieChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_statistics_page, container, false)
        pieChart = root.findViewById(R.id.piechart)
        viewModel.requestGlobalStats()
        setupPieChart()
        viewModel.ratioLiveData.observe(viewLifecycleOwner, Observer(fun(ratio) {
            loadPieChartData(ratio.first, ratio.second)
        }))
        return root
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