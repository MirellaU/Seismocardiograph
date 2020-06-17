package com.example.mirella.seismocardiograph;

import java.util.ArrayList;

public class HRDetectionActivity {

    public ArrayList<Double> accValues = new ArrayList<>();

//    f1 - najmniejsza częstotliwość
//    f2 - najwyższa częstotliwość
//    f_samp - częstotliwość próbkowania
//    N - rząd filtru
    private void BandPassFilter(double f1, double f2, double f_samp, int N){
        double f1c = f1/f_samp;
        double f2c = f2/f_samp;
        double omega1 = 2*Math.PI*f1c;
        double omega2 = 2*Math.PI*f2c;
        int middle = N/2;
        for(int i = (-N/2);i<(N/2); i++) {
            if(i==0){
                accValues.set(middle,2*(f2c - f1c));
            } else {
                accValues.set(middle+i, (Math.sin(omega2*i)/(Math.PI*i))-(Math.sin(omega1*i)/(Math.PI*i)));
            }
        }
    }

    //Potęgowanie sygnału
    private ArrayList<Double> SignalSquare(){
        ArrayList<Double> squared = new ArrayList<>();
        for (int i=0; i<accValues.size(); i++) {
            squared.add(Math.exp(accValues.get(i)));
        }
        return squared;
    }

    //Filtr ruchomej średniej
    private void Smoothing(int windowLength){
        //windowLength =10?
        for(int i=0; i<accValues.size()-windowLength+1; i++){
            accValues.set(i, (1/windowLength * accValues.get(i)));
        }
    }

    //Algorytm detekcji HR
    private void PeakDetection(){
        double max=0;
        double min=0;
        double lastMax=0;
        double lastMin=0;
        for(int i =0; i<accValues.size();i++){
            if(accValues.get(i)>lastMax) {
                max = accValues.get(i);
            }
            if(accValues.get(i)<lastMin) {
                min = accValues.get(i);
            }
            lastMax= accValues.get(i-1);
            lastMin = accValues.get(i-1);
        }
        double signal = max;
        double noise = min;
        double treshold = signal - 0.3 * signal;
        int peakPlace;
        int lastPeakPlace=0;
        int HR=0;
        double temp1;
        double temp2;
        for(int i = 0;i<accValues.size();i++){
            if(treshold<accValues.get(i)){
                peakPlace =i;
                temp1 = accValues.get(i);
                if(peakPlace>lastPeakPlace+30){
                    HR+=1;
                    signal = 0.125*temp1+0.875*signal;
                }
                lastPeakPlace=peakPlace;
            } else {
                temp2=accValues.get(i);
                noise=0.125*temp2 + 0.875*noise;
            }
            treshold = noise+0.5*(signal-noise);
        }
    }

}
