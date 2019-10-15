package com.example.mirella.seismocardiograph;

import android.app.IntentService;
import android.app.Notification;
import android.content.Context;
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

    public static final String TAG = "ServiceActivity";
    public static final int JOB_ID = 1;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    private float lastX, lastY, lastZ;

    private float deltaX = 0;
    private float deltaY = 0;
    private float deltaZ = 0;

    public ArrayList<Float> accValues = new ArrayList<>();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY; // want service to continue running until its explicitly stopped so return sticky
    }

    @Override
    public void onCreate() {
//        super.onCreate();
        try {
            mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        } catch (Exception e) {
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Nie wykryto akcelerometru. Nie można przeprowadzić badania.")
                    .setPositiveButton(android.R.string.ok, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        Log.d(TAG,"Service started");

    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) { }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) { return; }

    @Override
    public void onSensorChanged(SensorEvent event) {
        deltaX = Math.abs(lastX - event.values[0]);
        deltaY = Math.abs(lastY - event.values[1]);
        deltaZ = Math.abs(lastZ - event.values[2]);

        // if the change is below 2, it is just plain noise
        if (deltaX < 2)
            deltaX = 0;
        if (deltaY < 2)
            deltaY = 0;
        if (deltaZ < 2)
            deltaZ = 0;

        // set the last know values of x,y,z
        lastX = event.values[0];
        lastY = event.values[1];
        lastZ = event.values[2];

        //accValues.add(deltaX);
        //accValues.add(deltaY);
        accValues.add(deltaZ);
        Log.d(TAG, accValues.toString());
        if (accValues.size()!=0) {
            Send();
        } else{}
        accValues.clear();
//        final float alpha = 0.8;
//
//        // Isolate the force of gravity with the low-pass filter.
//        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
//        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
//        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];
//
//        // Remove the gravity contribution with the high-pass filter.
//        linear_acceleration[0] = event.values[0] - gravity[0];
//        linear_acceleration[1] = event.values[1] - gravity[1];
//        linear_acceleration[2] = event.values[2] - gravity[2];
        //mSensorManager.unregisterListener(this);
    }

    protected void Send() {
        Intent accValuesIntent = new Intent("NEW_ACC_VALUES");
        accValuesIntent.putExtra("ACC_VALUES", accValues);
        Log.d(TAG,accValues.toString());
        sendBroadcast(accValuesIntent);
    }

    @Override
    public void onDestroy() {
        mSensorManager.unregisterListener(this);
    }
}
