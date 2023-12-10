package com.rameshwar.theuntoldlines;

// ImagePagerAdapter.java

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.PagerAdapter;

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

public class ImagePagerAdapter extends PagerAdapter {

    private List<String> imageUrls;
    private Context context;
    private LayoutInflater layoutInflater;

    public ImagePagerAdapter(List<String> imageUrls, Context context) {
        this.imageUrls = imageUrls;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return imageUrls.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = layoutInflater.inflate(R.layout.item_layout_view_pager, container, false);

        ImageView imageView = view.findViewById(R.id.vpImageView);
        Button vpDownload = view.findViewById(R.id.vpDownload);
        Button vpShare = view.findViewById(R.id.vpShare);
        Button vpNext = view.findViewById(R.id.vpNext);
        CardView cardView = view.findViewById(R.id.cardView);
        ImageView vpUserImage = view.findViewById(R.id.vpUserImage);
        TextView vpUserName = view.findViewById(R.id.vpUserName);


        // Load image into ImageView using Picasso or any other image loading library
        Picasso.get().load(imageUrls.get(position)).into(imageView);


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
                Toast.makeText(context.getApplicationContext(), "Downloading...", Toast.LENGTH_SHORT).show();
                new ImageDownloadTask().execute(imageUrls.get(position));
            }
        });

        vpShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
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
//    private static class SaveCardViewImageTask extends AsyncTask<CardView, Void, String> {
//
//        private Context context;
//
//        SaveCardViewImageTask(Context context) {
//            this.context = context;
//        }
//
//        @Override
//        protected String doInBackground(CardView... cardViews) {
//            if (cardViews.length > 0) {
//                Bitmap cardBitmap = convertCardViewToBitmap(cardViews[0]);
//                return saveBitmapToFile(cardBitmap);
//            }
//            return null;
//        }
//
//        private Bitmap convertCardViewToBitmap(CardView cardView) {
//            Bitmap bitmap = Bitmap.createBitmap(cardView.getWidth(), cardView.getHeight(), Bitmap.Config.ARGB_8888);
//            cardView.draw(new Canvas(bitmap));
//            return bitmap;
//        }
//
//        private String saveBitmapToFile(Bitmap bitmap) {
//            String fileName = "card_image_" + System.currentTimeMillis() + ".png";
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                // Use MediaStore for Android 10 (API level 29) and higher
//                saveBitmapToMediaStore(bitmap, fileName);
//            } else {
//                // Use the app's external storage for Android versions below 10
//                saveBitmapToExternalStorage(bitmap, fileName);
//            }
//
//            return fileName;
//        }
//
//        private void saveBitmapToMediaStore(Bitmap bitmap, String fileName) {
//            ContentValues values = new ContentValues();
//            values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
//            values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
//            values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
//
//            try {
//                OutputStream imageOutStream = context.getContentResolver().openOutputStream(
//                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon().appendPath(fileName).build());
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, imageOutStream);
//                if (imageOutStream != null) {
//                    imageOutStream.close();
//                }
//                Log.d("SaveCardViewImageTask", "Image saved successfully");
//            } catch (IOException e) {
//                e.printStackTrace();
//                Log.e("SaveCardViewImageTask", "Error saving image", e);
//            }
//        }
//
//        private void saveBitmapToExternalStorage(Bitmap bitmap, String fileName) {
//            File imagePath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), fileName);
//
//            try (FileOutputStream fos = new FileOutputStream(imagePath)) {
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
//                Log.d("SaveCardViewImageTask", "Image saved successfully: " + imagePath.getAbsolutePath());
//            } catch (IOException e) {
//                e.printStackTrace();
//                Log.e("SaveCardViewImageTask", "Error saving image", e);
//            }
//        }
//    }

}

