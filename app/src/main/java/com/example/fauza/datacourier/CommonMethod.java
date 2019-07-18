package com.example.fauza.datacourier;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;

import com.example.fauza.datacourier.constant.Global;
import com.example.fauza.datacourier.entity.DataEntity;
import com.google.gson.Gson;

import java.util.List;

public class CommonMethod {
    public static void intent(Context ct, Class cl) {
        Intent intent = new Intent(ct, cl);
        ct.startActivity(intent);
    }

    public static void appendTv(TextView textView, String activity) {
        textView.append("\n" + activity);
    }

    public static void recordTimeDataFoundDevice() {
        long time = System.currentTimeMillis();
        Log.v(Global.TAG_RECORD_ACTIVITY_LOG, "S1 " + time);
    }

    public static void recordTimeDataConnectToDevice() {
        long time = System.currentTimeMillis();
        Log.v(Global.TAG_RECORD_ACTIVITY_LOG, "E1 " + time);
    }

    public static void recordTimeDataTransferStart() {
        long time = System.currentTimeMillis();
        Log.v(Global.TAG_RECORD_ACTIVITY_LOG, "S2 " + time);
    }

    public static void recordTimeDataTransferEnd() {
        long time = System.currentTimeMillis();
        Log.v(Global.TAG_RECORD_ACTIVITY_LOG, "E2 " + time);
    }
}
