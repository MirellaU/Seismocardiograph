package com.example.mirella.seismocardiograph;

import java.util.ArrayList;

public class HRDetectionActivity {

    public ArrayList<Double> accValues = new ArrayList<>();

    private void BandPassFilter(){

    }

    private void Smoothing(){

    }

    private ArrayList<Double> SignalSquare(){
        ArrayList<Double> squared = new ArrayList<>();
        for (int i=0; i<accValues.size(); i++) {
            squared.add(Math.exp(accValues.get(i)));
        }
        return squared;
    }

    private void PeakDetection(){

    }

}
