package com.example.fauza.datacourier;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.fauza.datacourier.constant.Global;
import com.example.fauza.datacourier.model.DataModel;
import com.example.fauza.datacourier.rest.ApiClient;
import com.example.fauza.datacourier.rest.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CollectDataActivity extends AppCompatActivity {

    public Context context = CollectDataActivity.this;

    @BindView(R.id.et_url)
    TextInputEditText etUrl;
    @BindView(R.id.lay_url)
    TextInputLayout layUrl;
    @BindView(R.id.bt_get)
    Button btGet;
    @BindView(R.id.lay_collect_data_activity)
    LinearLayout layCollectDataActivity;
    @BindView(R.id.pb_get_data)
    ProgressBar pbGetData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_data);
        ButterKnife.bind(this);

        etUrl.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    layUrl.setHint("Url");
                } else {
                    layUrl.setHint("example.com");
                }
            }
        });
    }

    @OnClick(R.id.bt_get)
    public void onCollectData() {
        showProgressBar();
        String url = "http://" + etUrl.getText().toString();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<List<DataModel>> call = apiInterface.getSensorData(url);
        try {
            Log.v(Global.TAG, url);
            call.enqueue(new Callback<List<DataModel>>() {
                @Override
                public void onResponse(@NonNull Call<List<DataModel>> call, @NonNull Response<List<DataModel>> response) {
                    List<DataModel> dataModels = response.body();
                    ArrayList<DataModel> dataModelArrayList = null;
                    if (dataModels != null) {
                        dataModelArrayList = new ArrayList<>(dataModels);
                    }
                    Intent testData = new Intent(CollectDataActivity.this, SaveDataActivity.class);
                    testData.putParcelableArrayListExtra("MY_PARCEL", dataModelArrayList);
                    startActivity(testData);
                    hideProgressBar();
                }

                @Override
                public void onFailure(@NonNull Call<List<DataModel>> call, @NonNull Throwable t) {
                    Log.v(Global.TAG, t.getMessage());
                    hideProgressBar();
                    snackbar("Error: Failed to Collect Data");
                }
            });
        } catch (Exception e) {
            hideProgressBar();
            e.printStackTrace();
            snackbar("Error: Invalid URL");
        }
    }

    public void snackbar(String message) {
        Snackbar.make(findViewById(R.id.lay_collect_data_activity), message, Snackbar.LENGTH_SHORT).show();
    }

    private void showProgressBar() {
        etUrl.setClickable(false);
        layUrl.setClickable(false);
        btGet.setClickable(false);
        layCollectDataActivity.setClickable(false);
        pbGetData.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        etUrl.setClickable(true);
        layUrl.setClickable(true);
        btGet.setClickable(true);
        layCollectDataActivity.setClickable(true);
        pbGetData.setVisibility(View.INVISIBLE);
    }
}
