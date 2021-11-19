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
        activityModelArrayList!!.add(ModelActivity("Running", R.drawable.logo_background))
        activityModelArrayList!!.add(ModelActivity("Walking", R.drawable.logo_background))
        activityModelArrayList!!.add(ModelActivity("Desk work", R.drawable.logo_background))
        activityModelArrayList!!.add(ModelActivity("Climbing stairs", R.drawable.logo_background))
        activityModelArrayList!!.add(ModelActivity("Falling", R.drawable.logo_background))
        activityModelArrayList!!.add(ModelActivity("Sitting/Standing", R.drawable.logo_background))


        val activityAdapter = AdapterActivity(this, activityModelArrayList)

        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        activityRV.setLayoutManager(linearLayoutManager)
        activityRV.setAdapter(activityAdapter)
    }
}