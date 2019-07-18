package com.example.fauza.datacourier.asynctask;

import android.content.Context;
import android.os.AsyncTask;

import com.example.fauza.datacourier.database.DataCourierDatabase;
import com.example.fauza.datacourier.entity.DataEntity;
import com.example.fauza.datacourier.interfaces.VerboseInterface;

import java.util.List;

public class VerboseAsyncTask extends AsyncTask<Void, Void, List<DataEntity>> {

    private VerboseInterface verboseInterface;
    private DataCourierDatabase dataCourierDatabase;

    public VerboseAsyncTask(Context context, VerboseInterface verboseInterface) {
        this.verboseInterface = verboseInterface;
        this.dataCourierDatabase = DataCourierDatabase.getInstance(context);
    }

    @Override
    protected List<DataEntity> doInBackground(Void... voids) {
        List<DataEntity> dataPost = dataCourierDatabase.getDataDao().getAll();
        return dataPost;
    }

    @Override
    protected void onPostExecute(List<DataEntity> dataEntities) {
        verboseInterface.passData(dataEntities);
    }
}
