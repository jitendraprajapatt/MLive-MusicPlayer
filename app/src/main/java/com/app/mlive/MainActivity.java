package com.app.mlive;


import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;


import android.app.Activity;



import android.content.Intent;





import android.os.Bundle;


import android.util.Log;

import android.widget.ArrayAdapter;


import android.widget.ListView;


import android.widget.Toast;



import com.google.android.gms.ads.AdRequest;

import com.google.android.gms.ads.LoadAdError;




import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;



public class MainActivity extends AppCompatActivity{

    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    ListView listView ;

    ArrayList<String> songName = new ArrayList<>();
    ArrayList<String> songUrl = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;
    private final String TAG = "MainActivity";
    private RewardedAd mRewardedAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listView);
        retrieveSongs();
        rewardedAds();
    }

    private void rewardedAds() {
        AdRequest adRequest = new AdRequest.Builder().build();

        RewardedAd.load(this, "ca-app-pub-3016893009439188/7750721997",
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        Log.d(TAG, loadAdError.getMessage());
                        mRewardedAd = null;

                    }


                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        mRewardedAd = rewardedAd;
                        Log.d(TAG, "onAdFailedToLoad");
                        if (mRewardedAd != null) {
                            Activity activityContext = MainActivity.this;
                            mRewardedAd.show(activityContext, rewardItem -> {
                                // Handle the reward.
                                Log.d("TAG", "The user earned the reward.");


                            });
                        } else {
                            Log.d("TAG", "The rewarded ad wasn't ready yet.");
                        }

                    }
                });
    }

    private void retrieveSongs() {
        reference = FirebaseDatabase.getInstance().getReference("Songs");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot snapshot1 : snapshot.getChildren()) {

                    Song songObj = snapshot1.getValue(Song.class);
                    assert songObj != null;
                    songName.add(songObj.getSongName());
                    songUrl.add(songObj.getSongUrl());

                    listView.setOnItemClickListener((parent, view, position, id) -> {

                        Intent i = new Intent(MainActivity.this,playing.class);
                        i.putExtra("songName" , songName.get(position));
                        i.putExtra("songUrl" , songUrl.get(position));

                       startActivity(i);



                    });

                }

               arrayAdapter= new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_activated_1, songName);
               listView.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {
                Toast.makeText(getApplicationContext(),"data load error",Toast.LENGTH_LONG).show();
            }
        });

    }


}