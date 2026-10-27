package com.example.qinarmy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.qinarmy.R;
import com.example.qinarmy.model.MenuItem;
import java.util.List;

public class MenuAdapter extends ArrayAdapter<MenuItem> {
    public MenuAdapter(Context ctx, List<MenuItem> items) {
        super(ctx, 0, items);
    }

    @Override
    public View getView(int pos, View convert, ViewGroup parent) {
        if (convert == null) {
            convert = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_menu, parent, false);
        }
        MenuItem item = getItem(pos);
        ((TextView) convert.findViewById(R.id.item_title)).setText(item.title);
        ((ImageView) convert.findViewById(R.id.item_icon)).setImageResource(item.iconResId);
        return convert;
    }
}
