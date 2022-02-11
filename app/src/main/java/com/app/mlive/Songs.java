package com.app.mlive;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;



import android.widget.Button;
import android.widget.Toast;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;


public class Songs extends AppCompatActivity {


    Button playMusic;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs);
        playMusic = findViewById(R.id.playMusic);


        playMusic.setOnClickListener(v -> {
            Intent intent = new Intent(Songs.this, MainActivity.class);
            startActivity(intent);

        });


    }



}