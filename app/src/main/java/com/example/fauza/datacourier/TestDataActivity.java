package com.example.fauza.datacourier;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.example.fauza.datacourier.asynctask.DeleteTestDataAsyncTask;
import com.example.fauza.datacourier.asynctask.TestDataReadAsyncTask;
import com.example.fauza.datacourier.entity.TestData;
import com.example.fauza.datacourier.interfaces.TestDataReadInterface;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TestDataActivity extends AppCompatActivity implements TestDataReadInterface {

    @BindView(R.id.tv_test_data)
    TextView tvTestData;
    @BindView(R.id.bt_delete_all_data)
    Button btDeleteAllData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_data);
        ButterKnife.bind(this);
        TestDataReadAsyncTask testDataReadAsyncTask = new TestDataReadAsyncTask(this, this);
        testDataReadAsyncTask.execute();
    }

    @Override
    public void readTestDataCallBack(List<TestData> testData) {
        for (TestData td : testData) {
            long timeDelta = (td.getTimeStampStart() - td.getTimeStampEnd());
            String show = String.format("%d %s %d \n", td.getId(), "-", timeDelta);
            tvTestData.append(show);
        }
    }

    @OnClick(R.id.bt_delete_all_data)
    public void onViewClicked() {
        DeleteTestDataAsyncTask deleteTestDataAsyncTask = new DeleteTestDataAsyncTask(this);
        deleteTestDataAsyncTask.execute();
    }
}
