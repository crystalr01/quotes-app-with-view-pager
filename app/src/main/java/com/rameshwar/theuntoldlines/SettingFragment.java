package com.rameshwar.theuntoldlines;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class SettingFragment extends Fragment {

    public SettingFragment() {
        // Required empty public constructor
    }

    LinearLayout personalinfo, experience, review;
    TextView personalinfobtn, experiencebtn, reviewbtn;

    private ImageView uploadImage,settingImage;
    private EditText uploadName;
    private TextView settingName;
    private Button saveButton;
    private Uri uri;
    private Bitmap bitmapImage;
    DBHelper dbHelper1;
    TextView editProfile,cancelEdit;
    LinearLayout userInfoLinearL,userprofileUpdateLinearL,saved,instaDev,instaUntold;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting,container,false);

        editProfile = view.findViewById(R.id.editProfile);
        cancelEdit = view.findViewById(R.id.cancelEditProfile);
        userInfoLinearL = view.findViewById(R.id.userInfoLinearL);
        userprofileUpdateLinearL = view.findViewById(R.id.userprofileUpdateLinearL);
        instaDev = view.findViewById(R.id.instaDev);
        instaUntold = view.findViewById(R.id.instaUntold);

        userInfoLinearL.setVisibility(View.VISIBLE);
        userprofileUpdateLinearL.setVisibility(View.GONE);
        cancelEdit.setVisibility(View.GONE);

        dbHelper1 = new DBHelper(getContext());

        uploadImage = view.findViewById(R.id.uploadImage);
        uploadName = view.findViewById(R.id.uploadName);
        saveButton = view.findViewById(R.id.saveButton);
        settingImage = view.findViewById(R.id.settingImage);
        settingName = view.findViewById(R.id.settingName);
        saved = view.findViewById(R.id.saved);

        editProfile.setOnClickListener(view1 -> {
            userprofileUpdateLinearL.setVisibility(View.VISIBLE);
            userInfoLinearL.setVisibility(View.GONE);
            editProfile.setVisibility(View.GONE);
            cancelEdit.setVisibility(View.VISIBLE);
//            Intent intent = new Intent(getContext(), UploadSQLiteActivity.class);
//            startActivity(intent);
        });


        cancelEdit.setOnClickListener(view1 -> {
            userprofileUpdateLinearL.setVisibility(View.GONE);
            userInfoLinearL.setVisibility(View.VISIBLE);
            editProfile.setVisibility(View.VISIBLE);
            cancelEdit.setVisibility(View.GONE);
//            Intent cancel = new Intent(getContext(),MainActivity.class);
//            startActivity(cancel);
        });

        instaDev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(Intent.ACTION_VIEW);
                intent1.setData(Uri.parse("https://www.instagram.com/crystal_r_software/"));
                startActivity(intent1);
            }
        });

        instaUntold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(Intent.ACTION_VIEW);
                intent1.setData(Uri.parse("https://www.instagram.com/untold__lines_01/"));
                startActivity(intent1);
            }
        });

        DBHelper dbHelper = new DBHelper(getContext());
        Cursor cursor = dbHelper.getUser();

        if (cursor.getCount() == 0) {
            Toast.makeText(getContext(), "No profile details found", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()){
                settingName.setText(""+cursor.getString(0));
                byte[] imageByte = cursor.getBlob(1);

                Bitmap bitmap = BitmapFactory.decodeByteArray(imageByte,0,imageByte.length);
                settingImage.setImageBitmap(bitmap);
            }
        }

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            assert data != null;
                            uri = data.getData();
                            try {
                                bitmapImage = MediaStore.Images.Media.getBitmap(getActivity().getApplicationContext().getContentResolver(), uri);
                            } catch (IOException e) {
                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            uploadImage.setImageBitmap(bitmapImage);
                        } else {
                            Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        saveButton.setOnClickListener(v -> {
            storeImage();
        });

        saved.setOnClickListener(view1 -> {
//            Intent savedIntent = new Intent(getContext(), SavedActivity.class);
//            startActivity(savedIntent);
        });


//        personalinfo = view.findViewById(R.id.personalinfo);
//        experience = view.findViewById(R.id.experience);
//        review = view.findViewById(R.id.review);
//        personalinfobtn = view.findViewById(R.id.personalinfobtn);
//        experiencebtn = view.findViewById(R.id.experiencebtn);
//        reviewbtn = view.findViewById(R.id.reviewbtn);
        /*making personal info visible*/
//        personalinfo.setVisibility(View.VISIBLE);
//        experience.setVisibility(View.GONE);
//        review.setVisibility(View.GONE);
//
//
//        personalinfobtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                personalinfo.setVisibility(View.VISIBLE);
//                experience.setVisibility(View.GONE);
//                review.setVisibility(View.GONE);
//                personalinfobtn.setTextColor(getResources().getColor(R.color.blue));
//                experiencebtn.setTextColor(getResources().getColor(R.color.grey));
//                reviewbtn.setTextColor(getResources().getColor(R.color.grey));
//
//            }
//        });
//
//        experiencebtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                personalinfo.setVisibility(View.GONE);
//                experience.setVisibility(View.VISIBLE);
//                review.setVisibility(View.GONE);
//                personalinfobtn.setTextColor(getResources().getColor(R.color.grey));
//                experiencebtn.setTextColor(getResources().getColor(R.color.blue));
//                reviewbtn.setTextColor(getResources().getColor(R.color.grey));
//
//            }
//        });
//
//        reviewbtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                personalinfo.setVisibility(View.GONE);
//                experience.setVisibility(View.GONE);
//                review.setVisibility(View.VISIBLE);
//                personalinfobtn.setTextColor(getResources().getColor(R.color.grey));
//                experiencebtn.setTextColor(getResources().getColor(R.color.grey));
//                reviewbtn.setTextColor(getResources().getColor(R.color.blue));
//
//            }
//        });
//

        return view;
    }

    private void storeImage() {
        if (!uploadName.getText().toString().isEmpty() &&
                uploadImage.getDrawable() != null && bitmapImage != null){

            dbHelper1.storeData(new ModelClass(uploadName.getText().toString(),bitmapImage));

            Toast.makeText(getContext(), "User Profile Updated", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getContext(), MainActivity.class);
            startActivity(intent);

            userprofileUpdateLinearL.setVisibility(View.GONE);
            userInfoLinearL.setVisibility(View.VISIBLE);
            editProfile.setVisibility(View.VISIBLE);

        } else {
            Toast.makeText(getContext(), "Fields are mandatory", Toast.LENGTH_SHORT).show();
        }
    }
}