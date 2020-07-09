package com.example.mirella.seismocardiograph;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

/**
 * Aktywność obsługująca menu aplikacji.
 * Zarządza sposobem wyświetlania ikon menu w aplikacji, powiadomieniami, uruchamianiem i wyłączaniem serwisu AccService,
 * odbieraniem i zapisywaniem dancyh z serwisu.
 *
 * @author Mirella
 * @version 1.0
 */
public class StartActivity extends AppCompatActivity {
    /**
     * TAG używany do odczytu logów z tej Aktywności.
     */
    private static final String TAG = "StartActivity";

    /**
     * Etykieta danych napływających z AccService.
     */
    private static final String ACC_VALUES = "NEW_ACC_VALUES";

    /**
     * Obiekt klasy Menu zawierający String z nazwą ikony i odwołanie do folderu res z ID obrazka przypisanego do ikony.
     */
    private final Menu[] menu = {
            new Menu(R.string.test_start, R.drawable.start),
            new Menu(R.string.test_stop, R.drawable.stop),
            new Menu(R.string.save, R.drawable.save),
            new Menu(R.string.show_plot, R.drawable.plots)
    };

    /**
     * Tablice przechowujące wartości z osi X,Y,Z.
     */
    private final ArrayList<Float> accSaveXValues = new ArrayList<>();
    private final ArrayList<Float> accSaveYValues = new ArrayList<>();
    private final ArrayList<Float> accSaveZValues = new ArrayList<>();

    /**
     * Zmienna przechowująca dane o ilości wykonanych badań przy jednym uruchomieniu aplikacji,
     * używana przy zapisie do pliku .csv w celu nienadpisywania pliku.
     */
    private int i = 1;

    /**
     * Flaga informująca o szybkim, podwójnym kliknięciu na ikonę przez użytkownika.
     */
    private boolean doubleTap = false;

    /**
     * Flaga informująca o tym, czy odbył się już zapis do pliku aktualnego badania.
     */
    private boolean saveToFile = false;

    /**
     * Menu aplikacji.
     */
    @BindView(R.id.menuGridView)
    GridView menuGridView;

    /**
     * Odbiornik transmisji, odbiera wartości przekazywane przez AccService
     * i zapisuje do tablic. Reaguje tylko na wiadomości z etykietą "NEW_ACC_VALUES".
     */
    private final BroadcastReceiver accValuesReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(ACC_VALUES)) {
                ArrayList<Float> accValues = (ArrayList<Float>) intent.getSerializableExtra("ACC_VALUES");
                accSaveXValues.add(accValues.get(0));
                accSaveYValues.add(accValues.get(1));
                accSaveZValues.add(accValues.get(2));
            }
        }
    };

    /**
     * Inicjalizuje zawartość menu w aktywności.
     *
     * @param  menu Menu w którym umieszczono elementy.
     * @return      True jeżeli chcemy wyświetlić menu w aplikacji.
     */
    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.up_menu,menu);
        return true;
    }

    /**
     * Wywoływany za każdym razem po kliknięciu na ikonę "Rozpocznij badanie". W
     * yświetla informację w oknie dialogowym o sposobie poprawnego przeprowadzania badania.
     *
     * @param item  Wybrany obiekt z menu.
     * @return      Flaga o wykryciu kliknięcia w menu.
     */
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

    /**
     * Metoda główna aktywności.
     * Ustawia uklad menu wyświetlany w aktywności, rejestruje odbiornik transmisji accValuesReceiver i tworzy obiekt typu IntentFilter,
     * który filtruje informacje pochodzące z AccService i pozwala na odbiór tylko tych z etykietą "NEW_ACC_VALUES".
     * Przypisuje obsługę metod do ikon w aplikacji.
     */
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
                    startTest();
                } else if (position==1 && doubleTap){
                    stopTest();
                } else if (position==2){
                    if(!saveToFile) {
                        Toast.makeText(getApplicationContext(),R.string.ExaminationNotStartInfo,Toast.LENGTH_LONG);
                    } else {
                        saveDataToCSV();
                    }
                } else if (position==3){
                    showPlot();
                }
            }
        });
    }

    /**
     * Obsługa ikony "Rozpocznij badanie" z menu głównego aplikacji.
     * Tworzy obiekt typu AlertDialog pytający użytkownika o rozpoczęcie badania.
     * W razie zgody rozpoczyna działanie serwisu AccService, ustawia flagi doubleTap i saveToFile na true
     * Przy odmowie zamyka okno AlertDialog i ustawia flagę doubleTap na false.
     */
    private void startTest(){
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

    /**
     * Obsługa ikony "Zakończ badanie" z menu głównego aplikacji.
     * Tworzy obiekt typu AlertDialog pytający użytkownika o zakończenie badania.
     * W razie zgody zatrzymuje dzialanie serwisu AccService i ustawia flagę doubleTap na false..
     * Przy odmowie zamyka okno AlertDialog i ustawia flagę doubleTap na true..
     */
    private void stopTest(){
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

    /**
     * Obsługa ikony "Pokaż wykresy" z menu głównego aplikacji.
     * Przenosi użytkownika do aktywności PlotActivity.
     */
    private void showPlot(){
        Intent intent = new Intent(StartActivity.this,PlotActivity.class);
        startActivity(intent);
    }

    /**
     * Obsługa ikony "Zapisz wynik badania" z menu głównego aplikacji.
     * Tworzy obiekt typu AlertDialog pytający użytkownika o zapis do pliku.
     * W razie zgody zapisuje plik przy pomocy funkcji save() i czyści tablice z wartościami X,Y,Z.
     * Przy odmowie zamyka okno AlertDialog.
     */
    private void saveDataToCSV() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.Save)
                .setPositiveButton(R.string.DoSave, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(accSaveXValues.size()!=0 && accSaveYValues.size()!=0 && accSaveZValues.size()!=0) {
                            save();
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

    /**
     * Metoda zapisuje tablice z wartościami osi X, Y, Z odczytanymi z akcelerometru
     * do pliku Seismocadriography_data + numer kolejnego badania.csv.
     * Plik jest zapisywany w pamięci wewnętrznej telefonu w katalogu com.example.nazwa_uzytkownika.seimsocardiograph.
     */
    private void save(){
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
}
