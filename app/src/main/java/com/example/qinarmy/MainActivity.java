package com.example.qinarmy;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.qinarmy.adapter.MenuAdapter;
import com.example.qinarmy.model.MenuItem;
import com.example.qinarmy.util.SystemUiHelper;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private final List<MenuItem> menuItems = new ArrayList<>();
    private Handler handler = new Handler();
    private Runnable statusRunnable;
    private Runnable idleRunnable;
    private Runnable focusKeeper;
    private String currentStyle = "list";
    private AbsListView mainListView;
    private int lastFocusedPosition = 0;
    
    private static final long IDLE_TIMEOUT = 60 * 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        SharedPreferences prefs = getSharedPreferences("launcher_prefs", MODE_PRIVATE);
        currentStyle = prefs.getString("style", "list");

        if (currentStyle.equals("grid")) {
            setContentView(R.layout.activity_main_grid);
        } else {
            setContentView(R.layout.activity_main);
        }

        buildMenu();
        
        if (currentStyle.equals("grid")) {
            GridView grid = findViewById(R.id.menu_grid);
            mainListView = grid;
            grid.setAdapter(new GridAdapter());
            grid.setOnItemClickListener((p, v, pos, id) -> onMenuClick(menuItems.get(pos)));
            grid.requestFocus();
        } else {
            ListView list = findViewById(R.id.menu_list);
            mainListView = list;
            list.setAdapter(new MenuAdapter(this, menuItems));
            list.setOnItemClickListener((p, v, pos, id) -> onMenuClick(menuItems.get(pos)));
            list.requestFocus();
        }

        // Запоминаем позицию при навигации
        mainListView.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                lastFocusedPosition = position;
            }
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        // Возвращаем фокус при касании
        getWindow().getDecorView().setOnTouchListener((v, event) -> {
            if (mainListView != null) {
                mainListView.requestFocus();
                mainListView.setSelection(lastFocusedPosition);
            }
            return true; 
        });

        setupStatusBar();

        idleRunnable = new Runnable() {
            @Override public void run() { startActivity(new Intent(MainActivity.this, LockScreenActivity.class)); }
        };
        
        focusKeeper = new Runnable() {
            @Override public void run() {
                if (mainListView != null && !mainListView.hasFocus()) {
                    mainListView.requestFocus();
                    mainListView.setSelection(lastFocusedPosition);
                }
                handler.postDelayed(this, 1000);
            }
        };
    }

    private void resetIdleTimer() {
        handler.removeCallbacks(idleRunnable);
        handler.postDelayed(idleRunnable, IDLE_TIMEOUT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SystemUiHelper.hideSystemBars(getWindow());
        
        SharedPreferences prefs = getSharedPreferences("launcher_prefs", MODE_PRIVATE);
        String savedStyle = prefs.getString("style", "list");
        if (!savedStyle.equals(currentStyle)) { recreate(); }

        statusRunnable = new Runnable() {
            @Override public void run() { updateStatusBar(); handler.postDelayed(this, 1000); }
        };
        handler.post(statusRunnable);
        handler.post(focusKeeper);
        resetIdleTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(statusRunnable);
        handler.removeCallbacks(idleRunnable);
        handler.removeCallbacks(focusKeeper);
    }

    private void setupStatusBar() { updateStatusBar(); }

    private void updateStatusBar() {
        TextView txtTime = findViewById(R.id.txt_time);
        if (txtTime != null) txtTime.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));
        TextView txtBattery = findViewById(R.id.txt_battery);
        if (txtBattery != null) {
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = registerReceiver(null, ifilter);
            if (batteryStatus != null) {
                int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                txtBattery.setText((int) ((level / (float) scale) * 100) + "%");
            }
        }
        TextView txtOperator = findViewById(R.id.txt_operator);
        if (txtOperator != null) {
            TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            if (tm != null) {
                String op = tm.getNetworkOperatorName();
                txtOperator.setText(op != null && !op.isEmpty() ? op : "Нет сети");
            }
        }
    }

    private void buildMenu() {
        menuItems.add(new MenuItem("Контакты", R.drawable.ic_menu_kontakty_120, MenuItem.Action.CONTACTS));
        menuItems.add(new MenuItem("Звонки", R.drawable.ic_menu_zvonki_120, MenuItem.Action.CALLS));
        menuItems.add(new MenuItem("SMS", R.drawable.ic_menu_sms_120, MenuItem.Action.SMS));
        menuItems.add(new MenuItem("FM-Радио", R.drawable.ic_menu_radio_120, MenuItem.Action.FM_RADIO));
        menuItems.add(new MenuItem("Часы", R.drawable.ic_menu_chasy_120, MenuItem.Action.CLOCK));
        menuItems.add(new MenuItem("Калькулятор", R.drawable.ic_menu_calculator_120, MenuItem.Action.CALCULATOR));
        menuItems.add(new MenuItem("Настройки", R.drawable.ic_menu_nastrojki_120, MenuItem.Action.SETTINGS));
        menuItems.add(new MenuItem("Заметки", R.drawable.ic_menu_zametki_120, MenuItem.Action.NOTES));
        menuItems.add(new MenuItem("Иное", R.drawable.ic_menu_inoe_120, MenuItem.Action.OTHER));
    }

    private void onMenuClick(MenuItem item) {
        resetIdleTimer();
        Intent i = null;
        switch (item.action) {
            case CONTACTS: i = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse("content://contacts/people")); break;
            case CALLS: i = new Intent(Intent.ACTION_DIAL); break;
            case SMS: i = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse("sms:")); break;
            case FM_RADIO: startActivity(new Intent(this, RadioActivity.class)); return;
            case CLOCK: i = new Intent(android.provider.AlarmClock.ACTION_SHOW_ALARMS); break;
            case CALCULATOR: i = findAppByKeywords("calc", "calculator"); break;
            case SETTINGS: startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS)); return;
            case NOTES: startActivity(new Intent(this, NotesActivity.class)); return;
            case OTHER: startActivity(new Intent(this, AppsActivity.class)); return;
        }
        if (i != null) { try { startActivity(i); } catch (Exception e) { Toast.makeText(this, "Не найдено", Toast.LENGTH_SHORT).show(); } }
        else Toast.makeText(this, "Приложение не найдено", Toast.LENGTH_SHORT).show();
    }

    private Intent findAppByKeywords(String... keywords) {
        android.content.pm.PackageManager pm = getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<android.content.pm.ResolveInfo> apps = pm.queryIntentActivities(mainIntent, 0);
        for (android.content.pm.ResolveInfo ri : apps) {
            String pkgName = ri.activityInfo.packageName.toLowerCase();
            String appName = ri.loadLabel(pm).toString().toLowerCase();
            for (String keyword : keywords) {
                if (pkgName.contains(keyword) || appName.contains(keyword)) {
                    Intent i = new Intent(Intent.ACTION_MAIN);
                    i.setClassName(ri.activityInfo.packageName, ri.activityInfo.name);
                    return i;
                }
            }
        }
        return null;
    }

    private class GridAdapter extends BaseAdapter {
        @Override public int getCount() { return menuItems.size(); }
        @Override public Object getItem(int p) { return menuItems.get(p); }
        @Override public long getItemId(int p) { return p; }
        @Override public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.grid_item, parent, false);
            MenuItem item = menuItems.get(position);
            ImageView icon = convertView.findViewById(R.id.icon);
            TextView name = convertView.findViewById(R.id.name);
            icon.setImageResource(item.iconResId);
            name.setText(item.title);
            return convertView;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        resetIdleTimer();
        if (keyCode == KeyEvent.KEYCODE_BACK) return true;
        return super.onKeyDown(keyCode, event);
    }
}