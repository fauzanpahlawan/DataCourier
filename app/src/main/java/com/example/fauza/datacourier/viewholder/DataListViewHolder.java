package com.example.fauza.datacourier.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.fauza.datacourier.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DataListViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.tv_data_id)
    TextView tvDataId;
    @BindView(R.id.tv_data_content)
    TextView tvDataContent;


    public DataListViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }


    public void setItem(String dataId, String dataContent) {
        tvDataId.setText(dataId);
        tvDataContent.setText(dataContent);
    }
}
