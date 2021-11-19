package com.specknet.pdiotapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.specknet.pdiotapp.data.model.AdapterActivity
import com.specknet.pdiotapp.data.model.ModelActivity
import java.util.*

class HistoryHealthData: AppCompatActivity() {
    lateinit var activityRV: RecyclerView

    private var activityModelArrayList: ArrayList<ModelActivity>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_health)
        activityRV = findViewById(R.id.idRVCourse)

        activityModelArrayList = ArrayList<ModelActivity>()
        activityModelArrayList!!.add(ModelActivity("Running", R.mipmap.running_foreground))
        activityModelArrayList!!.add(ModelActivity("Walking", R.mipmap.walking_foreground))
        activityModelArrayList!!.add(ModelActivity("Desk work", R.mipmap.desk_work_foreground))
        activityModelArrayList!!.add(ModelActivity("Climbing stairs", R.mipmap.climbing_foreground))
        activityModelArrayList!!.add(ModelActivity("Falling", R.mipmap.falling_foreground))
        activityModelArrayList!!.add(ModelActivity("Sitting/Standing", R.mipmap.standing_foreground))


        val activityAdapter = AdapterActivity(this, activityModelArrayList)

        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        activityRV.setLayoutManager(linearLayoutManager)
        activityRV.setAdapter(activityAdapter)
    }
}