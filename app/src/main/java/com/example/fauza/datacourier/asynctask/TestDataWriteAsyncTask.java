package com.example.fauza.datacourier.asynctask;

import android.content.Context;
import android.os.AsyncTask;

import com.example.fauza.datacourier.database.DataCourierDatabase;
import com.example.fauza.datacourier.entity.TestData;

public class TestDataWriteAsyncTask extends AsyncTask<Void, Void, Void> {

    private DataCourierDatabase dataCourierDatabase;
    private TestData testData;

    public TestDataWriteAsyncTask(Context context, TestData testData) {
        dataCourierDatabase = DataCourierDatabase.getInstance(context);
        this.testData = testData;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        dataCourierDatabase.getTestDataDao().insert(testData);
        return null;
    }
}
