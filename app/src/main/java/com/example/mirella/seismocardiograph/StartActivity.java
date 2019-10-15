package com.example.mirella.seismocardiograph;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StartActivity extends AppCompatActivity {

    public static final String TAG = "AccSensor";

    @BindView(R.id.startTestID)
    Button startTest;

    @OnClick(R.id.startTestID)
    public void StartTest(){
        Intent intent = new Intent(StartActivity.this,PlotActivity.class);
        startActivity(intent);
        Intent serviceIntent = new Intent(this, AccService.class);
        Log.d(TAG,"Start service");
        //AccService.enqueueWork(this, serviceIntent);
        startService(serviceIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);
    }
}
