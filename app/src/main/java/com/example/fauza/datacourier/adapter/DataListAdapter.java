package com.example.fauza.datacourier.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fauza.datacourier.R;
import com.example.fauza.datacourier.entity.DataEntity;
import com.example.fauza.datacourier.viewholder.DataListViewHolder;

import java.util.List;

public class DataListAdapter extends RecyclerView.Adapter<DataListViewHolder> {

    private List<DataEntity> dataEntities;

    public void setDataEntity(List<DataEntity> dataEntities) {
        this.dataEntities = dataEntities;
    }

    @NonNull
    @Override
    public DataListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_data_list, parent, false);
        return new DataListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DataListViewHolder holder, int position) {
        DataEntity dataEntity = dataEntities.get(position);
        String dataContent = String.format
                ("suhu: %s, kelembaban: %s, kelembabanTanah: %s, intensitasCahanaya %s",
                        dataEntity.getSuhu(), dataEntity.getKelembaban(), dataEntity.getKelembabanTanah(), dataEntity.getIntensitasCahaya());
        holder.setItem(
                dataEntity.getData_id(), dataContent
        );
    }

    @Override
    public int getItemCount() {
        if (dataEntities == null) {
            return 0;
        } else {
            return dataEntities.size();
        }
    }
}