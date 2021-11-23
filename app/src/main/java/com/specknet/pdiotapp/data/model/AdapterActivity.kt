package com.specknet.pdiotapp.data.model

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.specknet.pdiotapp.*
import com.specknet.pdiotapp.data.model.AdapterActivity.Viewholder
import java.util.ArrayList

class AdapterActivity(
    private val context: Context,
    private val activityModelArrayList: ArrayList<ModelActivity>
) :
    RecyclerView.Adapter<Viewholder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_layout, parent, false)
        return Viewholder(view)
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        val model = activityModelArrayList[position]

        holder.activityNameTV.text = model.activity_label
        holder.activityIV.setImageResource(model.activity_image)
        holder.itemView.setOnClickListener {
            val intent: Intent
            when (position) {
                0 -> {
                    intent = Intent(context, ChartRunning::class.java)
                    it.getContext().startActivity(intent)
                }
                1 -> {
                    intent = Intent(context, ChartWalking::class.java)
                    it.getContext().startActivity(intent)
                }
                2 -> {
                    intent = Intent(context, ChartFalling::class.java)
                    it.getContext().startActivity(intent)
                }
                3 -> {
                    intent = Intent(context, ChartSitting::class.java)
                    it.getContext().startActivity(intent)
                }
                4 -> {
                    intent = Intent(context, ChartLying::class.java)
                    it.getContext().startActivity(intent)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return activityModelArrayList.size
    }

    inner class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val activityIV: ImageView = itemView.findViewById(R.id.idIVActivityImage)
        val activityNameTV: TextView = itemView.findViewById(R.id.idTVActivityName)
    }
}