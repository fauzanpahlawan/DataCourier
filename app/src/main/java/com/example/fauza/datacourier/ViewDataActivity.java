package com.example.fauza.datacourier;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.fauza.datacourier.adapter.DataListAdapter;
import com.example.fauza.datacourier.asynctask.DeleteDataAsyncTask;
import com.example.fauza.datacourier.asynctask.ReadDataAsyncTask;
import com.example.fauza.datacourier.entity.DataEntity;
import com.example.fauza.datacourier.interfaces.ReadDataInterface;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ViewDataActivity extends AppCompatActivity implements ReadDataInterface {

    @BindView(R.id.tv_data_id)
    TextView tvDataId;
    @BindView(R.id.lay_labels)
    LinearLayout layLabels;
    @BindView(R.id.rv_data)
    RecyclerView rvData;
    @BindView(R.id.bt_delete_all_data)
    Button btDeleteAllData;
    private DataListAdapter dataListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_data);
        ButterKnife.bind(this);

        setUpRecyclerView();

        ReadDataAsyncTask readDataAsyncTask = new ReadDataAsyncTask(this, this);
        readDataAsyncTask.execute();
    }

    public void setUpRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        dataListAdapter = new DataListAdapter();
        rvData.setLayoutManager(layoutManager);
        rvData.setItemAnimator(new DefaultItemAnimator());
        rvData.setAdapter(dataListAdapter);
    }

    @Override
    public void readDataCallBack(List<DataEntity> dataEntities) {
        if (!dataEntities.isEmpty()) {
            dataListAdapter.setDataEntity(dataEntities);
            dataListAdapter.notifyDataSetChanged();
        } else {
            dataListAdapter.setDataEntity(null);
            dataListAdapter.notifyDataSetChanged();
        }
    }

    @OnClick(R.id.bt_delete_all_data)
    public void onDeleteAllData() {
        new AlertDialog.Builder(this)
                .setTitle("Deletion confirmation")
                .setMessage("Do you really want to delete all the data?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        Snackbar.make(findViewById(R.id.lay_view_data_activity), "Data deleted", Snackbar.LENGTH_SHORT).show();
                        DeleteDataAsyncTask deleteDataAsyncTask = new DeleteDataAsyncTask(ViewDataActivity.this);
                        deleteDataAsyncTask.execute();
                        ReadDataAsyncTask readDataAsyncTask = new ReadDataAsyncTask(ViewDataActivity.this, ViewDataActivity.this);
                        readDataAsyncTask.execute();
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }
}
