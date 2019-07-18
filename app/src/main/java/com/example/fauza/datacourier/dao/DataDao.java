package com.example.fauza.datacourier.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.fauza.datacourier.entity.DataEntity;

import java.util.List;

@Dao
public interface DataDao {
    @Insert
    void insertAll(List<DataEntity> dataEntities);

    @Query("SELECT * FROM data")
    List<DataEntity> getAll();

    @Query("DELETE FROM data")
    void deleteAllData();
}
