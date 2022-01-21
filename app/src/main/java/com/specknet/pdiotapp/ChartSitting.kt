package com.specknet.pdiotapp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
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
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChartSitting: AppCompatActivity() {

    private lateinit var barChart: BarChart
    private var TAG = "SittingActivity"
    private var dataPointsList = ArrayList<DataPointsGraph>()
    private val act = this@ChartSitting
    val historyDatabase = HistoryDatabase(act)

    private val channelId = "i.apps.notifications"
    private val description = "Test notification"
    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chart_sitting)
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        barChart = findViewById(R.id.barChart)

        dataPointsList = getScoreList()

        initBarChart()

        val entries: ArrayList<BarEntry> = ArrayList()

        for (i in dataPointsList.indices) {
            val score = dataPointsList[i]
            entries.add(BarEntry(i.toFloat(), score.time))
        }

        val barDataSet = BarDataSet(entries, "")
        barDataSet.setColors(*ColorTemplate.COLORFUL_COLORS)

        val data = BarData(barDataSet)
        barChart.data = data

        barChart.invalidate()

        sendNotification()

    }

    private fun sendNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)

            builder = Notification.Builder(this, channelId)
                .setSmallIcon(R.mipmap.logo_foreground)
                .setContentTitle("Inactive for too long")
                .setContentText("Get up and exercise, you have been sitting down for too long.")
        } else {
            builder = Notification.Builder(this)
                .setSmallIcon(R.mipmap.logo_foreground)
                .setContentTitle("Inactive for too long")
                .setContentText("Get up and exercise, you have been sitting down for too long.")
        }
        notificationManager.notify(1234, builder.build())
    }

    private fun initBarChart() {
        barChart.axisLeft.setDrawGridLines(false)
        val xAxis: XAxis = barChart.xAxis
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)

        barChart.axisRight.isEnabled = false

        barChart.legend.isEnabled = false

        barChart.description.isEnabled = false

        barChart.animateY(3000)

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
            return if (index < dataPointsList.size) {
                dataPointsList[index].day
            } else {
                ""
            }
        }
    }

    private fun getScoreList(): ArrayList<DataPointsGraph> {

        var durationToday: Float
        val sdf = SimpleDateFormat("dd/M/yyyy")
        for (i in 6 downTo 0 step 1){
            val day: Date = DateUtils.addDays(Date(), -i)
            val currentDate = sdf.format(day)
            durationToday = historyDatabase.getDuration("Sitting/Standing", currentDate).toFloat()
            if (durationToday > 0)
                dataPointsList.add(DataPointsGraph(currentDate, durationToday))
        }

        return dataPointsList
    }

}