package com.example.qinarmy;

import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LockScreenActivity extends AppCompatActivity {
    private TextView timeView, dateView;
    private Handler handler = new Handler();
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);
        
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        timeView = findViewById(R.id.lock_time);
        dateView = findViewById(R.id.lock_date);

        runnable = new Runnable() {
            @Override
            public void run() {
                Date now = new Date();
                timeView.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(now));
                dateView.setText(new SimpleDateFormat("EEEE, d MMMM", new Locale("ru")).format(now));
                handler.postDelayed(this, 1000);
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.post(runnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_STAR) {
                getSharedPreferences("launcher_prefs", MODE_PRIVATE).edit().putBoolean("need_lock", false).apply();
                finish();
                return true;
            }
            return true; 
        }
        return super.dispatchKeyEvent(event);
    }
}