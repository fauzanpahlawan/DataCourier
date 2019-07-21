package com.example.fauza.datacourier.asynctask;

import android.content.Context;
import android.os.AsyncTask;

import com.example.fauza.datacourier.database.DataCourierDatabase;
import com.example.fauza.datacourier.entity.TestData;
import com.example.fauza.datacourier.interfaces.TestDataReadInterface;

import java.util.List;

public class TestDataReadAsyncTask extends AsyncTask<Void, Void, List<TestData>> {

    private TestDataReadInterface testDataReadInterface;
    private DataCourierDatabase dataCourierDatabase;

    public TestDataReadAsyncTask(Context context, TestDataReadInterface testDataReadInterface) {
        dataCourierDatabase = DataCourierDatabase.getInstance(context);
        this.testDataReadInterface = testDataReadInterface;
    }

    @Override
    protected List<TestData> doInBackground(Void... voids) {
        return dataCourierDatabase.getTestDataDao().getAll();
    }

    @Override
    protected void onPostExecute(List<TestData> testData) {
        testDataReadInterface.readTestDataCallBack(testData);
    }
}
