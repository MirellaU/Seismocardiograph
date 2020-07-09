package com.example.mirella.seismocardiograph;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Adapter dla menu.
 *
 * @author Mirella
 * @version 1.0
 */
class MenuAdapter extends BaseAdapter {

    /**
     * Context aktywności.
     */
    private final Context mContext;

    /**
     * Obiekt typu Menu.
     */
    private final Menu[] menu;

    /**
     * Konstruktor klasy. Inicjalizuje nowy adapter dla menu.
     *
     * @param context Context aplikacji.
     * @param menu    Obiekt typu Menu[].
     */
    public MenuAdapter(Context context, Menu[] menu) {
        this.mContext = context;
        this.menu = menu;
    }

    /**
     * Zwraca ilość obiektów w zestawie danych reprezentowanych przez ten Adapter.
     *
     * @return Ilość obiektów.
     */
    @Override
    public int getCount() {
        return menu.length;
    }

    /**
     * Zwraca id obiektu z menu o konkretnej pozycji w zestawie danych.
     *
     * @param position  Pozycja obiektu w menu.
     * @return          Id obiektu o wybranej pozycji.
     */
    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**
     * Zwraca obiekt z menu o konkretnej pozycji w zestawie danych.
     *
     * @param position  Pozycja obiektu w menu.
     * @return          Obiekt z menu o wybranej pozycji.
     */
    @Override
    public Object getItem(int position) {
        return null;
    }

    /**
     * Ustawia nowy widok z danymi wyświetlanymi na konkretnych pozycjach.
     *
     * @param position      Pozycja obiektów w menu.
     * @param convertView   Starszy widok, używany w razie potrzeby.
     * @param parent        Klasa nadzrzędna do którego widok zostanie dołączony.
     * @return              Nowy widok z danymi wyświetlanymi na konkretnych pozycjach.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Menu menuItem = menu[position];
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.menu, null);
        }
        final ImageView imageView = convertView.findViewById(R.id.image);
        final TextView textView = convertView.findViewById(R.id.text);
        imageView.setImageResource(menuItem.getImageResource());
        textView.setText(mContext.getString(menuItem.getText()));
        return convertView;
    }
}
