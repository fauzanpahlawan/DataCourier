package com.example.fauza.datacourier.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.fauza.datacourier.entity.TestData;

import java.util.List;

@Dao
public interface TestDataDao {
    @Insert
    void insertAll(List<TestData> testData);

    @Insert
    void insert(TestData testData);

    @Query("SELECT * FROM testDataTable")
    List<TestData> getAll();

    @Query("DELETE FROM testDataTable")
    void deleteAllData();
}
