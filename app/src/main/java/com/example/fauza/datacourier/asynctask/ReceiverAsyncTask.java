package com.example.fauza.datacourier.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.fauza.datacourier.CommonMethod;
import com.example.fauza.datacourier.constant.Global;
import com.example.fauza.datacourier.database.DataCourierDatabase;
import com.example.fauza.datacourier.entity.DataEntity;
import com.example.fauza.datacourier.interfaces.ReceiverInterface;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

public class ReceiverAsyncTask extends AsyncTask<Void, Void, Void> {

    private String hostIp;
    private boolean dataExist = false;
    private ReceiverInterface receiverInterface;
    private DataCourierDatabase dataCourierDatabase;

    public ReceiverAsyncTask(String hostIp, Context context, ReceiverInterface receiverInterface) {
        this.hostIp = hostIp;
        this.dataCourierDatabase = DataCourierDatabase.getInstance(context);
        this.receiverInterface = receiverInterface;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(hostIp, Global.APP_PORT), 5000);
            Log.v(Global.TAG, socket.toString());
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Gson gson = new Gson();
            List<DataEntity> data = gson.fromJson(in.readLine(), Global.jsonToListDataEntity);
            if (!data.isEmpty()) {
                Log.v(Global.TAG, "dataReceived.");
                dataExist = true;
                try {
                    dataCourierDatabase.getDataDao().insertAll(data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            in.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.v(Global.TAG, e.toString());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void data) {
        if (dataExist) {
            CommonMethod.recordTimeDataTransferEnd();
            receiverInterface.dataReceiveStatus("Data Received.");
        }
    }
}