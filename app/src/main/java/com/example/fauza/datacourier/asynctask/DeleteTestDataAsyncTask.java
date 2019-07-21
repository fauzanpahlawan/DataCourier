package com.example.fauza.datacourier.asynctask;

import android.content.Context;
import android.os.AsyncTask;

import com.example.fauza.datacourier.database.DataCourierDatabase;

public class DeleteTestDataAsyncTask extends AsyncTask<Void, Void, Void> {
    DataCourierDatabase dataCourierDatabase;

    public DeleteTestDataAsyncTask(Context context) {
        dataCourierDatabase = DataCourierDatabase.getInstance(context);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        dataCourierDatabase.getTestDataDao().deleteAllData();
        return null;
    }
}
