package com.example.fauza.datacourier.rest;

import com.example.fauza.datacourier.model.DataModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface ApiInterface {
    @GET
    Call<List<DataModel>> getSensorData(@Url String url);
}
