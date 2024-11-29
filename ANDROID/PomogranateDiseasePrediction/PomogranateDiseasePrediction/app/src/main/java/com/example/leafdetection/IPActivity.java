package com.example.leafdetection;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Locale;

public class IPActivity extends AppCompatActivity {
    EditText etServerIP;
    Button btnConnect, btnChangeLanguage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ip);
        loadLocale();
        btnConnect = (Button) findViewById(R.id.btnConnect);
        etServerIP = (EditText) findViewById(R.id.etServerIP);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etServerIP.getText().toString().equals("")) {
                    Toast.makeText(IPActivity.this, "Please enter server ip", Toast.LENGTH_SHORT).show();
                } else {
                    MainActivity.SERVER_URL = "http://" + etServerIP.getText().toString() + ":5000/detectDisease";
                    startActivity(new Intent(IPActivity.this, HomeActivity.class));
                    finish();
                }
            }
        });

        btnChangeLanguage = (Button) findViewById(R.id.btnChangeLang);
        btnChangeLanguage.setVisibility(View.GONE);

        btnChangeLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChangeLanguageDialog();
            }
        });
    }

    private void showChangeLanguageDialog() {
        final String[] listItems = {"English", "मराठी"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(IPActivity.this);
        mBuilder.setTitle("Change Language...");
        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    setLocale("en");
                    recreate();
                } else if (i == 1) {
                    setLocale("mr");
                    recreate();
                }
                dialogInterface.dismiss();
            }
        });
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();

    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());

        //save data to shared preferences
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My_Lang", lang);
        editor.apply();
    }

    //load language saved in shred prefer
    public void loadLocale() {
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String lang = prefs.getString("My_Lang", "en");
        setLocale(lang);
    }

}
