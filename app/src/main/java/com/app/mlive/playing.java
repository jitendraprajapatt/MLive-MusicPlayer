package com.app.mlive;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;



public class playing extends AppCompatActivity {

    TextView songName, textCurrentTime, textTotalDuration;
    ImageView play, stop, forWord;
    String name,url;
    SeekBar seekBar;
    MediaPlayer mediaPlayer;
    private AdView mAdView;
    private final String TAG = "playing";
    Intent intent ;
    private InterstitialAd mInterstitialAd;
    private AudioManager audioManager;

  Handler handler = new Handler() ;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing);



        songName = findViewById(R.id.displayName);
        play = findViewById(R.id.PlayBtn);
        textCurrentTime = findViewById(R.id.textCurrentTime);
        textTotalDuration = findViewById(R.id.textTotalDuration);
        forWord = findViewById(R.id.forword);
        seekBar = findViewById(R.id.seekBar);
        mediaPlayer = new MediaPlayer();
        stop = findViewById(R.id.stopBtn);

        seekBar.setMax(100);

        intent = getIntent();
        name =intent.getStringExtra("songName");
        url = intent.getStringExtra("songUrl");
        songName.setText(name);
        loadAd();

        prepareMediaPlayer();

        stop.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()){
                mediaPlayer.stop();
            }

        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    handler.removeCallbacks(updater);
                    mediaPlayer.pause();
                    play.setImageResource(R.drawable.play);
                }else {
                    mediaPlayer.start();
                    play.setImageResource(R.drawable.pause);
                    updateSeekbar();
                }
            }
        });



        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                seekBar.setSecondaryProgress(percent);
            }
        });

        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
               SeekBar seekBar1 =(SeekBar) v;
               int playPostionsSeeks = (mediaPlayer.getDuration()/100)*seekBar1.getProgress();
               mediaPlayer.seekTo(playPostionsSeeks);
               textCurrentTime.setText(milliSecondsToTimer(mediaPlayer.getCurrentPosition()));
                return false;
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                seekBar.setProgress(0);
                play.setImageResource(R.drawable.play);
                textCurrentTime.setText("0:00");
                mediaPlayer.reset();
                prepareMediaPlayer();
            }
        });

        // google ads implement
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });


        // banner
        AdView adView = new AdView(this);

        adView.setAdSize(AdSize.BANNER);

        adView.setAdUnitId("ca-app-pub-3016893009439188/4894238763");

        //ad load banner
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);




    }

    private  void prepareMediaPlayer(){
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
            textTotalDuration.setText(milliSecondsToTimer(mediaPlayer.getDuration()));
        }catch (Exception exception){
            Toast.makeText(this,exception.getMessage(),Toast.LENGTH_LONG).show();
        }
    }
    private Runnable updater = new Runnable() {
        @Override
        public void run() {

            updateSeekbar();
            long currentDuration = mediaPlayer.getCurrentPosition();
            textCurrentTime.setText(milliSecondsToTimer(currentDuration));
        }
    };
    private  void  updateSeekbar(){
        if(mediaPlayer.isPlaying()){

            seekBar.setProgress((int)(((float)mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration())*100));
            handler.postDelayed(updater,1000);
        }
    }

    private String milliSecondsToTimer(long milliSeconds) {
        String timerString = "";
        String secondString;
        int hours = (int) (milliSeconds / (1000 * 60 * 60));
        int minutes = (int) (milliSeconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliSeconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        if (hours > 0) {
            timerString = hours + ":";
        }
        if (seconds < 0) {
            secondString = "0" + seconds;
        } else {
            secondString = "" + seconds;
        }
        timerString = timerString + minutes + ":" + secondString;
return timerString;
    }
    public void loadAd() {

        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this,"ca-app-pub-3016893009439188/7451037772", adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {

                mInterstitialAd = interstitialAd;
                if (mInterstitialAd == null) {

                    Toast.makeText(playing.this,"The interstitial ad wasn't ready yet",Toast.LENGTH_SHORT).show();
                } else {
                    mInterstitialAd.show(playing.this);
                }
                Toast.makeText(getApplicationContext(),"g-ads-I loaded",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {

                Toast.makeText(getApplicationContext(),"g-ads-I Failed",Toast.LENGTH_LONG).show();
                mInterstitialAd = null;


            }
        });


    }

        @Override
        protected void onDestroy () {
        super.onDestroy();

        if(mediaPlayer!= null){
            if (mediaPlayer.isPlaying()){
                mediaPlayer.stop();
                mediaPlayer.reset();
            }
        }

        }
}