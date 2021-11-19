package com.specknet.pdiotapp.data.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.specknet.pdiotapp.R;

import java.util.ArrayList;

public class AdapterActivity extends RecyclerView.Adapter<AdapterActivity.Viewholder> {

    private Context context;
    private ArrayList<ModelActivity> activityModelArrayList;

    public AdapterActivity(Context context, ArrayList<ModelActivity> activityModelArrayList) {
        this.context = context;
        this.activityModelArrayList = activityModelArrayList;
    }

    @NonNull
    @Override
    public AdapterActivity.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterActivity.Viewholder holder, int position) {
        ModelActivity model = activityModelArrayList.get(position);
        holder.activityNameTV.setText(model.getActivity_label());
        holder.activityIV.setImageResource(model.getActivity_image());
    }

    @Override
    public int getItemCount() {
        return activityModelArrayList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        private ImageView activityIV;
        private TextView activityNameTV;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            activityIV = itemView.findViewById(R.id.idIVActivityImage);
            activityNameTV = itemView.findViewById(R.id.idTVActivityName);
        }
    }
}

