package com.example.mirella.seismocardiograph;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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

@SuppressWarnings("unchecked")
public class StartActivity extends AppCompatActivity {

    private static final String TAG = "AccSensor";
    private static final String ACC_VALUES = "NEW_ACC_VALUES";
    private boolean doubleTap = false;
    private boolean saveToFile = false;
    private int i = 1;

    private final Menu[] menu = {
            new Menu(R.string.test_start, R.drawable.start),
            new Menu(R.string.test_stop, R.drawable.stop),
            new Menu(R.string.save, R.drawable.save),
            new Menu(R.string.show_plot, R.drawable.plots)
    };

    private ArrayList<Float> accValues = new ArrayList<>();
    private final ArrayList<Float> accSaveXValues = new ArrayList<>();
    private final ArrayList<Float> accSaveYValues = new ArrayList<>();
    private final ArrayList<Float> accSaveZValues = new ArrayList<>();

    @BindView(R.id.menuGridView)
    GridView menuGridView;

    private final BroadcastReceiver accValuesReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(ACC_VALUES)) {
                //noinspection unchecked
                accValues = (ArrayList<Float>) intent.getSerializableExtra("ACC_VALUES");
                accSaveXValues.add(accValues.get(0));
                accSaveYValues.add(accValues.get(1));
                accSaveZValues.add(accValues.get(2));
            }
        }
    };

    private void StartTest(){
        final Intent serviceIntent = new Intent(this, AccService.class);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.ExaminationInfo)
                .setMessage(R.string.ExaminationInfoText)
                .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        doubleTap=true;
                        saveToFile=true;
                        startService(serviceIntent);
                        Toast.makeText(getApplicationContext(), R.string.ExaminationStart, Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton(R.string.ExaminationNotIntentToStart, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        doubleTap=false;
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();
    }

    private void StopTest(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.ExaminationStopInfo)
                .setMessage(R.string.ExaminationIntentToStop)
                .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        doubleTap=false;
                        stopService(new Intent(StartActivity.this, AccService.class));
                        Toast.makeText(getApplicationContext(), R.string.ExaminationStop, Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton(R.string.ExaminationNotStop, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        doubleTap=true;
                    }
                });
        // Create the AlertDialog object
        AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();
    }

    private void ShowPlot(){
        Intent intent = new Intent(StartActivity.this,PlotActivity.class);
        startActivity(intent);
    }

    private void SaveDataToCSV() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.Save)
                .setPositiveButton(R.string.Save, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(accSaveXValues.size()!=0 && accSaveYValues.size()!=0 && accSaveZValues.size()!=0) {
                            Save();
                            accSaveXValues.clear();
                            accSaveYValues.clear();
                            accSaveZValues.clear();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), R.string.ExaminationNotStartInfo, Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton(R.string.DontSave, new DialogInterface.OnClickListener() {
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
        try {
            final File directory = getExternalFilesDir(null); //for external storage
            final String fileName = "Seismocardiography_data" + i + ".csv";
            File file = new File(directory, fileName);
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
            i++;
            //saved in /storage/emulated/0/Android/data/com.example.mirella.seismocardiograph/files
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

                if(position==0 && !doubleTap){
                    StartTest();
                }
                else if (position==1 && doubleTap){
                    StopTest();
                }
                else if (position==2){
                    if(!saveToFile) {
                        Toast.makeText(getApplicationContext(),R.string.ExaminationNotStartInfo,Toast.LENGTH_LONG);
                    } else {
                        SaveDataToCSV();
                    }
                }
                else if (position==3){
                    ShowPlot();
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.up_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.info) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.ExaminationInfo)
                    .setMessage(R.string.ExaminationInfoText)
                    .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }
}
