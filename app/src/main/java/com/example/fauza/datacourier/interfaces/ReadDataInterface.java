package com.example.fauza.datacourier.interfaces;

import com.example.fauza.datacourier.entity.DataEntity;

import java.util.List;

public interface ReadDataInterface {
    void readDataCallBack(List<DataEntity> dataEntities);
}
