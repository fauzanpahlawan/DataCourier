package com.example.fauza.datacourier.asynctask;

import android.content.Context;
import android.util.Log;

import com.example.fauza.datacourier.constant.Global;
import com.example.fauza.datacourier.database.DataCourierDatabase;
import com.example.fauza.datacourier.entity.DataEntity;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class ClientRunnable implements Runnable {
    private Socket clientSocket;
    private DataCourierDatabase dataCourierDatabase;

    public ClientRunnable(Socket clientSocket, Context context) {
        this.clientSocket = clientSocket;
        this.dataCourierDatabase = DataCourierDatabase.getInstance(context);
    }

    @Override
    public void run() {
        List<DataEntity> dataEntities = dataCourierDatabase.getDataDao().getAll();
        PrintWriter out = null;
        try {
            Gson gson = new Gson();
            String data = gson.toJson(dataEntities);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            Log.v(Global.TAG, "Sending to " + clientSocket.toString());
            out.print(data);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                    clientSocket.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}