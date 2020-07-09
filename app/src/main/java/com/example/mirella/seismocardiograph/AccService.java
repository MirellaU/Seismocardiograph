package com.example.mirella.seismocardiograph;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;

/**
 * Serwis zczytuje wartości z akcelerometru telefonu, a następnie wysyła je do aktywności StartActivity
 * i Plotactivity. odczyt wartości następuje po każdej zmianie wartości.
 *
 * @author Mirella
 * @version 1.0
 */
public class AccService extends JobIntentService implements SensorEventListener {

    /**
     * TAG używany do odczytu logów z tej Aktywności.
     */
    private static final String TAG = "ServiceActivity";

    /**
     * Tablica przechowująca wartości odczytane z czujnika dla osi X, Y i Z.
     */
    private final ArrayList<Double> accValues = new ArrayList<>();

    /**
     * Inicjalizacja menadżera zarządzającego czujnikami w telefonie.
     */
    private SensorManager mSensorManager;

    /**
     * Inicjalizacja czujnika.
     */
    private Sensor mAccelerometer;

    /**
     * Wywoływana w momencie rozpoczęcia pracy serwisu.
     *
     * @param intent    Intencja informująca o rozpoczęciu pracy serwisu.
     * @param flags     Dodatkowe informacje o pracy serwisu.
     * @param startId   Unikatowy klucz.
     * @return          Stała zapewniająca ciągłą pracę serwisu.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY; // want service to continue running until its explicitly stopped so return sticky
    }

    /**
     * Funkcja główna klasy.
     * Definiuje typ czujnika jako TYPE_LINEAR_ACCELERATION.
     * W przypadku niezarejetrowania akcelerometru w telefonie wyświetla użytkownikowi AlertDialog o jego braku.
     * Definiuje częstotliwość próbkowania na 100Hz.
     */
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
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
    }

    /**
     * Wywoływana w momencie zmiany rozdzielczości zapisu z czujnika.
     *
     * @param sensor    Typ czujnika.
     * @param i         Rozdzielczość.
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) { }

    /**
     * Wywoływana w momencie pojawienia się nowego zdarzenia z czujnika.
     * Zapisuje wartości odczytane z czujnika dla osi X,Y i Z do tablicy accValues.
     *
     * @param event Zdarzenie typu nowy odczyt z czujnika.
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        double lastX = event.values[0];
        double lastY = event.values[1];
        double lastZ = event.values[2];
        if(lastZ <0.5 && lastZ >0){
            accValues.add(lastX);
            accValues.add(lastY);
            accValues.add(lastZ);
            send();
        }
        accValues.clear();
    }

    /**
     * Zakończenie nasłuchu danych przez menadżer czujnika po wystąpieniu błędu aplikacji.
     */
    @Override
    public void onDestroy() {
        mSensorManager.unregisterListener(this);
    }

    /**
     * Wywoływana w momencie realizowania wielu zadań w ramach jednego serwisu.
     *
     * @param intent    Intencja rozpoczynająca kolejną pracę serwisu.
     */
    @Override
    protected void onHandleWork(@NonNull Intent intent) { }

    /**
     * Funkcja wysyła zapisane w tablicy accValues wartości z etykietą "NEW_ACC_VALUES".
     */
    private void send() {
        Intent accValuesIntent = new Intent("NEW_ACC_VALUES");
        accValuesIntent.putExtra("ACC_VALUES", accValues);
        sendBroadcast(accValuesIntent);
    }
}
