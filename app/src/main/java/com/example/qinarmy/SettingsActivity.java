package com.example.qinarmy;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.qinarmy.util.SystemUiHelper;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Button btn = findViewById(R.id.btn_exit_launcher);
        btn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            PackageManager pm = getPackageManager();
            List<ResolveInfo> activities = pm.queryIntentActivities(intent, 0);

            for (ResolveInfo ri : activities) {
                if (!ri.activityInfo.packageName.equals(getPackageName())) {
                    Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                    homeIntent.addCategory(Intent.CATEGORY_HOME);
                    homeIntent.setClassName(ri.activityInfo.packageName, ri.activityInfo.name);
                    try {
                        startActivity(homeIntent);
                        Toast.makeText(this, "Выход в обычный режим", Toast.LENGTH_SHORT).show();
                        return;
                    } catch (Exception e) {}
                }
            }
            Toast.makeText(this, "Стандартный лаунчер не найден", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        SystemUiHelper.hideSystemBars(getWindow());
    }
}