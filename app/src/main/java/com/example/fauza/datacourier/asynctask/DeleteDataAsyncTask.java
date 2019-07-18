package com.example.fauza.datacourier.asynctask;

import android.content.Context;
import android.os.AsyncTask;

import com.example.fauza.datacourier.database.DataCourierDatabase;

public class DeleteDataAsyncTask extends AsyncTask<Void, Void, Void> {

    private DataCourierDatabase dataCourierDatabase;

    public DeleteDataAsyncTask(Context context) {
        this.dataCourierDatabase = DataCourierDatabase.getInstance(context);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        dataCourierDatabase.getDataDao().deleteAllData();
        return null;
    }
}
