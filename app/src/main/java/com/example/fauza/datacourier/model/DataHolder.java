package com.example.fauza.datacourier.model;

public class DataHolder {
    private String suhu;
    private String kelembaban;
    private String kelembabanTanah;
    private String intensitasCahaya;

    public DataHolder(String suhu, String kelembaban, String kelembabanTanah, String intensitasCahaya) {
        this.suhu = suhu;
        this.kelembaban = kelembaban;
        this.kelembabanTanah = kelembabanTanah;
        this.intensitasCahaya = intensitasCahaya;
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
