package com.example.leafdetection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends AppCompatActivity {
    Button btnDisease, btnCropPrediction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnDisease = (Button) findViewById(R.id.btnDisease);
        btnCropPrediction = (Button) findViewById(R.id.btnCropPred);

        btnDisease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, MainActivity.class));
            }
        });

        btnCropPrediction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //crop prediction
                startActivity(new Intent(HomeActivity.this, CropActivity.class));
            }
        });


    }
}
