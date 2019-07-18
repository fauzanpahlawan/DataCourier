package com.example.fauza.datacourier;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.fauza.datacourier.asynctask.ClientRunnable;
import com.example.fauza.datacourier.constant.Global;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerIntentService extends IntentService {

    private Context mContext = ServerIntentService.this;
    private ServerSocket serverSocket;

    public ServerIntentService() {
        super("ServerIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(Global.TAG, "ServerIntentService.onCreate");
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.v(Global.TAG, "ServerIntentService.onStartCommand");
        return START_NOT_STICKY;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.v(Global.TAG, "ServerIntentService.onHandleIntent");
        try {
            serverSocket = new ServerSocket(Global.APP_PORT);
            Log.v(Global.TAG, "serverSocket.On" + Global.APP_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true)
            try {
                Socket clientSocket = serverSocket.accept();
                Log.v(Global.TAG, "Accepting " + clientSocket.toString());
                ClientRunnable clientRunnable = new ClientRunnable(clientSocket, mContext);
                clientRunnable.run();
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
    }
}
