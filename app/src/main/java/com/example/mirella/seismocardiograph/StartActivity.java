package com.example.mirella.seismocardiograph;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.mirella.seismocardiograph.PlotActivity.ACC_VALUES;

public class StartActivity extends AppCompatActivity {

    public static final String TAG = "AccSensor";
    public static String ACC_VALUES = "NEW_ACC_VALUES";

    private Menu[] menu = {
            new Menu(R.string.test_start, R.drawable.start),
            new Menu(R.string.test_stop, R.drawable.stop),
            new Menu(R.string.save, R.drawable.save),
            new Menu(R.string.show_plot, R.drawable.plots)
    };

    public ArrayList<Float> accValues = new ArrayList<>();
    public ArrayList<Float> accSaveXValues = new ArrayList<>();
    public ArrayList<Float> accSaveYValues = new ArrayList<>();
    public ArrayList<Float> accSaveZValues = new ArrayList<>();

    @BindView(R.id.menuGridView)
    GridView menuGridView;

    private BroadcastReceiver accValuesReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(ACC_VALUES)) {
                accValues = (ArrayList<Float>) intent.getSerializableExtra("ACC_VALUES");
                accSaveXValues.add(accValues.get(0));
                accSaveYValues.add(accValues.get(1));
                accSaveZValues.add(accValues.get(2));
            }
        }
    };

    public void StartTest(){
        //Intent intent = new Intent(StartActivity.this,PlotActivity.class);
        //startActivity(intent);
        final Intent serviceIntent = new Intent(this, AccService.class);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Informacja o badaniu")
                .setMessage("W celu poprawnego wykonania badania należy przyłożyć telefon na środku klatki piersiowej")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startService(serviceIntent);
                        Toast.makeText(getApplicationContext(), "Badanie rozpoczęte", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Nie chcę rozpoczynać badania", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        //.setView();
        // Create the AlertDialog object
        AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();
        Log.d(TAG,"Start service");
        //AccService.enqueueWork(this, serviceIntent);
    }

    public void StopTest(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Przerwanie badania")
                .setMessage("Czy na pewno chcesz przerwać badanie?")
                .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        stopService(new Intent(StartActivity.this, AccService.class));
                        Toast.makeText(getApplicationContext(), "Badanie przerwane", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Nie chcę przerywać badania", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        //.setView();
        // Create the AlertDialog object
        AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();
        Log.d(TAG,"Start service");
    }

    public void ShowPlot(){
        Intent intent = new Intent(StartActivity.this,PlotActivity.class);
        startActivity(intent);
    }

    public void SaveDataToCSV() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Czy chcesz zapisać wynik do pliku .csv?")
                .setPositiveButton("Zapisz do pliku", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(accSaveXValues.size()!=0 && accSaveYValues.size()!=0 && accSaveZValues.size()!=0) {
                            Save();
                            Toast.makeText(getApplicationContext(), "Zapisano pomyślnie", Toast.LENGTH_LONG).show();
                            accSaveXValues.clear();
                            accSaveYValues.clear();
                            accSaveZValues.clear();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Nie rozpoczęto badania!", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton("Nie zapisuj", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        // Create the AlertDialog object
        AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();
    }

    private void Save(){
        String timeStamp = new SimpleDateFormat(getString(R.string.dateFormat)).format(Calendar.getInstance().getTime());
        //String csv = android.os.Environment.getExternalStorageDirectory() + "/Download"+"/dane_z_ACC";
        //Log.d(TAG,csv);
        try {
            File directory = getExternalFilesDir(null); //for external storage
            String fileName = "dane_z_ACC.csv";
            File file = new File(directory, fileName);

            Log.d(TAG,directory.toString()); ///storage/emulated/0/Android/data/com.example.mirella.seismocardiograph/files
            // if file in directory not exists, create it
            if (!directory.exists()) {
                directory.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.append(timeStamp);
            bw.append("\n");
            bw.append("Oś X:");
            bw.append(accSaveXValues.toString());
            bw.append("\n");
            bw.append("Oś Y:");
            bw.append(accSaveYValues.toString());
            bw.append("\n");
            bw.append("Oś Z:");
            bw.append(accSaveZValues.toString());
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);

        IntentFilter accValuesIntentFilter = new IntentFilter("NEW_ACC_VALUES");
        registerReceiver(accValuesReceiver, accValuesIntentFilter);

        menuGridView.setAdapter(new MenuAdapter(this,menu));

        menuGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent,
                                    View v, int position, long id){
                if(position==0){
                 StartTest();
                }
                else if (position==1){
                    StopTest();
                }
                else if (position==2){
                    SaveDataToCSV();
                }
                else if (position==3){
                    ShowPlot();
                }

            }
        });
    }
}
