package com.example.fauza.datacourier;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.example.fauza.datacourier.asynctask.SaveDataAsyncTask;
import com.example.fauza.datacourier.constant.Global;
import com.example.fauza.datacourier.entity.DataEntity;
import com.example.fauza.datacourier.interfaces.SaveDataInterface;
import com.example.fauza.datacourier.model.DataModel;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SaveDataActivity extends AppCompatActivity implements SaveDataInterface {

    @BindView(R.id.tv_data)
    TextView tvData;
    @BindView(R.id.bt_save_data)
    Button btSaveData;
    List<DataModel> data;
    long dataId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_data);
        ButterKnife.bind(this);
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                data = null;
            } else {
                data = extras.getParcelableArrayList("MY_PARCEL");
            }
            String preview = new Gson().toJson(data);
            tvData.setText(preview);
        }
    }

    @OnClick(R.id.bt_save_data)
    public void onSaveClicked() {
        List<DataEntity> dataEntities = new ArrayList<>();
        for (DataModel dataModel : data) {
            dataEntities.add(new DataEntity(dataModel.getData_id(), dataModel.getSuhu(), dataModel.getKelembaban(), dataModel.getKelembabanTanah(), dataModel.getIntensitasCahaya()));
        }
        SaveDataAsyncTask saveDataAsyncTask = new SaveDataAsyncTask(this, this, dataEntities);
        saveDataAsyncTask.execute();
    }

    @Override
    public void saveDataCallBack(String message) {
        Snackbar.make(findViewById(R.id.lay_save_data_activity), message, Snackbar.LENGTH_SHORT).show();
        if (message.equalsIgnoreCase(Global.SAVE_SUCCESS)) {
            this.finish();
        }
    }
}
