package com.example.mirella.seismocardiograph;

import android.content.DialogInterface;
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
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StartActivity extends AppCompatActivity {

    public static final String TAG = "AccSensor";

    @BindView(R.id.startTestID)
    Button startTest;

    @BindView(R.id.showPlotID)
    Button showPlot;

    @OnClick(R.id.startTestID)
    public void StartTest(){
        //Intent intent = new Intent(StartActivity.this,PlotActivity.class);
        //startActivity(intent);
        final Intent serviceIntent = new Intent(this, AccService.class);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Informacja o badaniu")
                .setMessage("W celu poprawnego wykonania badania należy przyłożyć telefon na środku klatki piersiowej")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Save();
                        //rollValues.clear();
                        //pitchValues.clear();
                        startService(serviceIntent);
                        Toast.makeText(getApplicationContext(), "Badanie rozpoczęte", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Chcę wyjść z aplikacji", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
                    }
                });
        // Create the AlertDialog object
        AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();
        Log.d(TAG,"Start service");
        //AccService.enqueueWork(this, serviceIntent);
    }

    @OnClick(R.id.showPlotID)
    public void ShowPlot(){
        Intent intent = new Intent(StartActivity.this,PlotActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);
    }
}
