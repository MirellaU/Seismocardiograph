package com.example.mirella.seismocardiograph;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

class MenuAdapter extends BaseAdapter {

    private final Context mContext;
    private final Menu[] menu;

    // 1
    public MenuAdapter(Context context, Menu[] menu) {
        this.mContext = context;
        this.menu = menu;
    }

    // 2
    @Override
    public int getCount() {
        return menu.length;
    }

    // 3
    @Override
    public long getItemId(int position) {
        return 0;
    }

    // 4
    @Override
    public Object getItem(int position) {
        return null;
    }

    // 5
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
