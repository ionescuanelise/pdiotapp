package com.specknet.pdiotapp.live

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.specknet.pdiotapp.Activity
import com.specknet.pdiotapp.HistoryDatabase
import com.specknet.pdiotapp.R
import com.specknet.pdiotapp.User

import com.specknet.pdiotapp.utils.Constants
import com.specknet.pdiotapp.utils.RESpeckLiveData
import com.specknet.pdiotapp.utils.RespeckDataPrediction

//import com.specknet.pdiotapp.utils.ThingyLiveData

import org.tensorflow.lite.Interpreter

import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDateTime
import java.util.*

import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class LiveDataActivity : AppCompatActivity() {

    private val act = this@LiveDataActivity

    // global graph variables
    lateinit var dataSet_res_accel_x: LineDataSet
    lateinit var dataSet_res_accel_y: LineDataSet
    lateinit var dataSet_res_accel_z: LineDataSet

    var time = 0f
    lateinit var allRespeckData: LineData

    lateinit var predictionTextView : TextView
    lateinit var activityPrediction : String
    lateinit var respeckChart: LineChart

    // global broadcast receiver so we can unregister it
    lateinit var respeckLiveUpdateReceiver: BroadcastReceiver
    lateinit var looperRespeck: Looper

    val filterTestRespeck = IntentFilter(Constants.ACTION_RESPECK_LIVE_BROADCAST)

    var canCollect = false
    var predictions = ArrayList<FloatArray>()
    var interpreter: Interpreter? = null

    val historyDatabaseHelper = HistoryDatabase(act)


    @Throws(IOException::class)
    private fun loadModelFile(tflite : String): MappedByteBuffer{
        val MODEL_ASSETS_PATH = tflite
        val assetFileDescrptor = this.assets.openFd(MODEL_ASSETS_PATH)
        val fileInputStream = FileInputStream(assetFileDescrptor.getFileDescriptor())
        val fileChannel = fileInputStream.getChannel()
        val startOffset = assetFileDescrptor.startOffset
        val declaredLength = assetFileDescrptor.getDeclaredLength()
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }


    val map : HashMap<Int, String> = HashMap<Int, String>()

//    fun createMap(){
//        map.put(0, "Climbing stairs");
//        map.put(1, "Descending stairs");
//        map.put(2, "Desk work");
//        map.put(3, "Falling on knees");
//        map.put(4, "Falling on the back");
//        map.put(5, "Falling on the left");
//        map.put(6, "Falling on the right");
//        map.put(7, "Lying down left");
//        map.put(8, "Lying down on back");
//        map.put(9, "Lying down on stomach");
//        map.put(10, "Lying down right");
//        map.put(11, "Movement");
//        map.put(12, "Running");
//        map.put(13, "Sitting");
//        map.put(14, "Sitting bent backward");
//        map.put(15, "Sitting bent forward");
//        map.put(16, "Standing");
//        map.put(17, "Walking at normal speed");
//    }

    // grouped activities v2
//    fun createMap(){
//        map.put(0, "Climbing stairs")
//        map.put(1, "Descending stairs")
//        map.put(2, "Falling (Grouped)")
//        map.put(3, "Lying (Grouped)")
//        map.put(4, "Movement")
//        map.put(5, "Running")
//        map.put(6, "Sitting/Standing")
//        map.put(7, "Walking at normal speed")
//    }

    //grouped activities v4
    fun createMap(){
        map.put(0, "Falling")
        map.put(1, "Lying")
        map.put(2, "Running")
        map.put(3, "Sitting/Standing")
        map.put(4, "Walking at normal speed")
    }

    fun mapOutputLabel(maxIndex : Int) : String {
        return map.get(maxIndex).toString()
    }


    fun getActivityPredictionString(window : Array<FloatArray>): String {
        System.out.println("Got this:" + window.size)
        val output = FloatArray(5)
        interpreter!!.run(arrayOf(window), arrayOf(output))
        val maxIndex = output.indices.maxByOrNull <Int, Float> { it: Int -> output[it] } ?: -1
        val resultString = mapOutputLabel(maxIndex)
        return resultString
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_data)
        createMap()
        interpreter = Interpreter(loadModelFile("CNN_HAR_v7_50_windowsize.tflite"))
        canCollect = true
        setupCharts()

        // set up the broadcast receiver
        respeckLiveUpdateReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {

                Log.i("thread", "I am running on thread = " + Thread.currentThread().name)

                val action = intent.action

                if (action == Constants.ACTION_RESPECK_LIVE_BROADCAST) {

                    val liveData =
                        intent.getSerializableExtra(Constants.RESPECK_LIVE_DATA) as RESpeckLiveData
                    Log.d("Live", "onReceive: liveData = " + liveData)

//                     get all relevant intent contents
                    val x = liveData.accelX
                    val y = liveData.accelY
                    val z = liveData.accelZ
                    val g = liveData.gyro

                    predictions.add(floatArrayOf(x, y, z, g.x, g.y, g.z))
                    if (predictions.size >= 50) {
                        val current_predictions = predictions.take(50).toTypedArray()
                        updatePrediction(current_predictions)
                        predictions.clear()
                    }

                    time += 1
                    updateGraph("respeck", x, y, z)

                }
            }
        }

        // register receiver on another thread
        val handlerThreadRespeck = HandlerThread("bgThreadRespeckLive")
        handlerThreadRespeck.start()
        looperRespeck = handlerThreadRespeck.looper
        val handlerRespeck = Handler(looperRespeck)
        this.registerReceiver(respeckLiveUpdateReceiver, filterTestRespeck, null, handlerRespeck)

    }

    private fun updateHistoricalData(predictedActivity: String ){

        val sdf = SimpleDateFormat("dd/M/yyyy")
        val currentDate = sdf.format(Date())

        postDataToSQLite(1, currentDate, predictedActivity)

    }

    private fun postDataToSQLite(duration: Long, timestamp: String, name: String) {

        var activity = Activity(activity_name = name,
            duration = duration,
            date = timestamp
        )
        System.out.println("Activity today: " + name + " timestamp: " + timestamp)
        if (!historyDatabaseHelper!!.checkActivityToday(name, timestamp))
            historyDatabaseHelper!!.addActivity(activity)

        else{
            historyDatabaseHelper!!.updateDuration(activity)
        }

    }

    private fun updatePrediction(current_predictions: Array<FloatArray>) {

            predictionTextView = findViewById(R.id.activityPredictionTextView)

            val thread: Thread = object : Thread() {
                override fun run() {
                    try {
//                        while (!this.isInterrupted) {
                            sleep(2000)
                            runOnUiThread {
                                    activityPrediction = getActivityPredictionString(current_predictions)
                                    updateHistoricalData(activityPrediction)
                                    predictionTextView.setText("Activity is: " + activityPrediction)
                                }
//                            }
                        } catch (e: InterruptedException) {
                    }
                    }
                }

            thread.start()
    }


    fun setupCharts() {
        respeckChart = findViewById(R.id.respeck_chart)

        // Respeck

        time = 0f
        val entries_res_accel_x = ArrayList<Entry>()
        val entries_res_accel_y = ArrayList<Entry>()
        val entries_res_accel_z = ArrayList<Entry>()

        dataSet_res_accel_x = LineDataSet(entries_res_accel_x, "Accel X")
        dataSet_res_accel_y = LineDataSet(entries_res_accel_y, "Accel Y")
        dataSet_res_accel_z = LineDataSet(entries_res_accel_z, "Accel Z")

        dataSet_res_accel_x.setDrawCircles(false)
        dataSet_res_accel_y.setDrawCircles(false)
        dataSet_res_accel_z.setDrawCircles(false)

        dataSet_res_accel_x.setColor(
            ContextCompat.getColor(
                this,
                R.color.red
            )
        )
        dataSet_res_accel_y.setColor(
            ContextCompat.getColor(
                this,
                R.color.green
            )
        )
        dataSet_res_accel_z.setColor(
            ContextCompat.getColor(
                this,
                R.color.blue
            )
        )

        val dataSetsRes = ArrayList<ILineDataSet>()
        dataSetsRes.add(dataSet_res_accel_x)
        dataSetsRes.add(dataSet_res_accel_y)
        dataSetsRes.add(dataSet_res_accel_z)

        allRespeckData = LineData(dataSetsRes)
        respeckChart.data = allRespeckData
        respeckChart.invalidate()

    }

    fun updateGraph(graph: String, x: Float, y: Float, z: Float) {
        // take the first element from the queue
        // and update the graph with it
        if (graph == "respeck") {
            dataSet_res_accel_x.addEntry(Entry(time, x))
            dataSet_res_accel_y.addEntry(Entry(time, y))
            dataSet_res_accel_z.addEntry(Entry(time, z))

            runOnUiThread {
                allRespeckData.notifyDataChanged()
                respeckChart.notifyDataSetChanged()
                respeckChart.invalidate()
                respeckChart.setVisibleXRangeMaximum(150f)
                respeckChart.moveViewToX(respeckChart.lowestVisibleX + 40)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(respeckLiveUpdateReceiver)
        looperRespeck.quit()
    }
}
