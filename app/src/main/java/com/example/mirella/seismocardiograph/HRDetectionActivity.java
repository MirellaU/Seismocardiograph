package com.example.mirella.seismocardiograph;

import android.util.Log;

import java.util.ArrayList;

class HRDetectionActivity {

    private static final String TAG = "HRClass";

    //Potęgowanie sygnału
    public void SignalSquare(ArrayList<Float> HRValues){
        for (int i=0; i<HRValues.size(); i++) {
            HRValues.set(i, HRValues.get(i)*HRValues.get(i));
        }
        //Log.d(TAG, "HR po exp: " + HRValues);
    }

    //Filtr ruchomej średniej
    public void Smoothing(int windowLength, ArrayList<Float> HRValues){
        //windowLength =10?
        float sum =0;
        float temp = 0;
        for(int i=0; i<HRValues.size(); i++){
            sum= sum + HRValues.get(i);
            if(i>windowLength-1) {
                temp = HRValues.get(i-windowLength);
                HRValues.set(i - windowLength, sum / windowLength);
            }
            if(i>HRValues.size()-windowLength){
                temp = HRValues.get(i-windowLength);
                HRValues.set(i, sum / windowLength);
            }
            sum = sum - temp;
        }
        //Log.d(TAG, "HR po wyg: " + HRValues);
    }

    //Algorytm detekcji HR
    public int PeakDetection(ArrayList<Float> HRValues){
        double avr=0;
        int HRValue=0;
        for(int i =0; i<HRValues.size();i++){
           avr=avr+HRValues.get(i);
        }
        avr=avr/HRValues.size();
        Log.d(TAG, "Signal " + avr);
        double signal = 1.2*avr;
        double noise = avr;
        //Log.d(TAG, "Signal " + String.valueOf(signal));
        //Log.d(TAG, "Noise "  + String.valueOf(noise));
        double threshold=noise+0.25*(signal-noise);
        Log.d(TAG, "Threshold "  + threshold);
        int peakPlace;
        int lastPeakPlace=0;
        double temp1;
        double temp2;
        for(int i = 0;i<HRValues.size();i++){
            if(threshold<HRValues.get(i)){
                peakPlace =i;
                temp1 = HRValues.get(i);
                Log.d(TAG, "Temp1 "  + temp1);
                if(peakPlace>lastPeakPlace+20){
                    HRValue+=1;
                    signal = 0.2*temp1+0.8*signal;
                Log.d(TAG, "Signal2 "  + signal);
                }
                lastPeakPlace=peakPlace;
            } else {
                temp2=HRValues.get(i);
                //Log.d(TAG, "Temp2 "  + String.valueOf(temp2));
                noise=0.2*temp2 + 0.8*noise;
                //Log.d(TAG, "Noise2 "  + String.valueOf(noise));
            }
            threshold =noise+0.8*(signal-noise);
            //Log.d(TAG, "Threshold2 "  + String.valueOf(threshold));
        }
        return HRValue;
    }

}
