package com.example.mirella.seismocardiograph;

import android.app.IntentService;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.JobIntentService;
import android.util.Log;

import java.util.ArrayList;

public class AccService extends JobIntentService {

    public static final String TAG = "ServiceActivity";
    public static final int JOB_ID = 1;

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, AccService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(Intent intent) {
        ArrayList<Float> accValues = (ArrayList<Float>) intent.getSerializableExtra("ACC_VALUES");
        Intent accValuesIntent = new Intent("NEW_ACC_VALUES");
        accValuesIntent.putExtra("ACC_VALUES", accValues);
        Log.d(TAG,accValues.toString());
        sendBroadcast(accValuesIntent);
    }
}
