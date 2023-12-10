package com.rameshwar.theuntoldlines;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class Full_Image_Activity extends AppCompatActivity {

    private String url;
    private ImageView fullImage;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        loadAd();

        ImageView fullImage = findViewById(R.id.fullImage);
        Button downloadImg = findViewById(R.id.download);
        Button shareimg = findViewById(R.id.share);

        url = getIntent().getStringExtra("image");

        Glide.with(this).load(url).into(fullImage);

        shareimg.setOnClickListener(v -> {

            if (mInterstitialAd != null) {
                mInterstitialAd.show(Full_Image_Activity.this);
            }

            shareimg.setText("Share Again!");
            shareimg.setEnabled(true);

            BitmapDrawable drawable = (BitmapDrawable)fullImage.getDrawable();
            Bitmap bitmap = drawable.getBitmap();

            String bitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(),bitmap,"title",null);
            Uri uri = Uri.parse(bitmapPath);

            try {

                Intent imgIntent = new Intent(Intent.ACTION_SEND);
                imgIntent.setType("image/png");
                imgIntent.putExtra(Intent.EXTRA_STREAM,uri);
                imgIntent.putExtra(Intent.EXTRA_TEXT,"Download the App and get Daily Motivational Quotes to motivate you!");
                imgIntent = Intent.createChooser(imgIntent,"Share by");
                startActivity(imgIntent);

                Toast.makeText(this, "Sharing", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        downloadImg.setOnClickListener(v -> {

            if (mInterstitialAd != null) {
                mInterstitialAd.show(Full_Image_Activity.this);
            }

            downloadImg.setText("Save Again!");
            downloadImg.setEnabled(true);

            Toast.makeText(this, "Saved to Gallery", Toast.LENGTH_SHORT).show();


            checkPermission();
        });


        getSupportActionBar().setTitle("Download");
    }

    private void checkPermission() {

        Dexter.withContext(this)
                .withPermissions(
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            downloadImg();
                        } else {
                            Toast.makeText(Full_Image_Activity.this, "Please allow all permissions", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

                    }
                }).check();

    }

    private void downloadImg() {
        ImageView fullImage = findViewById(R.id.fullImage);
        BitmapDrawable drawable = (BitmapDrawable)fullImage.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        String bitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(),bitmap,"title",null);
        Uri uri = Uri.parse(bitmapPath);

        try {

            Intent imgIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            imgIntent.setType("image/png");
            imgIntent.putExtra(Intent.EXTRA_STREAM,uri);
            //   imgIntent.putExtra(Intent.EXTRA_TEXT,"Download the App and get access of your all needs to share on any social media platforms \n\n Download From Google Play" +
            //        "  \n\n https://play.google.com/store/apps/details?id=com.rameshwar.marathi.goodmorningandgoodnightshayri")
            //    imgIntent = Intent.createChooser(imgIntent,"Share by");
            startActivity(imgIntent);

            //    Toast.makeText(this, "Sharing", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }

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
}