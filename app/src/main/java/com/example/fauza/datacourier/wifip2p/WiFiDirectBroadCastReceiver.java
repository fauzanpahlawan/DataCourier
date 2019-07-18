package com.example.fauza.datacourier.wifip2p;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.design.widget.Snackbar;

import com.example.fauza.datacourier.DeliverDataActivity;
import com.example.fauza.datacourier.R;
import com.example.fauza.datacourier.ReceiveDataActivity;

public class WiFiDirectBroadCastReceiver extends BroadcastReceiver {

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private DeliverDataActivity sActivity;
    private ReceiveDataActivity rActivity;

    public WiFiDirectBroadCastReceiver(WifiP2pManager mManager, WifiP2pManager.Channel mChannel, DeliverDataActivity sActivity, ReceiveDataActivity receiveDataActivity) {
        this.mManager = mManager;
        this.mChannel = mChannel;
        this.sActivity = sActivity;
        this.rActivity = receiveDataActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Wifi State Listener
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                if (rActivity != null) {
                    Snackbar.make(rActivity.findViewById(R.id.lay_receive_data_activity), "Wifi Enabled", Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(sActivity.findViewById(R.id.lay_send_data_activity), "Wifi Enabled", Snackbar.LENGTH_SHORT).show();
                }
            } else {
                if (rActivity != null) {
                    Snackbar.make(rActivity.findViewById(R.id.lay_receive_data_activity), "Wifi Disabled", Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(sActivity.findViewById(R.id.lay_send_data_activity), "Wifi Disabled", Snackbar.LENGTH_SHORT).show();
                }
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Peers List Listener
            if (mManager != null) {
                if (rActivity != null) {
//                    mManager.requestPeers(mChannel, rActivity.peerListListener);
                }
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Connection Listener
            if (mManager == null) {
                return;
            }
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if (networkInfo.isConnected()) {
                if (rActivity != null) {
//                    mManager.requestConnectionInfo(mChannel, rActivity.connectionInfoListener);
                } else {
                    mManager.requestConnectionInfo(mChannel, sActivity);
                    mManager.requestGroupInfo(mChannel, sActivity);
                }
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {

        }
    }
}
