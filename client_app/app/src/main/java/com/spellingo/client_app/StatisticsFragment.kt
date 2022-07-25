package com.spellingo.client_app

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.button.MaterialButton
import org.w3c.dom.Text


class StatisticsFragment: Fragment() {

    private val viewModel: StatisticsViewModel by activityViewModels()
    private lateinit var pieChart: PieChart
    private lateinit var noStats: TextView
    private lateinit var barList: ArrayList<BarEntry>
    private lateinit var barDataSet: BarDataSet
    private lateinit var barData: BarData
    private lateinit var navController: NavController
    private val navListener = NavController.OnDestinationChangedListener { _, destination, _ ->
        if(viewModel.previousDestination != destination.id
            && destination.id == R.id.statisticsFragment) {
            // Reset all old livedata
            viewModel.resetLiveData()
        }
        viewModel.previousDestination = destination.id
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_stats, container, false)
        noStats = root.findViewById<TextView>(R.id.no_stats_text)

            // NavController
        navController = findNavController()
        navController.addOnDestinationChangedListener(navListener)

        // Return to main menu
        val mainMenuButton = root.findViewById<MaterialButton>(R.id.stats_to_main_menu_button)
        mainMenuButton.setOnClickListener {
            navController.navigate(R.id.action_statisticsFragment_to_fragment_main_menu)
        }

        // Initialize and draw pie chart here
        pieChart = root.findViewById(R.id.pie_chart)
        setupPieChart()

        // First spinner
        val spinner1: Spinner = root.findViewById(R.id.stats_spinner_1)
        val arrayList1 = ArrayList<String>()
        arrayList1.add("All Words")
        arrayList1.add("Categories")
        arrayList1.add("Difficulties")
        val statArrayAdapter1 = ArrayAdapter(this.requireActivity(), R.layout.spinner_item_1, arrayList1)
        statArrayAdapter1.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinner1.adapter = statArrayAdapter1

        // Second spinner
        val spinner2: Spinner = root.findViewById(R.id.stats_spinner_2)
        val arrayList2 = ArrayList<String>()
        val statArrayAdapter2 = ArrayAdapter(this.requireActivity(), R.layout.spinner_item_2, arrayList2)
        statArrayAdapter2.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinner2.adapter = statArrayAdapter2

        // First spinner on click
        spinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = parent?.getItemAtPosition(position).toString()
                if (selectedItem == "All Words") {
                    viewModel.requestGlobalStats()
                } else if (selectedItem == "Categories") {
                    viewModel.requestCategoryBreakdown()
                } else if (selectedItem == "Difficulties") {
                    viewModel.requestDifficultyBreakdown()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        val categoryBreakdownString = "Breakdown"
        val difficultyBreakdownString = "Breakdown"

        // Second spinner on click
        spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = parent?.getItemAtPosition(position).toString()
                when(viewModel.statisticsChartType) {
                    StatisticsChartType.CATEGORY, StatisticsChartType.SELECTED_CATEGORY -> {
                        if(selectedItem == categoryBreakdownString) {
                            viewModel.requestCategoryBreakdown()
                        }
                        else {
                            viewModel.requestCategoryStats(selectedItem.lowercase())
                        }
                    }
                    StatisticsChartType.DIFFICULTY, StatisticsChartType.SELECTED_DIFFICULTY -> {
                        if(selectedItem == difficultyBreakdownString) {
                            viewModel.requestDifficultyBreakdown()
                        }
                        else {
                            viewModel.requestDifficultyStats(Difficulty.getByName(selectedItem))
                        }
                    }
                    else -> {}
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        viewModel.requestGlobalStats()
        viewModel.ratioLiveData.observe(viewLifecycleOwner, Observer(fun(ratio) {
            if(ratio == null) return
            when(viewModel.statisticsChartType) {
                StatisticsChartType.CATEGORY -> {
                    statArrayAdapter2.clear()
                    statArrayAdapter2.add(categoryBreakdownString)
                    statArrayAdapter2.addAll(viewModel.categoryList)
                    spinner2.visibility = View.VISIBLE
                }
                StatisticsChartType.DIFFICULTY -> {
                    statArrayAdapter2.clear()
                    statArrayAdapter2.add(difficultyBreakdownString)
                    statArrayAdapter2.addAll(listOf(
                        Difficulty.EASY,
                        Difficulty.MEDIUM,
                        Difficulty.HARD
                    ).map { diff -> diff.toString().lowercase().replaceFirstChar { it.uppercase() } })
                    spinner2.visibility = View.VISIBLE
                }
                StatisticsChartType.ALL -> {
                    statArrayAdapter2.clear()
                    spinner2.visibility = View.INVISIBLE
                }
                else -> {}
            }
            loadPieChartData(ratio)
        }))
        return root
    }


    private fun setupPieChart() {
        pieChart.isDrawHoleEnabled = false
        pieChart.description.isEnabled = false
        pieChart.setUsePercentValues(false)
        pieChart.setTouchEnabled(false)
        pieChart.setDrawEntryLabels(false)

        // Legend
        val legend: Legend = pieChart.legend
        legend.isEnabled = true
        legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        legend.orientation = Legend.LegendOrientation.VERTICAL
        legend.setDrawInside(false)
        legend.form = Legend.LegendForm.CIRCLE
        legend.textSize = 16f

        // Set legend text color
        val typedValue = TypedValue()
        requireActivity().theme.resolveAttribute(R.attr.primary_text, typedValue, true)
        legend.textColor = typedValue.data
    }

    private fun loadPieChartData (ratio: List<Pair<String, Int>>) {
        // If chart is empty, show no stats yet msg
        if (ratio.isEmpty()) {
            pieChart.visibility = View.GONE
            noStats.visibility = View.VISIBLE
        } else {
            pieChart.visibility = View.VISIBLE
            noStats.visibility = View.GONE
        }

        val dataEntry = ratio.map { PieEntry(it.second.toFloat(), it.first) }

        // Colors
        val colors = arrayListOf<Int>()
        colors.add(resources.getColor(R.color.monokai_green))
        colors.add(resources.getColor(R.color.monokai_red))
        colors.add(resources.getColor(R.color.monokai_yellow))
        colors.add(resources.getColor(R.color.monokai_blue))
        colors.add(resources.getColor(R.color.monokai_cyan))
        colors.add(resources.getColor(R.color.monokai_magenta))
        colors.add(resources.getColor(R.color.monokai_gray))
        val dataSet = PieDataSet(dataEntry, "")
        dataSet.colors = colors

        val data: PieData = PieData(dataSet)
        data.setDrawValues(true)
        val formatter: ValueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return "" + value.toInt()
            }
        }
        data.setValueFormatter(formatter)
        data.setValueTextSize(24f)
        data.setValueTextColor(Color.BLACK)

        pieChart.data = data
        pieChart.invalidate()
    }
}