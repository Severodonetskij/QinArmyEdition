package com.example.qinarmy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.qinarmy.adapter.AppListAdapter;
import com.example.qinarmy.model.AppInfo;
import com.example.qinarmy.util.AppLoader;
import com.example.qinarmy.util.SystemUiHelper;
import java.util.Collections;
import java.util.List;

public class AllAppsActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps);
        List<AppInfo> apps = AppLoader.loadLaunchableApps(this);
        Collections.sort(apps, (a1, a2) -> {
            String s1 = a1.label.toLowerCase(); String s2 = a2.label.toLowerCase();
            boolean isRus1 = s1.length() > 0 && Character.UnicodeBlock.of(s1.charAt(0)) == Character.UnicodeBlock.CYRILLIC;
            boolean isRus2 = s2.length() > 0 && Character.UnicodeBlock.of(s2.charAt(0)) == Character.UnicodeBlock.CYRILLIC;
            if (isRus1 && !isRus2) return -1; if (!isRus1 && isRus2) return 1;
            return s1.compareTo(s2);
        });
        ListView lv = findViewById(R.id.apps_list);
        lv.setAdapter(new AppListAdapter(this, apps));
        lv.setOnItemClickListener((p, v, pos, id) -> {
            AppInfo app = (AppInfo) p.getItemAtPosition(pos);
            Intent launch = getPackageManager().getLaunchIntentForPackage(app.packageName);
            if (launch != null) startActivity(launch);
        });
        lv.post(lv::requestFocus);
    }
    @Override protected void onResume() { super.onResume(); SystemUiHelper.hideSystemBars(getWindow()); }
}