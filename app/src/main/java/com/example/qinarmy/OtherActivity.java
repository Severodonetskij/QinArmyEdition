package com.example.qinarmy;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.qinarmy.util.SystemUiHelper;
import java.util.ArrayList;
import java.util.List;

public class OtherActivity extends AppCompatActivity {

    static class OtherItem {
        String title;
        String action;
        OtherItem(String title, String action) { this.title = title; this.action = action; }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other);

        List<OtherItem> items = new ArrayList<>();
        items.add(new OtherItem("О телефоне", "ABOUT"));
        items.add(new OtherItem("Расширенная файловая система", "FILES"));
        items.add(new OtherItem("Календарь", "CALENDAR"));
        items.add(new OtherItem("Управление системами", "ALL_APPS"));

        ListView list = findViewById(R.id.other_list);
        list.setAdapter(new ArrayAdapter<OtherItem>(this, android.R.layout.simple_list_item_1, items) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(OtherActivity.this).inflate(android.R.layout.simple_list_item_1, parent, false);
                }
                TextView tv = convertView.findViewById(android.R.id.text1);
                tv.setText(items.get(position).title);
                tv.setTextColor(0xFFFFFFFF);
                tv.setTextSize(18f);
                tv.setPadding(32, 32, 32, 32);
                return convertView;
            }
        });

        list.setOnItemClickListener((p, v, pos, id) -> {
            String action = items.get(pos).action;
            switch (action) {
                case "ABOUT":
                    startActivity(new Intent(android.provider.Settings.ACTION_DEVICE_INFO_SETTINGS));
                    break;
                case "FILES":
                    Intent files = new Intent(Intent.ACTION_GET_CONTENT);
                    files.setType("*/*");
                    startActivity(files);
                    break;
                case "CALENDAR":
                    Intent cal = new Intent(Intent.ACTION_VIEW);
                    cal.setData(Uri.parse("content://com.android.calendar/time"));
                    try { startActivity(cal); } catch (Exception e) { startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS)); }
                    break;
                case "ALL_APPS":
                    startActivity(new Intent(this, AppsActivity.class));
                    break;
            }
        });
        list.post(list::requestFocus);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SystemUiHelper.hideSystemBars(getWindow());
    }
}