package com.example.qinarmy;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.qinarmy.util.SystemUiHelper;
import java.util.ArrayList;
import java.util.List;

public class NotesActivity extends AppCompatActivity {
    private List<String> notesList = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundColor(Color.parseColor("#F5F5DC"));

        LinearLayout header = new LinearLayout(this);
        header.setBackgroundColor(Color.parseColor("#1976D2"));
        header.setPadding(16, 16, 16, 16);
        TextView title = new TextView(this);
        title.setText("Заметки");
        title.setTextColor(Color.WHITE);
        title.setTextSize(22f);
        header.addView(title);
        layout.addView(header);

        listView = new ListView(this);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, notesList) {
            @Override public android.view.View getView(int position, android.view.View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                view.setTextColor(Color.BLACK); view.setTextSize(20f); view.setPadding(24, 24, 24, 24);
                return view;
            }
        };
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((p, v, pos, id) -> {
            new AlertDialog.Builder(this).setTitle("Заметка").setMessage(notesList.get(pos))
                .setPositiveButton("Удалить", (d, w) -> { notesList.remove(pos); adapter.notifyDataSetChanged(); saveNotes(); })
                .setNegativeButton("Закрыть", null).show();
        });
        layout.addView(listView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));

        setContentView(layout);
        loadNotes();
    }

    @Override protected void onResume() { super.onResume(); SystemUiHelper.hideSystemBars(getWindow()); }

    @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            if (listView.getSelectedItemPosition() == notesList.size() - 1 || notesList.isEmpty()) { showAddNoteDialog(); return true; }
        } else if (keyCode == KeyEvent.KEYCODE_BACK) { finish(); return true; }
        return super.onKeyDown(keyCode, event);
    }

    private void showAddNoteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Новая заметка");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("Сохранить", (dialog, which) -> {
            String text = input.getText().toString().trim();
            if (!text.isEmpty()) { notesList.add(text); adapter.notifyDataSetChanged(); saveNotes(); }
        });
        builder.setNegativeButton("Отмена", null).show();
    }

    private void saveNotes() {
        try {
            StringBuilder sb = new StringBuilder();
            for (String s : notesList) sb.append(s).append("\n");
            java.io.FileOutputStream fos = openFileOutput("notes.txt", Context.MODE_PRIVATE);
            fos.write(sb.toString().getBytes()); fos.close();
        } catch (Exception e) {}
    }
    private void loadNotes() {
        try {
            java.io.FileInputStream fis = openFileInput("notes.txt");
            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(fis));
            String line;
            while ((line = reader.readLine()) != null) if (!line.isEmpty()) notesList.add(line);
            fis.close(); adapter.notifyDataSetChanged();
        } catch (Exception e) {}
    }
}