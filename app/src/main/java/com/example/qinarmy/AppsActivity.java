package com.example.qinarmy;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.qinarmy.util.SystemUiHelper;

public class AppsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundResource(R.drawable.bg_1);
        layout.setPadding(16, 16, 16, 16);

        TextView title = new TextView(this);
        title.setText("Иное");
        title.setTextColor(Color.WHITE);
        title.setTextSize(20f);
        title.setGravity(Gravity.CENTER);
        layout.addView(title);

        // ПОРЯДОК: Сначала О телефоне, затем Настройки интерфейса
        addStyledButton(layout, "О телефоне", R.drawable.ic_menu_o_telefone, () -> { 
            try { startActivity(new Intent(android.provider.Settings.ACTION_DEVICE_INFO_SETTINGS)); } catch (Exception e) {} 
        });
        addStyledButton(layout, "Настройки интерфейса", R.drawable.ic_menu_smenit_interfejs, () -> 
            startActivity(new Intent(this, SetupActivity.class))
        );
        addStyledButton(layout, "Файловый менеджер", R.drawable.ic_menu_fajlovyj_menedzher, () -> { 
            Intent i = new Intent(Intent.ACTION_GET_CONTENT); i.setType("*/*"); 
            try { startActivity(i); } catch (Exception e) {} 
        });
        addStyledButton(layout, "Календарь", R.drawable.ic_menu_kalendar, () -> { 
            Intent i = new Intent(Intent.ACTION_MAIN); i.addCategory(Intent.CATEGORY_APP_CALENDAR); 
            try { startActivity(i); } catch (Exception e) {} 
        });
        addStyledButton(layout, "Управление системами", R.drawable.ic_menu_upavlenie_sistemami, () -> 
            startActivity(new Intent(this, AllAppsActivity.class))
        );

        setContentView(layout);
    }

    private void addStyledButton(LinearLayout parent, String text, int iconRes, Runnable action) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(16, 8, 16, 8);
        row.setFocusable(true);
        row.setBackgroundColor(Color.parseColor("#33FFFFFF"));

        ImageView icon = new ImageView(this);
        icon.setImageResource(iconRes);
        icon.setLayoutParams(new LinearLayout.LayoutParams(48, 48));
        row.addView(icon);

        TextView txt = new TextView(this);
        txt.setText(text);
        txt.setTextColor(Color.WHITE);
        txt.setTextSize(18f);
        txt.setSingleLine(true);
        txt.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        txt.setMarqueeRepeatLimit(-1);
        txt.setSelected(true);
        txt.setPadding(16, 0, 0, 0);
        row.addView(txt);

        row.setOnClickListener(v -> action.run());
        row.setOnFocusChangeListener((v, hasFocus) -> v.setBackgroundColor(hasFocus ? Color.parseColor("#66FFFFFF") : Color.parseColor("#33FFFFFF")));
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 72);
        params.setMargins(0, 8, 0, 8);
        parent.addView(row, params);
    }

    @Override
    protected void onResume() { super.onResume(); SystemUiHelper.hideSystemBars(getWindow()); }
}