package com.rameshwar.theuntoldlines;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Fullshayari extends AppCompatActivity {

    EditText fullShayari;
    Button fcopy, share, saveBtn;
    LinearLayout linearLayout, layoutdataBack;
    CardView cardView;
    ImageView change, increase, decrease, layoutPerson;

    TextView blue, black, red, orange, white, layoutName;

    Vibrator vibrator;

    int[] back_images;

    private float fontSize = 12f;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullshayari);

        fullShayari = findViewById(R.id.fullShayari);
        fcopy = findViewById(R.id.fcopy);
        share = findViewById(R.id.fshare);
        saveBtn = findViewById(R.id.saveBtn);
        linearLayout = findViewById(R.id.shayari_layout);
        cardView = findViewById(R.id.icard);
        change = findViewById(R.id.change);
        increase = findViewById(R.id.increasse);
        decrease = findViewById(R.id.decrease);
        blue = findViewById(R.id.blue);
        black = findViewById(R.id.black);
        orange = findViewById(R.id.orange);
        red = findViewById(R.id.red);
        white = findViewById(R.id.white);
        layoutName = findViewById(R.id.layoutName);
        layoutPerson = findViewById(R.id.layoutPerson);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        ShowAd();

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        DBHelper dbHelper = new DBHelper(this);
        Cursor cursor = dbHelper.getUser();

        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No profile details found", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                layoutName.setText("" + cursor.getString(0));
                byte[] imageByte = cursor.getBlob(1);

                Bitmap bitmap = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
                layoutPerson.setImageBitmap(bitmap);
            }
        }

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        textColor();

        layoutPerson.setOnClickListener(v -> {
            Intent intent = new Intent(Fullshayari.this, UploadSQLiteActivity.class);
            startActivity(intent);
        });

        layoutName.setOnClickListener(v -> {
            Intent intent = new Intent(Fullshayari.this, UploadSQLiteActivity.class);
            startActivity(intent);
        });

        increase.setOnClickListener(v -> {
            fontSize += 1f;
            fullShayari.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
            vibrator.vibrate(70);
        });

        decrease.setOnClickListener(v -> {
            fontSize -= 1f;
            fullShayari.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
            vibrator.vibrate(70);
        });


        back_images = new int[]{R.drawable.img1, R.drawable.img2, R.drawable.img3,
                R.drawable.img, R.drawable.img6, R.drawable.imagefour, R.drawable.imggreen,
                R.drawable.imgneavy, R.drawable.imgpaper, R.drawable.imgpink, R.drawable.imgvoilet,
                R.drawable.img11, R.drawable.img12, R.drawable.img13, R.drawable.img14, R.drawable.img15, R.drawable.img16};


        change.setOnClickListener(v -> {

            int array_lenght = back_images.length;

            Random random = new Random();

            int random_number = random.nextInt(array_lenght);

            cardView.setBackground(ContextCompat.getDrawable(getApplicationContext(), back_images[random_number]));
            //   layoutdataBack.setBackground(ContextCompat.getDrawable(getApplicationContext(), back_images[random_number]));
            vibrator.vibrate(70);
        });

//        change.setOnClickListener(view -> {
//            Random random1 = new Random();
//            int color1 = Color.argb(255, random1.nextInt(256), random1.nextInt(256), random1.nextInt(256));
//            //     int color1 = getRandomColor();
//            cardView.setCardBackgroundColor(color1);
//        });

        String ShayariD = getIntent().getStringExtra("shayariText");
        fullShayari.setText(ShayariD);


        fcopy.setOnClickListener(view -> {

            fcopybtn();

            vibrator.vibrate(100);

        });


        share.setOnClickListener(view -> {

            shareImg();

            vibrator.vibrate(100);
        });

        saveBtn.setOnClickListener(view -> {

          ShowAd();

            saveImage();

            vibrator.vibrate(100);
        });

        getSupportActionBar().setTitle("Edit Status");
    }

    private void textColor() {
        red.setOnClickListener(v -> {
            fullShayari.setTextColor(getResources().getColor(R.color.red));
            layoutName.setTextColor(getResources().getColor(R.color.red));

            vibrator.vibrate(70);
        });

        blue.setOnClickListener(v -> {
            fullShayari.setTextColor(getResources().getColor(R.color.color1));
            layoutName.setTextColor(getResources().getColor(R.color.color1));

            vibrator.vibrate(70);
        });

        black.setOnClickListener(v -> {
            ShowAd();
            fullShayari.setTextColor(getResources().getColor(R.color.black));
            layoutName.setTextColor(getResources().getColor(R.color.black));

            vibrator.vibrate(70);
        });

        orange.setOnClickListener(v -> {
            fullShayari.setTextColor(getResources().getColor(R.color.color3));
            layoutName.setTextColor(getResources().getColor(R.color.color3));

            vibrator.vibrate(70);
        });

        white.setOnClickListener(v -> {
            fullShayari.setTextColor(getResources().getColor(R.color.white));
            layoutName.setTextColor(getResources().getColor(R.color.white));

            vibrator.vibrate(70);
        });

    }

    private void fcopybtn() {

        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        clipboard.setText(fullShayari.getText());

        Toast.makeText(Fullshayari.this, "Copied", Toast.LENGTH_SHORT).show();
    }

    private void shareImg() {

        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, fullShayari.getText());
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "The title");
            startActivity(Intent.createChooser(shareIntent, "Share..."));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void saveImage() {

        linearLayout.setDrawingCacheEnabled(true);
        linearLayout.buildDrawingCache();
        linearLayout.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        Bitmap bitmap = linearLayout.getDrawingCache();

        save(bitmap);
    }

    @SuppressLint("NewApi")
    private void save(Bitmap bitmap) {

        String root = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "quotes_image", null);
        File file = new File(root + "Download");
        String filename = "quotes_image.jpg";
        File myfile = new File(file, filename);

        if (myfile.exists()) {
            myfile.delete();
        }

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(myfile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            Toast.makeText(this, "Saved as a image...!", Toast.LENGTH_SHORT).show();
            linearLayout.setDrawingCacheEnabled(false);

        } catch (Exception e) {
            Toast.makeText(this, "Saved as a image...!", Toast.LENGTH_SHORT).show();
        }
    }

    private int getRandomColour() {
        List<Integer> colorcode = new ArrayList<>();
        colorcode.add(R.color.color1);
        colorcode.add(R.color.color2);
        colorcode.add(R.color.color3);
        colorcode.add(R.color.color4);
        colorcode.add(R.color.color5);
        colorcode.add(R.color.color6);
        colorcode.add(R.color.color7);

        Random random = new Random();
        int number = random.nextInt(colorcode.size());

        return colorcode.get(number);

    }

    private void ShowAd(){
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this, "ca-app-pub-6972616661119579/9608957784", adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
            }

            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                super.onAdLoaded(interstitialAd);
                interstitialAd.show(Fullshayari.this);
            }
        });
    }
}