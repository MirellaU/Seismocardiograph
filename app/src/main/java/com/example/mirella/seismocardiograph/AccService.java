package com.example.mirella.seismocardiograph;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import java.util.ArrayList;

public class AccService extends JobIntentService implements SensorEventListener {

    private static final String TAG = "ServiceActivity";

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    private final ArrayList<Double> accValues = new ArrayList<>();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY; // want service to continue running until its explicitly stopped so return sticky
    }

    @Override
    public void onCreate() {
        try {
            mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        } catch (Exception e) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.Error)
                    .setMessage(R.string.NoAcc)
                    .setPositiveButton(android.R.string.ok, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) { }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) { }

    @Override
    public void onSensorChanged(SensorEvent event) {
        double lastX = event.values[0];
        double lastY = event.values[1];
        double lastZ = event.values[2];

        if(lastZ <0.5 && lastZ >0){
            accValues.add(lastX);
            accValues.add(lastY);
            accValues.add(lastZ);
            Send();
        }
        accValues.clear();
    }

    private void Send() {
        Intent accValuesIntent = new Intent("NEW_ACC_VALUES");
        accValuesIntent.putExtra("ACC_VALUES", accValues);
        sendBroadcast(accValuesIntent);
    }

    @Override
    public void onDestroy() {
        mSensorManager.unregisterListener(this);
    }
}
