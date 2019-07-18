package com.example.fauza.datacourier.asynctask;

import android.os.AsyncTask;

import com.example.fauza.datacourier.interfaces.CheckConnectionInterface;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class CheckConnectionAsyncTask extends AsyncTask<Void, Void, Boolean> {

    private CheckConnectionInterface checkConnectionInterface;

    public CheckConnectionAsyncTask(CheckConnectionInterface checkConnectionInterface) {
        this.checkConnectionInterface = checkConnectionInterface;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            int timeoutMs = 1500;
            Socket sock = new Socket();
            SocketAddress sockAddr = new InetSocketAddress("8.8.8.8", 53);

            sock.connect(sockAddr, timeoutMs);
            sock.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean isOnline) {
        checkConnectionInterface.checkConnectionCallBack(isOnline);
    }
}
