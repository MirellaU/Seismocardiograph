package com.example.mirella.seismocardiograph;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Aktywność obsługująca ikonę "Pokaż wykresy" z menu aplikacji.
 * Zarządza sposobem wyświetlania wykresów i danymi na wykresie, odbiera dane z serwisu AccService,
 * zawiera zmienne do filtracji i analizy sygnału. Wyświetla tętno w aktywności.
 *
 * @author Mirella
 * @version 1.0
 */
public class PlotActivity extends AppCompatActivity {

    /**
     * TAG używany do odczytu logów z tej Aktywności.
     */
    private static final String TAG = "PlotActivity";

    /**
     * Etykieta danych napływających z AccService.
     */
    private static final String ACC_VALUES = "NEW_ACC_VALUES";

    /**
     * Wysokość i szerokość siatki układu.
     */
    private static int width; //linear layout width
    private static int height; //linear layout height

    /**
     * Tablice przechowujące wartości z osi X,Y,Z akcelerometru.
     */
    private final ArrayList<Float> accXValues = new ArrayList<>();
    private final ArrayList<Float> accYValues = new ArrayList<>();
    private final ArrayList<Float> accZValues = new ArrayList<>();

    //HR detection variables
    /**
     * Tablica przechwująca wartości osi Z, używana do analizy sygnału.
     */
    private final ArrayList<Float> HRValues = new ArrayList<>();

    /**
     * Tablica przechwująca wartości po analizie sygnału, używana do wyświetlania sygnału na wykresie.
     */
    private final ArrayList<Float> HRPlotValues = new ArrayList<>();

    /**
     * Tablica przechwująca wartości po analizie sygnału, używana do obliczenia i wyświetlenia
     * użytkownikowi wartości tętna.
     */
    private final ArrayList<Double> displayedHR = new ArrayList<>();

    /**
     * Instancja klasy HRDetectionActivity.
     */
    private final HRDetectionActivity HR = new HRDetectionActivity();

    /**
     * Suma kolejnych wartości wykrytych pików.
     */
    private int sum = 0;
    /**
     * Ilość kolejnych detekcji pików.
     */
    private int i = 1;
    /**
     * Indeks tablicy displayedHR.
     */
    private int index=0;

    /**
     * Wykres wartości X.
     */
    @BindView(R.id.accXChartID)
    LineChart accXChart;
    /**
     * Wykres wartości Y.
     */
    @BindView(R.id.accYChartID)
    LineChart accYChart;
    /**
     * Wykres wartości Z.
     */
    @BindView(R.id.accZChartID)
    LineChart accZChart;
    /**
     * Wykres wartości HR.
     */
    @BindView(R.id.HRChartID)
    LineChart HRChart;
    /**
     * Wartość HR wyświetlana uzytkownikowi.
     */
    @BindView(R.id.HRValue)
    TextView HRValue;
    /**
     * Checkbox wykresu osi X.
     */
    @BindView(R.id.checkboxXAxis)
    CheckBox checkboxXAxis;
    /**
     * Checkbox wykresu osi Y.
     */
    @BindView(R.id.checkboxYAxis)
    CheckBox checkboxYAxis;
    /**
     * Checkbox wykresu osi Z.
     */
    @BindView(R.id.checkboxZAxis)
    CheckBox checkboxZAxis;
    /**
     * Linear layout.
     */
    @BindView(R.id.linearLayout)
    LinearLayout linearLayout;

    /**
     * Odbiornik transmisji, odbiera wartości przekazywane przez AccService
     * i zapisuje do tablic. Reaguje tylko na wiadomości z etykietą "NEW_ACC_VALUES".
     * Wywołuje funkcje addAccValuesEntry dodającą wartości osi X,Y,Z na odpowiadające im wykresy.
     * Wywołuje funkcje isCheckBoxChecked.
     * Po osiągnięciu przez tablicę HRValues wielkości równej 100 (odpowiadającej 1s) wywołuje funkcję findPeaks.
     */
    private final BroadcastReceiver accValuesReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACC_VALUES)) {
                ArrayList<Double> accValues = (ArrayList<Double>) intent.getSerializableExtra("ACC_VALUES");
                double x = accValues.get(0);
                double y = accValues.get(1);
                double z = accValues.get(2);
                accXValues.add((float) x);
                accYValues.add((float) y);
                accZValues.add((float) z);
                HRValues.add((float) z);
                addAccValuesEntry(accXChart, accXValues, Color.BLUE);
                addAccValuesEntry(accYChart, accYValues, Color.GREEN);
                addAccValuesEntry(accZChart, accZValues, Color.RED);
                isCheckBoxChecked();
                if (HRValues.size() > 99) {
                    findPeaks();
                }
            }
        }
    };

    /**
     * Znajduje piki sygnału odpowiadające biciu serca. Wywołuje filtr Butterwortha z klasy
     * BandpassFilterButterworthImplementation na tablicy HRValues, następnie poddaje sygnał
     * potęgowaniu (funkcja signalSquare), wygładzaniu (funkcja smoothing) i detekcji pików (funkcja peakDetection).
     * Na końcu wyświetla sygnał na wykresie i podaje obliczoną wg wzoru wartość tętna.
     */
    private void findPeaks() {
        int n = 6;
        int f_samp = 100;
        float f2 = 20;
        float f1 = 15;
        BandpassFilterButterworthImplementation BPF = new BandpassFilterButterworthImplementation(f1, f2, n, f_samp);
        for (int i = 0; i < HRValues.size(); i++) {
            BPF.compute(HRValues.get(i));
        }
        HR.signalSquare(HRValues);
        int windowLength = 10;
        HR.smoothing(windowLength, HRValues);
        HRPlotValues.addAll(HRValues);
        int HRVal = HR.peakDetection(HRValues);
        addHRValuesEntry(HRChart, HRPlotValues, Color.RED);
        HRValues.clear();
        if (HRVal > 0) {
            sum = sum + HRVal;
            float val = sum * 100 / i;
            displayedHR.add(index, (double) (val / 100 * 60));
            String textViewText = "TĘTNO " + String.format("%1$.0f", displayedHR.get(displayedHR.size() - 1));
            HRValue.setText(textViewText);
            i++;
            index++;
            if (displayedHR.size() > 10) {
                displayedHR.clear();
                index=0;
            }
        }
    }

    /**
     * Wyświetlanie wykresu z wartościami przyśpieszeń odczytanymi z akcelerometru dla osi X, Y i Z.
     *
     * @param lineChart  Zmienna przechwująca nazwę wykresu wyświetlającego wartości.
     * @param val        Lista z wartościami wyświetlanymi na wykresie.
     * @param color      Kolor wyświetlanej funkcji.
     */
    private void addAccValuesEntry(LineChart lineChart, ArrayList val, int color) {
        LineData data = lineChart.getData();

        if (data == null) {
            data = new LineData();
            lineChart.setData(new LineData());
        }

        ArrayList<Entry> values = new ArrayList<>();

        for (int i = 0; i < val.size(); i++) {
            values.add(new Entry(i, (float) val.get(i)));
        }
        removeDataSet(lineChart);

        LineDataSet lds = new LineDataSet(values, "");

        lds.setLineWidth(2.5f);
        lds.setDrawCircles(false);
        lds.setColor(color);
        lds.setHighLightColor(color);
        lds.setValueTextSize(0f);

        data.addDataSet(lds);
        data.notifyDataChanged();

        lineChart.notifyDataSetChanged();
        lineChart.invalidate();

        lineChart.setVisibleXRangeMaximum(100);
        lineChart.moveViewToX(val.size());

        if (val.size() > 100) {
            val.remove(val.size() - 99);
        }
    }

    /**
     * Wyświetlanie wykresu z wartościami HR.
     *
     * @param lineChart Zmienna przechwująca nazwę wykresu wyświetlającego wartości.
     * @param val       Lista z wartościami wyświetlanymi na wykresie.
     * @param color     Kolor wyświetlanej funkcji.
     */
    private void addHRValuesEntry(LineChart lineChart, ArrayList val, int color) {
        LineData data = lineChart.getData();

        if (data == null) {
            data = new LineData();
            lineChart.setData(new LineData());
        }

        ArrayList<Entry> values = new ArrayList<>();

        for (int i = 0; i < val.size(); i++) {
            values.add(new Entry(i, (float) val.get(i)));
        }
        removeDataSet(lineChart);

        LineDataSet lds = new LineDataSet(values, "");

        lds.setLineWidth(2.5f);
        lds.setDrawCircles(false);
        lds.setColor(color);
        lds.setHighLightColor(color);
        lds.setValueTextSize(0f);

        data.addDataSet(lds);
        data.notifyDataChanged();

        lineChart.notifyDataSetChanged();
        lineChart.invalidate();

        lineChart.setVisibleXRangeMaximum(200);
        lineChart.moveViewToX(val.size());

        if (val.size() > 200) {
            val.remove(val.size() - 199);
        }
    }

    /**
     * Usuwanie zestawu danych z wykresu.
     *
     * @param chart  Nazwa wykresu.
     */
    private void removeDataSet(LineChart chart) {
        LineData data = chart.getData();
        if (data != null) {
            data.removeDataSet(data.getDataSetByIndex(data.getDataSetCount() - 1));
            chart.notifyDataSetChanged();
            chart.invalidate();
        }
    }

    /**
     * Konfiguracja wykresów danych z akcelerometru, zawiera paramtery wyświetlania osi X i Y.
     *
     * @param chart  Nazwa wykresu.
     * @param label  Nazwa zestawu danych.
     */
    private void chartConfiguration(LineChart chart, String label) {
        chart.setKeepPositionOnRotation(true);
        chart.getDescription().setEnabled(true);
        chart.getDescription().setText(label);
        chart.getDescription().setTextSize(20);
        chart.getAxisRight().setDrawLabels(false);
        chart.getLegend().setEnabled(false);
        chart.fitScreen();

        final LineData data = new LineData();
        chart.setData(data);

        //accChart.fitScreen();
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawGridLines(false); // no grid lines
        leftAxis.setAxisMinimum(-1f); // start at -1
        leftAxis.setAxisMaximum(1f); // the axis maximum is 1

        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawGridLines(false); //no grid lines
        chart.getXAxis().setDrawLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);

        IAxisValueFormatter xAxisFormatter = new IAxisValueFormatter() {
            // @Override
            public String getFormattedValue(float value, AxisBase axis) {
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss", Locale.GERMAN);
                String time = sdf.format(new Date());
                return time;
            }
        };
        xAxis.setValueFormatter(xAxisFormatter);
    }

    /**
     * Konfiguracja wykresu HR, zawiera paramtery wyświetlania osi X i Y.
     *
     * @param chart  Nazwa wykresu.
     */
    private void hrChartConfiguration(LineChart chart) {
        chart.setKeepPositionOnRotation(true);
        chart.getDescription().setEnabled(true);
        chart.getDescription().setText("Tętno");
        chart.getDescription().setTextSize(20);
        chart.getAxisRight().setDrawLabels(false);
        chart.getLegend().setEnabled(false);
        chart.fitScreen();

        final LineData data = new LineData();
        chart.setData(data);

        //accChart.fitScreen();
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawGridLines(false); // no grid lines
        leftAxis.setAxisMinimum(0f); // start at -1
        leftAxis.setAxisMaximum(0.2f); // the axis maximum is 1

        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawGridLines(false); //no grid lines
        chart.getXAxis().setDrawLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);

        IAxisValueFormatter xAxisFormatter = new IAxisValueFormatter() {
            // @Override
            public String getFormattedValue(float value, AxisBase axis) {
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss", Locale.GERMAN);
                String time = sdf.format(new Date());
                return time;
            }
        };
        xAxis.setValueFormatter(xAxisFormatter);
    }

    /**
     * Sprawdza który z checkboxów został zaznaczony.
     * Wyświetla te wykresy dla których zaznaczono odpowiadający im checkbox.
     */
    private void isCheckBoxChecked() {
        width = linearLayout.getWidth();
        height = linearLayout.getHeight();

        if (checkboxXAxis.isChecked()) {
            accXChart.setVisibility(View.VISIBLE);
            accXChart.setLayoutParams(new LinearLayout.LayoutParams(width, 700));

        } else {
            accXChart.setVisibility(View.INVISIBLE);
            accXChart.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        }
        if (checkboxYAxis.isChecked()) {
            accYChart.setVisibility(View.VISIBLE);
            accYChart.setLayoutParams(new LinearLayout.LayoutParams(width, 700));
        } else {
            accYChart.setVisibility(View.INVISIBLE);
            accYChart.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        }
        if (checkboxZAxis.isChecked()) {
            accZChart.setVisibility(View.VISIBLE);
            accZChart.setLayoutParams(new LinearLayout.LayoutParams(width, 700));
        } else {
            accZChart.setVisibility(View.INVISIBLE);
            accZChart.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        }
    }

    /**
     * Metoda główna aktywności.
     * Rejestruje odbiornik transmisji accValuesReceiver i tworzy obiekt typu IntentFilter,
     * który filtruje informacje pochodzące z AccService i pozwala na odbiór tylko tych z etykietą "NEW_ACC_VALUES".
     * Ustawia szerokość i wysokość layoutu dla prawidłowego wyświetlania wykresów w aplikacji.
     * Konfiguruje sposoby wyświetlania wykresów (funkcja setLayoutParams).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plot);
        ButterKnife.bind(this);

        IntentFilter accValuesIntentFilter = new IntentFilter("NEW_ACC_VALUES");
        registerReceiver(accValuesReceiver, accValuesIntentFilter);

        accXChart.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        accYChart.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        accZChart.setLayoutParams(new LinearLayout.LayoutParams(1050, 700));
        HRChart.setLayoutParams(new LinearLayout.LayoutParams(1050, 700));

        //chart options
        chartConfiguration(accXChart, "Oś X");
        chartConfiguration(accYChart, "Oś Y");
        chartConfiguration(accZChart, "Oś Z");
        hrChartConfiguration(HRChart);
    }

    /**
     * Określa zachowanie Aktywności w momencie zatrzymania się aplikacji.
     */
    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     * Zakończenie nasłuchu danych przez odbiornik transmisji po wystąpieniu błędu aplikacji.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(accValuesReceiver);
    }

}
