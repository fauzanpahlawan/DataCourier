package com.example.fauza.datacourier.asynctask;

import android.content.Context;
import android.os.AsyncTask;

import com.example.fauza.datacourier.database.DataCourierDatabase;
import com.example.fauza.datacourier.entity.DataEntity;
import com.example.fauza.datacourier.interfaces.ReadDataInterface;

import java.util.List;

public class ReadDataAsyncTask extends AsyncTask<Void, Void, List<DataEntity>> {
    private DataCourierDatabase dataCourierDatabase;
    private ReadDataInterface readDataInterface;

    public ReadDataAsyncTask(Context context, ReadDataInterface readDataInterface) {
        this.dataCourierDatabase = DataCourierDatabase.getInstance(context);
        this.readDataInterface = readDataInterface;
    }

    @Override
    protected List<DataEntity> doInBackground(Void... voids) {
        return dataCourierDatabase.getDataDao().getAll();
    }

    @Override
    protected void onPostExecute(List<DataEntity> dataEntities) {
        readDataInterface.readDataCallBack(dataEntities);
    }
}
