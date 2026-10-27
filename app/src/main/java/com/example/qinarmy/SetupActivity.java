package com.example.qinarmy;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.qinarmy.util.SystemUiHelper;

public class SetupActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundColor(Color.parseColor("#121212"));
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(16, 16, 16, 16);

        TextView title = new TextView(this);
        title.setText("Выберите стиль");
        title.setTextColor(Color.WHITE);
        title.setTextSize(24f);
        title.setGravity(Gravity.CENTER);
        layout.addView(title);

        layout.addView(createMenuRow("Список", R.drawable.spisok, "list"), new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
        layout.addView(createMenuRow("Сетка", R.drawable.setka, "grid"), new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));

        setContentView(layout);
    }

    private LinearLayout createMenuRow(String text, int iconRes, String style) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.VERTICAL);
        row.setGravity(Gravity.CENTER);
        row.setBackgroundColor(Color.parseColor("#222222"));
        row.setFocusable(true);

        ImageView icon = new ImageView(this);
        icon.setImageResource(iconRes);
        icon.setLayoutParams(new LinearLayout.LayoutParams(120, 120));
        row.addView(icon);

        TextView txt = new TextView(this);
        txt.setText(text);
        txt.setTextColor(Color.WHITE);
        txt.setTextSize(20f);
        row.addView(txt);

        row.setOnClickListener(v -> {
            // СОХРАНЯЕМ СТИЛЬ
            getSharedPreferences("launcher_prefs", MODE_PRIVATE).edit().putString("style", style).apply();
            
            // ПЕРЕЗАПУСКАЕМ MAIN ACTIVITY, ЧТОБЫ ПРИМЕНИТЬ ИЗМЕНЕНИЯ
            Intent i = new Intent(this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();

            // Открываем настройки, чтобы предложить сделать главным
            startActivity(new Intent(Settings.ACTION_HOME_SETTINGS));
        });

        row.setOnFocusChangeListener((v, hasFocus) -> v.setBackgroundColor(hasFocus ? Color.parseColor("#444444") : Color.parseColor("#222222")));
        return row;
    }

    @Override
    protected void onResume() { super.onResume(); SystemUiHelper.hideSystemBars(getWindow()); }
}