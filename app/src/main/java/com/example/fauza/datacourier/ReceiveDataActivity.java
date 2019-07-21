package com.example.fauza.datacourier;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fauza.datacourier.asynctask.CheckConnectionAsyncTask;
import com.example.fauza.datacourier.asynctask.ReadDataAsyncTask;
import com.example.fauza.datacourier.asynctask.ReceiverAsyncTask;
import com.example.fauza.datacourier.asynctask.TestDataWriteAsyncTask;
import com.example.fauza.datacourier.constant.Global;
import com.example.fauza.datacourier.entity.DataEntity;
import com.example.fauza.datacourier.entity.TestData;
import com.example.fauza.datacourier.interfaces.CheckConnectionInterface;
import com.example.fauza.datacourier.interfaces.ReadDataInterface;
import com.example.fauza.datacourier.interfaces.ReceiverInterface;
import com.example.fauza.datacourier.interfaces.VerboseInterface;
import com.example.fauza.datacourier.model.DataHolder;
import com.example.fauza.datacourier.wifip2p.WiFiDirectBroadCastReceiver;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.skyfishjy.library.RippleBackground;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReceiveDataActivity extends AppCompatActivity implements CheckConnectionInterface, ReadDataInterface, ReceiverInterface, VerboseInterface {

    @BindView(R.id.tv_activity_log)
    TextView tvActivityLog;
    @BindView(R.id.lay_receive_data_activity)
    LinearLayout layReceiveDataActivity;

    Context mContext = ReceiveDataActivity.this;
    @BindView(R.id.ripple_center_image)
    ImageView rippleCenterImage;
    @BindView(R.id.ripple_content)
    RippleBackground rippleContent;


    //Firebase
    private DatabaseReference databaseReference;

    private List<DataEntity> dataEntities = null;
    private WifiCredentialholder wifiCredentialholder;

    //WiFi and WiFi p2p
    IntentFilter mIntentFilter;
    WifiManager wifiManager;
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    BroadcastReceiver mReceiver;

    long timeStampStart;
    long timeStartEnd;

    private WifiP2pDnsSdServiceRequest serviceRequest = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_data);
        ButterKnife.bind(this);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        initializeWifiP2p();

        enableWIfi();

        ReadDataAsyncTask readDataAsyncTask = new ReadDataAsyncTask(this, ReceiveDataActivity.this);
        readDataAsyncTask.execute();

        rippleContent.startRippleAnimation();
    }

    private void enableWIfi() {
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
    }

    private void initializeWifiP2p() {
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WiFiDirectBroadCastReceiver(mManager, mChannel, null, ReceiveDataActivity.this);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    /**
     * SERVICE DISCOVERY
     * This part below contain parts required to make service discovery possible.
     * registerLocalService() ->to create record of the service.
     * discoverService() -> to start service discovery.
     * unregisterLocalService() -> to clear and remove current unused local service.
     * unregisterServiceRequest() -> to clear and remove current unused service discovery.
     * PS* local service and service discovery will keep running until timeout, hence clearer and remover required.
     */

    final HashMap<String, String> buddies = new HashMap<>();

    //to start service discovery
    private void discoverService() {
        CommonMethod.appendTv(tvActivityLog, "discoverService");
        //unregister current service request
        if (serviceRequest != null) {
            unregisterServiceRequest();
        }
        //set local service listener
        //method -> setDnsSdResponseListeners(channel, serviceResponseListener, TxtListener);
        mManager.setDnsSdResponseListeners(mChannel,
                //service response listener
                new WifiP2pManager.DnsSdServiceResponseListener() {
                    @Override
                    public void onDnsSdServiceAvailable(String instanceName, String registrationType, WifiP2pDevice device) {
                        //check if instance name is receiver
                        String dataCourier = instanceName.substring(0, 12);
                        if (dataCourier.equalsIgnoreCase(Global.DATA_COURIER)) {
                            Log.v(Global.TAG, "onDnsSdServiceAvailable");
                            CommonMethod.appendTv(tvActivityLog, "Service Found");
                            String[] wifiCredentials = instanceName.split(":");
                            wifiCredentialholder = new WifiCredentialholder();
                            wifiCredentialholder.SSID = wifiCredentials[1];
                            wifiCredentialholder.password = wifiCredentials[2];
                            wifiCredentialholder.IPAddress = wifiCredentials[3];
                            CommonMethod.recordTimeDataFoundDevice();
                            timeStampStart = System.currentTimeMillis();
                            //connect to the wifi
                            connectToWifi(wifiCredentialholder);
                            //get the device information to use as connect
                        } else {
                            Log.v(Global.TAG, "Not our app user.");
                        }
                    }
                },
                //txt record listener
                new WifiP2pManager.DnsSdTxtRecordListener() {
                    @Override
                    public void onDnsSdTxtRecordAvailable(String fullDomain, Map<String, String> record, WifiP2pDevice device) {
                        //if there is exist service discovered this method will detect it.
                        buddies.put(device.deviceAddress, record.get(Global.KEY_ROLE));
                        Log.v(Global.TAG, "onDnsSdTxtRecordAvailable");
                        Log.v(Global.TAG, buddies.toString());
                    }
                });

        //create instance of service request
        serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();

        //add service request to wifi p2p manager
        mManager.addServiceRequest(mChannel, serviceRequest, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.v(Global.TAG, Global.ADD_SERVICE_REQUEST_SUCCESS);
            }

            @Override
            public void onFailure(int i) {
                Log.v(Global.TAG, Global.ADD_SERVICE_REQUEST_FAILED);
            }
        });

        //finally initiate service discovery
        mManager.discoverServices(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.v(Global.TAG, Global.DISCOVER_SERVICE_SUCCESS);
            }

            @Override
            public void onFailure(int i) {
                Log.v(Global.TAG, Global.DISCOVER_SERVICE_FAILED);
            }
        });
    }

    public void unregisterServiceRequest() {
        mManager.clearServiceRequests(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.v(Global.TAG, "clearServiceRequests.onSuccess");
            }

            @Override
            public void onFailure(int i) {
                Log.v(Global.TAG, "clearServiceRequests.onFailure");
            }
        });

        mManager.removeServiceRequest(mChannel, serviceRequest, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.v(Global.TAG, "removeServiceRequest.onSuccess");
            }

            @Override
            public void onFailure(int i) {
                Log.v(Global.TAG, "removeServiceRequest.onFailure");
            }
        });
    }

    private class WifiCredentialholder {
        String SSID;
        String password;
        String IPAddress;
    }

    private void connectToWifi(WifiCredentialholder wifiCredentialholder) {
        int netId;
        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        wifiConfiguration.SSID = String.format("\"%s\"", wifiCredentialholder.SSID);
        wifiConfiguration.preSharedKey = String.format("\"%s\"", wifiCredentialholder.password);
        netId = wifiManager.addNetwork(wifiConfiguration);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();
    }

    public BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v(Global.TAG, "ConnectionChangeReceiver.onReceive()");
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (networkInfo != null) {
                if (networkInfo.isConnected()) {
                    Log.v(Global.TAG, "networkInfo.isConnected");
                    if (wifiCredentialholder != null) {
                        DhcpInfo dhcp = wifiManager.getDhcpInfo();
                        String address = Formatter.formatIpAddress(dhcp.gateway);
                        if (address.equalsIgnoreCase(wifiCredentialholder.IPAddress)) {
                            Log.v(Global.TAG, "we got the correct one");
                            CommonMethod.appendTv(tvActivityLog, String.format("%s %s", "Connecting to", address));
                            CommonMethod.recordTimeDataConnectToDevice();
                            timeStartEnd = System.currentTimeMillis();
                            TestData testData = new TestData(timeStartEnd, timeStampStart);
                            TestDataWriteAsyncTask testDataWriteAsyncTask = new TestDataWriteAsyncTask(mContext, testData);
                            testDataWriteAsyncTask.execute();
                            CommonMethod.recordTimeDataTransferStart();
                            CommonMethod.appendTv(tvActivityLog, "Connected");
                            ReceiverAsyncTask receiverAsyncTask = new ReceiverAsyncTask(address, mContext, ReceiveDataActivity.this);
                            CommonMethod.appendTv(tvActivityLog, "Send ping.");
                            receiverAsyncTask.execute();
                        } else {
                            Log.v(Global.TAG, "ssid.isNotEqual");
                        }
                    } else {
                        Log.v(Global.TAG, "wifiCredentialholder.isNull");
                    }
                } else {
                    Log.v(Global.TAG, "networkInfo.isNotConnected");
                }
            }
//            getConnectivityStatus(context);
        }

        public void getConnectivityStatus(Context context) {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                if (null != activeNetwork) {
                    if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                        Log.v(Global.TAG, "Wifi Enabled");
                        CheckConnectionAsyncTask checkConnectionAsyncTask = new CheckConnectionAsyncTask(ReceiveDataActivity.this);
                        checkConnectionAsyncTask.execute();
                    }
                    if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                        Log.v(Global.TAG, "Mobile Data Enabled");
                        CheckConnectionAsyncTask checkConnectionAsyncTask = new CheckConnectionAsyncTask(ReceiveDataActivity.this);
                        checkConnectionAsyncTask.execute();
                    }
                }
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
        registerReceiver(networkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        discoverService();

//        CheckConnectionAsyncTask checkConnectionAsyncTask = new CheckConnectionAsyncTask(ReceiveDataActivity.this);
//        checkConnectionAsyncTask.execute();

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
        unregisterReceiver(networkReceiver);

        unregisterServiceRequest();
        disconnect();
        removeGroup();
        deletePersistentGroups();

        wifiManager.disconnect();
    }

    public void removeGroup() {
        mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.v(Global.TAG, "removeGroup.onSuccess");
            }

            @Override
            public void onFailure(int i) {
                Log.v(Global.TAG, "removeGroup.onFailure");
            }
        });
    }

    public void disconnect() {
        if (mManager != null && mChannel != null) {
            mManager.requestGroupInfo(mChannel, new WifiP2pManager.GroupInfoListener() {
                @Override
                public void onGroupInfoAvailable(WifiP2pGroup group) {
                    if (group != null && mManager != null && mChannel != null
                            && group.isGroupOwner()) {
                        mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {

                            @Override
                            public void onSuccess() {
                                Log.d(Global.TAG, "removeGroup onSuccess -");
                            }

                            @Override
                            public void onFailure(int reason) {
                                Log.d(Global.TAG, "removeGroup onFailure -" + reason);
                            }
                        });
                    }
                }
            });
        }
    }

    private void deletePersistentGroups() {
        try {
            Method[] methods = WifiP2pManager.class.getMethods();
            for (Method method : methods) {
                if (method.getName().equals("deletePersistentGroup")) {
                    // Delete any persistent group
                    for (int netid = 0; netid < 32; netid++) {
                        method.invoke(mManager, mChannel, netid, null);
                    }
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Delete Group Failed", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void readDataCallBack(List<DataEntity> dataEntities) {
        if (dataEntities != null) {
            if (!dataEntities.isEmpty()) {
                this.dataEntities = dataEntities;
                Log.v(Global.TAG, "readDataCallBack.dataExist");
            } else {
                Log.v(Global.TAG, "readDataCallBack.dataEmpty");
            }
        }
    }


    @Override
    public void checkConnectionCallBack(Boolean isOnline) {
        if (isOnline) {
            Log.v(Global.TAG, "Internet Connection Available");
            deliverData();
        }
    }

    public void deliverData() {
        if (this.dataEntities != null) {
            for (DataEntity dataEntity : this.dataEntities) {
                DataHolder dataHolder = new DataHolder(dataEntity.getSuhu(), dataEntity.getKelembaban(), dataEntity.getKelembabanTanah(), dataEntity.getIntensitasCahaya());
                databaseReference.child(dataEntity.getData_id()).setValue(dataHolder);
            }
        }
    }

    @Override
    public void dataReceiveStatus(String messages) {
        CommonMethod.appendTv(tvActivityLog, messages);
        this.finish();
    }

    @Override
    public void passData(List<DataEntity> dataEntities) {
        for (DataEntity dataEntity : dataEntities) {
            Gson gson = new Gson();
            String showData = gson.toJson(dataEntity);
            CommonMethod.appendTv(tvActivityLog, showData);
        }
    }
}
