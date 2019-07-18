package com.example.fauza.datacourier.constant;

import com.example.fauza.datacourier.entity.DataEntity;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Global {
    public static final String DB_NAME = "data_courier-database";
    public static final int APP_PORT = 8888;
    public static final String SAVE_SUCCESS = "Save data success.";
    public static final String SAVE_FAILED = "Save data failed.";
    public static final String DATA_COURIER = "DATA_COURIER";


    //Service Discovery
    public static final int GROUP_OWNER_SERVICE_PORT = 8989;
    public static final int RECEIVER_SERVICE_PORT = 9090;
    public static final String KEY_LISTEN_PORT = "KEY_LISTEN_PORT";
    public static final String KEY_ROLE = "KEY_ROLE";
    public static final String KEY_STATUS = "KEY_STATUS";
    public static final String STATUS_AVAILABLE = "AVAILABLE";
    public static final String STATUS_UNAVAILABLE = "UNAVAILABLE";
    public static final String ROLE_RECEIVER = "RECEIVER";
    public static final String ROLE_GROUP_OWNER = "Group Owner";
    public static final String SERVICE_INSTANCE = "DataCourier";
    public static final String SERVICE_INSTANCE_RECEIVER = "DataCourier_Receiver";
    public static final String SERVICE_INSTANCE_GROUP_OWNER = "DataCourier_GroupOwner";
    public static final String SERVICE_TYPE = "_presence._tcp";

    public static final String ADD_SERVICE_REQUEST_SUCCESS = "addServiceRequest success";
    public static final String ADD_SERVICE_REQUEST_FAILED = "addServiceRequest failed";
    public static final String DISCOVER_SERVICE_SUCCESS = "discoverServices success";
    public static final String DISCOVER_SERVICE_FAILED = "discoverServices failed";

    //GSON
    public static final Type jsonToListDataEntity = new TypeToken<ArrayList<DataEntity>>() {
    }.getType();

    //RECORD TAGS
    public static final String TAG = "DATA_COURIER";
    public static final String TAG_RECORD_ACTIVITY_LOG = "DATCOUR_ACTIVITY_LOG";

    //REGEX
    public static final String REGEX_REMOVE_QUOTES = "^\"|\"$";

    //Broadcast
    public static final String SOCKET_ACTION = "com.example.fauza.datacourier.socket_action";
    public static final String SOCKET_STATUS = "com.example.fauza.datacourier.socket_status";

}
