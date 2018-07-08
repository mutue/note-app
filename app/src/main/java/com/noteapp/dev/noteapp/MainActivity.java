package com.noteapp.dev.noteapp;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private EditText txtInput;
    private Button btnAdd;
    private ListView listView;
    private List<String> list;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);

        loadData();
        listView = findViewById(R.id.listView);
        btnAdd = findViewById(R.id.btnAdd);
        txtInput = findViewById(R.id.txtInput);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkDup()) {
                    Toast.makeText(MainActivity.this, getResources().
                            getString(R.string.double_entry), Toast.LENGTH_SHORT).show();
                    return;
                } else if (isTextEmpty()) {
                    Toast.makeText(MainActivity.this, getResources().
                            getString(R.string.empty_entry), Toast.LENGTH_SHORT).show();
                    return;
                }

                list.add(txtInput.getText().toString());
                adapter.notifyDataSetChanged();
                txtInput.setText("");
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, View view, final int index, long l) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle(getResources().getString(R.string.delete));
                dialog.setMessage(getResources().getString(R.string.ask_for_clarity));
                final int posRm = index;
                dialog.setNegativeButton(getResources().getString(R.string.cancel), null);
                dialog.setPositiveButton(getResources().getString(R.string.ok), new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        list.remove(posRm);
                        adapter.notifyDataSetChanged();
                    }
                });
                dialog.show();
            }
        });

        txtInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                switch (i) {
                    case KeyEvent.KEYCODE_ENTER:
                        if (checkDup()) {
                            Toast.makeText(MainActivity.this, getResources().
                                    getString(R.string.double_entry), Toast.LENGTH_SHORT).show();
                            txtInput.setText("");
                            break;
                        } else if (isTextEmpty()) {
                            Toast.makeText(MainActivity.this, getResources().
                                    getString(R.string.empty_entry), Toast.LENGTH_SHORT).show();
                            break;
                        } else {
                            list.add(txtInput.getText().toString());
                            adapter.notifyDataSetChanged();
                            txtInput.setText("");
                            break;
                        }
                }
                return true;
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        saveData();
    }

    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString("save list", json);
        editor.apply();
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.saved), Toast.LENGTH_LONG).show();
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("save list", null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        list = gson.fromJson(json, type);

        if (list == null)
            list = new ArrayList<>();
    }

    public boolean checkDup() {
        for (String m : list) {
            if (m.equals(txtInput.getText().toString())) {
                return true;
            }
        }
        return false;
    }

    public boolean isTextEmpty() {
        return txtInput.getText().toString().equals("");
    }
}
