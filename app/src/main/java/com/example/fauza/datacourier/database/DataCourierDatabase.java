package com.example.fauza.datacourier.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.fauza.datacourier.constant.Global;
import com.example.fauza.datacourier.dao.DataDao;
import com.example.fauza.datacourier.entity.DataEntity;

@Database(entities = {DataEntity.class}, version = 1, exportSchema = false)
public abstract class DataCourierDatabase extends RoomDatabase {

    public static DataCourierDatabase INSTANCE;

    public abstract DataDao getDataDao();

    public static DataCourierDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context, DataCourierDatabase.class, Global.DB_NAME).build();
        }
        return INSTANCE;
    }
}
