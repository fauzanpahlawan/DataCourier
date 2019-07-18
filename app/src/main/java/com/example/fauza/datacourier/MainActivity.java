package com.example.fauza.datacourier;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {


    @BindView(R.id.im_bt_collect_data)
    ImageButton imBtCollectData;
    @BindView(R.id.tv_label_collect_data)
    TextView tvLabelCollectData;
    @BindView(R.id.im_bt_view_data)
    ImageButton imBtViewData;
    @BindView(R.id.tv_label_view_data)
    TextView tvLabelViewData;
    @BindView(R.id.im_bt_start_delivery)
    ImageButton imBtStartDelivery;
    @BindView(R.id.tv_label_start_delivery)
    TextView tvLabelStartDelivery;
    @BindView(R.id.im_bt_start_receiving)
    ImageButton imBtStartReceiving;
    @BindView(R.id.tv_label_start_receiving)
    TextView tvLabelStartReceiving;
    @BindView(R.id.lay_activity_main)
    GridLayout layActivityMain;

    WifiManager wifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
    }

    @OnClick({R.id.im_bt_collect_data, R.id.im_bt_view_data, R.id.im_bt_start_delivery, R.id.im_bt_start_receiving})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.im_bt_collect_data:
                CommonMethod.intent(MainActivity.this, CollectDataActivity.class);
                break;
            case R.id.im_bt_view_data:
                CommonMethod.intent(MainActivity.this, ViewDataActivity.class);
                break;
            case R.id.im_bt_start_delivery:
                CommonMethod.intent(MainActivity.this, DeliverDataActivity.class);
                break;
            case R.id.im_bt_start_receiving:
                CommonMethod.intent(MainActivity.this, ReceiveDataActivity.class);
                break;
        }
    }

    public void runTest() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                CommonMethod.intent(MainActivity.this, ReceiveDataActivity.class);
            }
        }, 5000);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        runTest();
    }


}