package com.rameshwar.theuntoldlines;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class ImageSliderAdapter extends PagerAdapter {
    private List<ImageItem> imageItemList;
    private LayoutInflater inflater;
    private Context context;
    Activity activity;
    private InterstitialAd mInterstitialAd;

    public ImageSliderAdapter(Context context, List<ImageItem> imageItemList) {
        this.context = context;
        this.imageItemList = imageItemList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return imageItemList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = inflater.inflate(R.layout.item_layout_view_pager, container, false);

        ImageView imageView = view.findViewById(R.id.vpImageView);
        Button vpDownload = view.findViewById(R.id.vpDownload);
        Button vpShare = view.findViewById(R.id.vpShare);
        Button vpNext = view.findViewById(R.id.vpNext);
        CardView cardView = view.findViewById(R.id.cardView);
        ImageView vpUserImage = view.findViewById(R.id.vpUserImage);
        TextView vpUserName = view.findViewById(R.id.vpUserName);

        loadAd();

        String imageUrl = imageItemList.get(position).getImageUrl();

        // Load the image into the ImageView using Glide
     //   Glide.with(context).load(imageUrl).into(imageView);
        Picasso.get().load(imageUrl).into(imageView);

        DBHelper dbHelper = new DBHelper(context.getApplicationContext());
        Cursor cursor = dbHelper.getUser();

        if (cursor.getCount() == 0) {
            //     Toast.makeText(context.getApplicationContext(), "No profile details found", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()){
                vpUserName.setText(""+cursor.getString(0));
                byte[] imageByte = cursor.getBlob(1);

                Bitmap bitmap = BitmapFactory.decodeByteArray(imageByte,0,imageByte.length);
                vpUserImage.setImageBitmap(bitmap);
            }
        }

        vpDownload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                showAd();
                Toast.makeText(context.getApplicationContext(), "Downloading...", Toast.LENGTH_SHORT).show();
                saveImageToGallery(imageUrl);
            }
        });

        vpShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAd();
                Toast.makeText(context.getApplicationContext(), "Sharing...", Toast.LENGTH_SHORT).show();
                shareImage(imageView);
            }
        });

        vpNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //   new SaveCardViewImageTask(context).execute(cardView);
                Toast.makeText(context.getApplicationContext(), "Sharing...", Toast.LENGTH_SHORT).show();
                shareImageWhatsapp(imageView);
            }
        });

        vpUserImage.setOnClickListener(view1 -> {
            Intent intent = new Intent(context.getApplicationContext(), UploadSQLiteActivity.class);
            context.startActivity(intent);
        });

        container.addView(view);
        return view;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    private class ImageDownloadTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                InputStream inputStream = url.openStream();
                return BitmapFactory.decodeStream(inputStream);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                // Handle the downloaded image (e.g., save it, display it, etc.)
                // For simplicity, I'm just logging the bitmap's byte count
                saveImageToExternalStorage(result);
            } else {
                Toast.makeText(context, "Failed to download image", Toast.LENGTH_SHORT).show();
            }
        }

        private void saveImageToExternalStorage(Bitmap bitmap) {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String randomValue = String.valueOf(new Random().nextInt(1000)); // Add a random value to avoid name conflicts
            String filename = "untold_" + timeStamp + "_" + randomValue + ".png";

            File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

            File imageFile = new File(storageDir, filename);

            try {
                OutputStream fOut = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                fOut.close();

                // Notify the gallery to show the new image
                context.sendBroadcast(new android.content.Intent(
                        android.content.Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                        android.net.Uri.fromFile(imageFile))
                );

                Toast.makeText(context, "Image saved successfully", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(context, "Error saving image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void shareImage(ImageView imageView) {
        imageView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(imageView.getDrawingCache());
        imageView.setDrawingCacheEnabled(false);

        // Save the bitmap to a temporary file using FileProvider
        File tempFile = saveBitmapToTempFile(bitmap);

        // Get the FileProvider content URI
        Uri contentUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", tempFile);

        // Create an Intent with ACTION_SEND
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);

        // Grant read permissions to the receiving app
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // Launch the Intent Chooser
        context.startActivity(Intent.createChooser(shareIntent, "Share Image"));
    }

    private void shareImageWhatsapp(ImageView imageView) {
        imageView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(imageView.getDrawingCache());
        imageView.setDrawingCacheEnabled(false);

        // Save the bitmap to a temporary file using FileProvider
        File tempFile = saveBitmapToTempFile(bitmap);

        // Get the FileProvider content URI
        Uri contentUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", tempFile);

        // Create an Intent with ACTION_SEND
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
        shareIntent.setPackage("com.whatsapp");

        // Grant read permissions to the receiving app
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // Launch the Intent Chooser
        context.startActivity(Intent.createChooser(shareIntent, "Share Image"));
    }

    private File saveBitmapToTempFile(Bitmap bitmap) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String filename = "untold_image_" + timeStamp + ".png";

        File storageDir = new File(context.getExternalCacheDir(), "temp_images");
        storageDir.mkdirs();

        File imageFile = new File(storageDir, filename);

        try {
            OutputStream fOut = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error preparing image for sharing", Toast.LENGTH_SHORT).show();
        }

        return imageFile;
    }

    private void saveImageToGallery(String imageUrl) {
        Glide.with(context).asBitmap().load(imageUrl).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                saveBitmapToGallery(resource);
            }
        });
    }

    private void saveBitmapToGallery(Bitmap bitmap) {
        String filename = "image_" + System.currentTimeMillis() + ".jpg";

        // Save image to external storage
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Calisthenics_Methods");
        dir.mkdirs();

        File file = new File(dir, filename);
        try {
            OutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();

            // Notify the system to scan the new image, making it available in the gallery
            MediaScannerConnection.scanFile(context,
                    new String[]{file.getAbsolutePath()},
                    new String[]{"image/jpeg"}, null);

            // Optionally, show a toast or a notification to inform the user
            Toast.makeText(context, "Image saved to gallery", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle error
            Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show();
        }
    }

    void loadAd(){

        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(context,"ca-app-pub-6972616661119579/9608957784", adRequest,
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

    void showAd(){
        if (mInterstitialAd != null) {
            mInterstitialAd.show((Activity) context);

            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();
                    mInterstitialAd = null;
                    loadAd();
                }
            });
        }
    }

}

