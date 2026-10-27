package com.example.qinarmy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.qinarmy.R;
import com.example.qinarmy.model.AppInfo;
import java.util.List;

public class AppListAdapter extends ArrayAdapter<AppInfo> {
    public AppListAdapter(Context ctx, List<AppInfo> items) {
        super(ctx, 0, items);
    }

    @Override
    public View getView(int pos, View convert, ViewGroup parent) {
        if (convert == null) {
            convert = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_app, parent, false);
        }
        AppInfo app = getItem(pos);
        ((TextView) convert.findViewById(R.id.app_title)).setText(app.label);
        ((ImageView) convert.findViewById(R.id.app_icon)).setImageDrawable(app.icon);
        return convert;
    }
}
