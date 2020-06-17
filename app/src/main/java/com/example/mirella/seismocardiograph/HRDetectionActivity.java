package com.example.mirella.seismocardiograph;

import java.util.ArrayList;

public class HRDetectionActivity {

    public int HRValue;
//    f1 - najmniejsza częstotliwość
//    f2 - najwyższa częstotliwość
//    f_samp - częstotliwość próbkowania
//    N - rząd filtru
    public void BandPassFilter(float f1, float f2, int f_samp, int N,  ArrayList<Float> HRValues) {
        ArrayList<Float> filter = new ArrayList<>();
        float f1c = f1/f_samp;
        float f2c = f2/f_samp;
        float omega1 = (float) (2*Math.PI*f1c);
        float omega2 = (float) (2*Math.PI*f2c);
        int middle = N/2;
        for(int i = (-N/2);i<(N/2); i++) {
            if(i==0){
                filter.add(middle,2*(f2c - f1c));
            } else {
                filter.add(middle+i, (float) ((Math.sin(omega2*i)/(Math.PI*i))-(Math.sin(omega1*i)/(Math.PI*i))));
            }
            int stop=0;
        }

        for(int i =0; i<HRValues.size(); i++){
            HRValues.set(i,HRValues.get(i)*filter.get(i));
        }
    }

    //Potęgowanie sygnału
    public void SignalSquare(ArrayList<Float> HRValues){
        for (int i=0; i<HRValues.size(); i++) {
            HRValues.set(i,(float) Math.exp(HRValues.get(i)));
        }
    }

    //Filtr ruchomej średniej
    public void Smoothing(int windowLength, ArrayList<Float> HRValues){
        //windowLength =10?
        for(int i=0; i<HRValues.size()-windowLength+1; i++){
            HRValues.set(i, (1/windowLength * HRValues.get(i)));
        }
    }

    //Algorytm detekcji HR
    public void PeakDetection(ArrayList<Float> HRValues){
        double max=0;
        double min=0;
        double lastMax=0;
        double lastMin=0;
        for(int i =1; i<HRValues.size();i++){
            if(HRValues.get(i)>lastMax) {
                max = HRValues.get(i);
            }
            if(HRValues.get(i)<lastMin) {
                min = HRValues.get(i);
            }
            lastMax= HRValues.get(i-1);
            lastMin = HRValues.get(i-1);
        }
        double signal = max;
        double noise = min;
        double threshold = signal - 0.3 * signal;
        int peakPlace;
        int lastPeakPlace=0;
        double temp1;
        double temp2;
        for(int i = 0;i<HRValues.size();i++){
            if(threshold<HRValues.get(i)){
                peakPlace =i;
                temp1 = HRValues.get(i);
                if(peakPlace>lastPeakPlace+30){
                    HRValue+=1;
                    signal = 0.125*temp1+0.875*signal;
                }
                lastPeakPlace=peakPlace;
            } else {
                temp2=HRValues.get(i);
                noise=0.125*temp2 + 0.875*noise;
            }
            threshold = noise+0.5*(signal-noise);
        }
    }

}
