package com.example.fauza.datacourier.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "testDataTable")
public class TestData {
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int id;
    @ColumnInfo(name = "timeStampStart")
    private long timeStampStart;
    @ColumnInfo(name = "timeStampEnd")
    private long timeStampEnd;

    public TestData(long timeStampStart, long timeStampEnd) {
        this.timeStampStart = timeStampStart;
        this.timeStampEnd = timeStampEnd;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTimeStampStart() {
        return timeStampStart;
    }

    public void setTimeStampStart(long timeStampStart) {
        this.timeStampStart = timeStampStart;
    }

    public long getTimeStampEnd() {
        return timeStampEnd;
    }

    public void setTimeStampEnd(long timeStampEnd) {
        this.timeStampEnd = timeStampEnd;
    }
}
