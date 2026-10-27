package com.example.qinarmy;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.qinarmy.util.SystemUiHelper;
import java.util.Random;

public class RadioActivity extends AppCompatActivity {
    private TextView freqDisplay;
    private TextView headphoneWarning;
    private EqView eqView;
    private ScaleView scaleView;
    private float currentFreq = 98.3f;
    private AudioManager audioManager;
    private Handler handler = new Handler();
    private Random random = new Random();
    private boolean isPlaying = true;

    private Runnable waveRunnable = new Runnable() {
        @Override
        public void run() {
            if (isPlaying && eqView != null) eqView.invalidate();
            handler.postDelayed(this, 100);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setBackgroundColor(Color.parseColor("#151922"));
        mainLayout.setPadding(16, 16, 16, 16);
        mainLayout.setGravity(Gravity.CENTER);

        eqView = new EqView(this);
        mainLayout.addView(eqView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100));

        freqDisplay = new TextView(this);
        freqDisplay.setText(String.format(java.util.Locale.US, "%.1f", currentFreq));
        freqDisplay.setTextColor(Color.WHITE);
        freqDisplay.setTextSize(72f);
        freqDisplay.setTypeface(Typeface.DEFAULT_BOLD);
        freqDisplay.setGravity(Gravity.CENTER);
        freqDisplay.setShadowLayer(10f, 0f, 0f, Color.parseColor("#FF3366"));
        mainLayout.addView(freqDisplay);

        scaleView = new ScaleView(this);
        LinearLayout.LayoutParams scaleParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 80);
        scaleParams.topMargin = 40;
        mainLayout.addView(scaleView, scaleParams);

        headphoneWarning = new TextView(this);
        headphoneWarning.setTextSize(16f);
        headphoneWarning.setGravity(Gravity.CENTER);
        headphoneWarning.setPadding(0, 20, 0, 0);
        mainLayout.addView(headphoneWarning);

        setContentView(mainLayout);
        handler.post(waveRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SystemUiHelper.hideSystemBars(getWindow());
        if (audioManager != null && (audioManager.isWiredHeadsetOn() || audioManager.isBluetoothA2dpOn())) {
            headphoneWarning.setText("");
            isPlaying = true;
        } else {
            headphoneWarning.setText("Вставьте наушники");
            headphoneWarning.setTextColor(Color.RED);
            isPlaying = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(waveRunnable);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            currentFreq += 0.5f;
            if (currentFreq > 108.0f) currentFreq = 87.5f;
            freqDisplay.setText(String.format(java.util.Locale.US, "%.1f", currentFreq));
            scaleView.invalidate();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            currentFreq -= 0.5f;
            if (currentFreq < 87.5f) currentFreq = 108.0f;
            freqDisplay.setText(String.format(java.util.Locale.US, "%.1f", currentFreq));
            scaleView.invalidate();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    class EqView extends View {
        private Paint paint = new Paint();
        private int[] heights = new int[20];
        public EqView(Context context) {
            super(context);
            paint.setColor(Color.parseColor("#40FF3366"));
            paint.setStrokeWidth(6f);
        }
        @Override protected void onDraw(Canvas canvas) {
            int barWidth = getWidth() / heights.length;
            for (int i = 0; i < heights.length; i++) {
                heights[i] = isPlaying ? random.nextInt(80) + 20 : 20;
                canvas.drawLine(i * barWidth + barWidth/2f, getHeight()/2f - heights[i]/2f, i * barWidth + barWidth/2f, getHeight()/2f + heights[i]/2f, paint);
            }
        }
    }

    class ScaleView extends View {
        private Paint linePaint = new Paint();
        private Paint activePaint = new Paint();
        public ScaleView(Context context) {
            super(context);
            linePaint.setColor(Color.GRAY);
            linePaint.setStrokeWidth(2f);
            activePaint.setColor(Color.parseColor("#FF3366"));
            activePaint.setStrokeWidth(4f);
        }
        @Override protected void onDraw(Canvas canvas) {
            int width = getWidth(), height = getHeight(), ticks = 20;
            for (int i = 0; i <= ticks; i++) canvas.drawLine(i * ((float)width/ticks), height*0.3f, i * ((float)width/ticks), height*0.7f, linePaint);
            float activeX = ((currentFreq - 87.5f) / (108.0f - 87.5f)) * width;
            canvas.drawLine(activeX, 0, activeX, height, activePaint);
        }
    }
}