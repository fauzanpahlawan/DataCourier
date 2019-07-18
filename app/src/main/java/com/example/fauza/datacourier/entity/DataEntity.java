package com.example.fauza.datacourier.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "data")

public class DataEntity {
    @ColumnInfo(name = "data_id")
    @PrimaryKey
    @NonNull
    private String data_id;

    @ColumnInfo(name = "suhu")
    private String suhu;

    @ColumnInfo(name = "kelembaban")
    private String kelembaban;

    @ColumnInfo(name = "kelembabanTanah")
    private String kelembabanTanah;

    @ColumnInfo(name = "intensitasCahaya")
    private String intensitasCahaya;

    public DataEntity(@NonNull String data_id, String suhu, String kelembaban, String kelembabanTanah, String intensitasCahaya) {
        this.data_id = data_id;
        this.suhu = suhu;
        this.kelembaban = kelembaban;
        this.kelembabanTanah = kelembabanTanah;
        this.intensitasCahaya = intensitasCahaya;
    }

    @NonNull
    public String getData_id() {
        return data_id;
    }

    public void setData_id(@NonNull String data_id) {
        this.data_id = data_id;
    }

    public String getSuhu() {
        return suhu;
    }

    public void setSuhu(String suhu) {
        this.suhu = suhu;
    }

    public String getKelembaban() {
        return kelembaban;
    }

    public void setKelembaban(String kelembaban) {
        this.kelembaban = kelembaban;
    }

    public String getKelembabanTanah() {
        return kelembabanTanah;
    }

    public void setKelembabanTanah(String kelembabanTanah) {
        this.kelembabanTanah = kelembabanTanah;
    }

    public String getIntensitasCahaya() {
        return intensitasCahaya;
    }

    public void setIntensitasCahaya(String intensitasCahaya) {
        this.intensitasCahaya = intensitasCahaya;
    }
}
