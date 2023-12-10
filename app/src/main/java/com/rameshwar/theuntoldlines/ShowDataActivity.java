package com.rameshwar.theuntoldlines;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShowDataActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<ImageModel> list;
    private ImageAdapter imageAdapter;
    private DatabaseReference reference;
    private Dialog loadingDialogue;

    ShimmerFrameLayout shimmerFrameLayout;
    LinearLayout shimmerLinearL;

    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_data);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        loadAd();
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        loadingDialogue = new Dialog(this);
        loadingDialogue.setContentView(R.layout.loading);
        loadingDialogue.getWindow().setBackgroundDrawable(getDrawable(R.drawable.back_round));
        loadingDialogue.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialogue.setCancelable(false);
      //  loadingDialogue.show();

        String name = getIntent().getStringExtra("name");

        recyclerView = findViewById(R.id.recyclerView);
        shimmerFrameLayout = findViewById(R.id.shimmer_view_container);
        shimmerLinearL = findViewById(R.id.shimmerLinearL);

        reference = FirebaseDatabase.getInstance().getReference().child("Untold").child("Category").child(name).child("data");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list = new ArrayList<>();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    ImageModel data = snapshot1.getValue(ImageModel.class);
                    list.add(data);
                }

                LinearLayoutManager layoutManager = new LinearLayoutManager(ShowDataActivity.this);
                recyclerView.setLayoutManager(layoutManager);
                imageAdapter = new ImageAdapter(ShowDataActivity.this, list);
                recyclerView.setAdapter(imageAdapter);
                shimmerFrameLayout.stopShimmer();
                shimmerLinearL.setVisibility(View.GONE);
               // loadingDialogue.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(ShowDataActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();

            }
        });
    }

    void loadAd(){

        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,"ca-app-pub-6972616661119579/9608957784", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        mInterstitialAd = null;
                    }
                });

    }

    @Override
    protected void onPause() {
        shimmerFrameLayout.stopShimmer();
        super.onPause();
    }

    @Override
    protected void onResume() {
        shimmerFrameLayout.stopShimmer();
        super.onResume();
    }

    @Override
    public void onBackPressed() {

        if (mInterstitialAd != null) {
            mInterstitialAd.show(ShowDataActivity.this);

            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();

                    mInterstitialAd = null;
                    ShowDataActivity.super.onBackPressed();
                }
            });
        } else {
            super.onBackPressed();
        }

    }
}