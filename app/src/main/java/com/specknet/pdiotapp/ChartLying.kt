package com.specknet.pdiotapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.specknet.pdiotapp.utils.DataPointsGraph
import org.apache.commons.lang3.time.DateUtils
import org.apache.commons.lang3.time.DateUtils.addDays
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChartLying: AppCompatActivity() {

    private lateinit var barChart: BarChart
    private var TAG = "FragmentActivity"
    private var scoreList = ArrayList<DataPointsGraph>()
    private val act = this@ChartLying
    val historyDatabase = HistoryDatabase(act)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chart_running)

        barChart = findViewById(R.id.barChart)

        scoreList = getScoreList()

        initBarChart()

        //now draw bar chart with dynamic data
        val entries: ArrayList<BarEntry> = ArrayList()

        //you can replace this data object with  your custom object
        for (i in scoreList.indices) {
            val score = scoreList[i]
            entries.add(BarEntry(i.toFloat(), score.time))
        }

        val barDataSet = BarDataSet(entries, "")
        barDataSet.setColors(*ColorTemplate.COLORFUL_COLORS)

        val data = BarData(barDataSet)
        barChart.data = data

        barChart.invalidate()

    }

    private fun initBarChart() {
        //hide grid lines
        barChart.axisLeft.setDrawGridLines(false)
        val xAxis: XAxis = barChart.xAxis
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)

        //remove right y-axis
        barChart.axisRight.isEnabled = false

        //remove legend
        barChart.legend.isEnabled = false

        //remove description label
        barChart.description.isEnabled = false

        //add animation
        barChart.animateY(3000)

        // to draw label on xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM_INSIDE
        xAxis.valueFormatter = MyAxisFormatter()
        xAxis.setDrawLabels(true)
        xAxis.granularity = 1f
        xAxis.labelRotationAngle = +90f

    }

    inner class MyAxisFormatter : IndexAxisValueFormatter() {
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            val index = value.toInt()
            Log.d(TAG, "getAxisLabel: index $index")
            return if (index < scoreList.size) {
                scoreList[index].day
            } else {
                ""
            }
        }
    }

    // simulate api call
    // we are initialising it directly
    private fun getScoreList(): ArrayList<DataPointsGraph> {

        var durationToday = 10.0F
        val sdf = SimpleDateFormat("dd/M/yyyy")
        val currentDate = sdf.format(Date())
        var random = arrayOf(1570.0F, 2082.0F, 1794.0F, 2071.0F, 1990.0F, 1870.0F, 1900.0F)
        for (i in 6 downTo 0 step 1){
            val day: Date = DateUtils.addDays(Date(), -i)
            val currentDate = sdf.format(day)
            durationToday = historyDatabase.getDuration("Lying", currentDate).toFloat()
            if (durationToday > 0)
                scoreList.add(DataPointsGraph(currentDate, durationToday))
            else
                scoreList.add(DataPointsGraph(currentDate, random[i]))
        }

        return scoreList
    }

}