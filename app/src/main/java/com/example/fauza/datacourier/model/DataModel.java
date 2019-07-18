package com.example.fauza.datacourier.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class DataModel implements Parcelable {
    @SerializedName("data_id")
    private String data_id;
    @SerializedName("suhu")
    private String suhu;
    @SerializedName("kelembaban")
    private String kelembaban;
    @SerializedName("kelembabanTanah")
    private String kelembabanTanah;
    @SerializedName("intensitasCahaya")
    private String intensitasCahaya;

    public DataModel(String data_id, String suhu, String kelembaban, String kelembabanTanah, String intensitasCahaya) {
        this.data_id = data_id;
        this.suhu = suhu;
        this.kelembaban = kelembaban;
        this.kelembabanTanah = kelembabanTanah;
        this.intensitasCahaya = intensitasCahaya;
    }

    public String getData_id() {
        return data_id;
    }

    public void setData_id(String data_id) {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.data_id);
        dest.writeString(this.suhu);
        dest.writeString(this.kelembaban);
        dest.writeString(this.kelembabanTanah);
        dest.writeString(this.intensitasCahaya);
    }

    protected DataModel(Parcel in) {
        this.data_id = in.readString();
        this.suhu = in.readString();
        this.kelembaban = in.readString();
        this.kelembabanTanah = in.readString();
        this.intensitasCahaya = in.readString();
    }

    public static final Creator<DataModel> CREATOR = new Creator<DataModel>() {
        @Override
        public DataModel createFromParcel(Parcel source) {
            return new DataModel(source);
        }

        @Override
        public DataModel[] newArray(int size) {
            return new DataModel[size];
        }
    };
}
