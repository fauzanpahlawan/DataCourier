package com.example.fauza.datacourier.asynctask;

import android.content.Context;
import android.os.AsyncTask;

import com.example.fauza.datacourier.constant.Global;
import com.example.fauza.datacourier.database.DataCourierDatabase;
import com.example.fauza.datacourier.entity.DataEntity;
import com.example.fauza.datacourier.interfaces.SaveDataInterface;

import java.util.List;

public class SaveDataAsyncTask extends AsyncTask<Void, Void, String> {

    private List<DataEntity> dataEntity;
    private DataCourierDatabase dataCourierDatabase;
    private SaveDataInterface saveDataInterface;


    public SaveDataAsyncTask(Context context, SaveDataInterface saveDataInterface, List<DataEntity> dataEntity) {
        this.dataCourierDatabase = DataCourierDatabase.getInstance(context);
        this.saveDataInterface = saveDataInterface;
        this.dataEntity = dataEntity;

    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            dataCourierDatabase.getDataDao().insertAll(dataEntity);
            return Global.SAVE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return Global.SAVE_FAILED;
        }
    }

    @Override
    protected void onPostExecute(String message) {
        saveDataInterface.saveDataCallBack(message);
    }
}

