package com.example.fauza.datacourier;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fauza.datacourier.asynctask.CheckConnectionAsyncTask;
import com.example.fauza.datacourier.asynctask.ReadDataAsyncTask;
import com.example.fauza.datacourier.constant.Global;
import com.example.fauza.datacourier.entity.DataEntity;
import com.example.fauza.datacourier.interfaces.CheckConnectionInterface;
import com.example.fauza.datacourier.interfaces.ReadDataInterface;
import com.example.fauza.datacourier.model.DataHolder;
import com.example.fauza.datacourier.wifip2p.WiFiDirectBroadCastReceiver;
import com.google.android.gms.common.internal.service.Common;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.skyfishjy.library.RippleBackground;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeliverDataActivity extends AppCompatActivity implements CheckConnectionInterface, ReadDataInterface, WifiP2pManager.GroupInfoListener, WifiP2pManager.ConnectionInfoListener {

    IntentFilter mIntentFilter;
    WifiManager wifiManager;
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    BroadcastReceiver mReceiver;
    InetAddress hostIp;

    @BindView(R.id.lay_send_data_activity)
    LinearLayout laySendDataActivity;
    @BindView(R.id.tv_connection_info)
    TextView tvConnectionInfo;
    @BindView(R.id.tv_activity_log)
    TextView tvActivityLog;
    @BindView(R.id.ripple_center_image)
    ImageView rippleCenterImage;
    @BindView(R.id.ripple_content)
    RippleBackground rippleContent;

    //Firebase
    private DatabaseReference databaseReference;

    private List<DataEntity> dataEntities = null;

    //serviceDiscovery
    private WifiP2pDnsSdServiceInfo serviceInfo = null;
    private WifiP2pDnsSdServiceRequest serviceRequest = null;

    String mNetworkName = "";
    String mPassphrase = "";
    String mInetAddress = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_data);
        ButterKnife.bind(this);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        initializeWifiP2p();

        enableWIfi();

        ReadDataAsyncTask readDataAsyncTask = new ReadDataAsyncTask(this, this);
        readDataAsyncTask.execute();

        rippleContent.startRippleAnimation();

        LocalBroadcastManager.getInstance(this).registerReceiver(networkReceiver,
                new IntentFilter(Global.SOCKET_ACTION));
    }

    private void enableWIfi() {
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
    }

    private void initializeWifiP2p() {
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WiFiDirectBroadCastReceiver(mManager, mChannel, DeliverDataActivity.this, null);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
        hostIp = wifiP2pInfo.groupOwnerAddress;
        mInetAddress = hostIp.getHostAddress();
        String info = getResources().getString(R.string.host_indicator) + ", Host Address: " + hostIp.getHostAddress();
        tvConnectionInfo.setText(info);
    }


    @Override
    public void onGroupInfoAvailable(WifiP2pGroup group) {
        Log.v(Global.TAG, "onGroupInfoAvailable");
        try {
            if (mNetworkName.equals(group.getNetworkName()) && mPassphrase.equals(group.getPassphrase())) {
                Log.v(Global.TAG, "Group Already Exist");
            } else {
                mNetworkName = group.getNetworkName();
                mPassphrase = group.getPassphrase();
                String wifiGroupInstance = "DATA_COURIER:" + group.getNetworkName() + ":" + group.getPassphrase() + ":" + mInetAddress;
                registerLocalService(wifiGroupInstance);
                Log.v(Global.TAG, mNetworkName);
                Log.v(Global.TAG, mPassphrase);
                Log.v(Global.TAG, wifiGroupInstance);
                CommonMethod.appendTv(tvConnectionInfo, mNetworkName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    //call this method after become group owner
    private void registerLocalService(String instanceName) {
        //clear current local service
        if (serviceInfo != null) {
            unregisterLocalService();
        }

        //add local service as group owner
        Map<String, String> record = new HashMap<>();
        record.put(Global.KEY_LISTEN_PORT, String.valueOf(Global.GROUP_OWNER_SERVICE_PORT));
        record.put(Global.KEY_ROLE, Global.ROLE_GROUP_OWNER);
        record.put(Global.KEY_STATUS, Global.STATUS_AVAILABLE);

        //create the instance of the service
        serviceInfo = WifiP2pDnsSdServiceInfo.newInstance(instanceName, Global.SERVICE_TYPE, record);

        //finally add the local service to wifi p2p manager
        mManager.addLocalService(mChannel, serviceInfo, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.v(Global.TAG, "addLocalService.onSuccess");
            }

            @Override
            public void onFailure(int i) {
                Log.v(Global.TAG, "addLocalService.onFailure");
            }
        });
    }

    //to unregister currently local service
    private void unregisterLocalService() {
        mManager.clearLocalServices(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.v(Global.TAG, "clearLocalServices.onSuccess");
            }

            @Override
            public void onFailure(int i) {
                Log.v(Global.TAG, "clearLocalServices.onFailure");
            }
        });

        mManager.removeLocalService(mChannel, serviceInfo, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.v(Global.TAG, "removeLocalService.onSuccess");
            }

            @Override
            public void onFailure(int i) {
                Log.v(Global.TAG, "removeLocalService.onFailure");
            }
        });
    }

    final HashMap<String, String> buddies = new HashMap<>();

    //to start service discovery
    private void discoverService() {
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
                    public void onDnsSdServiceAvailable(String instanceName, String registrationType, WifiP2pDevice resourceType) {
                        //check if instance name is receiver
                        if (instanceName.equalsIgnoreCase(Global.SERVICE_INSTANCE_RECEIVER)) {
                            resourceType.deviceName = buddies
                                    .containsKey(resourceType.deviceAddress) ? buddies
                                    .get(resourceType.deviceAddress) : resourceType.deviceName;

                            Log.v(Global.TAG, "onDnsSdServiceAvailable");
                            Log.v(Global.TAG, resourceType.deviceAddress);
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
                Log.v(Global.TAG, "addServiceRequest.onSuccess");
            }

            @Override
            public void onFailure(int i) {
                Log.v(Global.TAG, "addServiceRequest.onFailure");
            }
        });

        //finally initiate service discovery
        mManager.discoverServices(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.v(Global.TAG, "discoverServices.onSuccess");
            }

            @Override
            public void onFailure(int i) {
                Log.v(Global.TAG, "discoverServices.onFailure");
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

    public void createGroup() {
        removeGroup();
        if (wifiManager.isWifiEnabled()) {
            mManager.createGroup(mChannel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Log.v(Global.TAG, "createGroup.onSuccess");
                    startServerService();
                }

                @Override
                public void onFailure(int i) {
                    Log.v(Global.TAG, "createGroup.onFailure");
                }
            });
        }
    }

    public void startServerService() {
        if (!isMyServiceRunning()) {
            Intent i = new Intent(this, ServerIntentService.class);
            startService(i);
            Log.v(Global.TAG, "startServerService.firstTimeRun");
        } else {
            Log.v(Global.TAG, "startServerService.isRunning");
        }
    }

    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (ServerIntentService.class.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }


    public BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("NetworkCheckReceiver", "ConnectionChangeReceiver.onReceive()");
            if (intent.getAction() != null) {
                if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                    getConnectivityStatus(context);
                } else if (intent.getAction().equals(Global.SOCKET_ACTION)) {
                    if (intent.getStringExtra(Global.SOCKET_STATUS).equalsIgnoreCase("Receiving connection")) {
                        tvActivityLog.setText("");
                    }
                    CommonMethod.appendTv(tvActivityLog, intent.getStringExtra(Global.SOCKET_STATUS));
                }
            }
            getConnectivityStatus(context);
        }

        public void getConnectivityStatus(Context context) {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                if (null != activeNetwork) {
                    if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                        Log.v(Global.TAG, "Wifi Enabled");

                    }
                    if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                        Log.v(Global.TAG, "Mobile Data Enabled");
                        CheckConnectionAsyncTask checkConnectionAsyncTask = new CheckConnectionAsyncTask(DeliverDataActivity.this);
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

        createGroup();
        discoverService();

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
        unregisterReceiver(networkReceiver);

        removeGroup();
        unregisterLocalService();
        unregisterServiceRequest();
        disconnect();
        deletePersistentGroups();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(networkReceiver);
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
}