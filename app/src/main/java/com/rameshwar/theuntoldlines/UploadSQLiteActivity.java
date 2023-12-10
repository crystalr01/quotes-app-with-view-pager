package com.rameshwar.theuntoldlines;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.io.IOException;

public class UploadSQLiteActivity extends AppCompatActivity {

    private ImageView uploadImage;
    private EditText uploadName;
    private Button saveButton;
    private Uri uri;
    private Bitmap bitmapImage;
    DBHelper dbHelper1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_sqlite);

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        dbHelper1 = new DBHelper(this);

        uploadImage = findViewById(R.id.uploadImage);
        uploadName = findViewById(R.id.uploadName);
        saveButton = findViewById(R.id.saveButton);

        getSupportActionBar().setTitle("Edit Profile");

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            assert data != null;
                            uri = data.getData();
                            try {
                                bitmapImage = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            } catch (IOException e) {
                                Toast.makeText(UploadSQLiteActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            uploadImage.setImageBitmap(bitmapImage);
                        } else {
                            Toast.makeText(UploadSQLiteActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        uploadImage.setOnClickListener(v -> {
            try {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                activityResultLauncher.launch(intent);
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        saveButton.setOnClickListener(v -> {
            storeImage();
        });
    }

    private void storeImage() {
        if (!uploadName.getText().toString().isEmpty() &&
                uploadImage.getDrawable() != null && bitmapImage != null){

            dbHelper1.storeData(new ModelClass(uploadName.getText().toString(),bitmapImage));

            Intent intent = new Intent(UploadSQLiteActivity.this, MainActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "All fields are mandatory", Toast.LENGTH_SHORT).show();
        }
    }
}