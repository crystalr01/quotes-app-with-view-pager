package com.rameshwar.theuntoldlines;

import static com.google.android.material.internal.ContextUtils.getActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.datatransport.BuildConfig;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends MainApp {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ArrayList<NoticeData> list;
    private NoticeAdapter adapter;
    private DatabaseReference reference;

    private Dialog loadingDialogue;
    ActionBarDrawerToggle drawerToggle;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    String url = "https://play.google.com/store/apps/dev?id=5194175941837931502";

    BottomNavigationView bottomNavigationView;

    public static Context contextOfApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

     //   recyclerView = findViewById(R.id.recyclerView);
//        loadingDialogue = new Dialog(this);
//        loadingDialogue.setContentView(R.layout.loading);
//        loadingDialogue.getWindow().setBackgroundDrawable(getDrawable(R.drawable.back_round));
//        loadingDialogue.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        loadingDialogue.setCancelable(false);

        bottomNavigationView = findViewById(R.id.bottom_navigation);



        getSupportFragmentManager().beginTransaction().replace(R.id.body_container,new TrendingFragment()).commit();
        bottomNavigationView.setSelectedItemId(R.id.trending);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment fragment = null;

                int id = item.getItemId();

                if(id == R.id.trending){
                    fragment = new TrendingFragment();
                } else if (id == R.id.categories){
                    fragment = new CategoryFragment();
                } else if (id == R.id.texts){
                    fragment = new TextFragment();
                } else {
                    fragment = new SettingFragment();
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.body_container,fragment).commit();

                return true;
            }
        });

    //    loadingDialogue.show();
        drawerLayout = findViewById(R.id.drawerLAyout);
        navigationView = findViewById(R.id.navView);
        drawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.shareApp) {
                    try {
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("text/plain");
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name");
                        String shareMessage = "\nLet me recommend you this application \nFor high level motivational quotes.\nDownload and Share" +
                                " Marathi Motivational Quotes with more than 10+ Categories!\n\n";
                        shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
                        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                        startActivity(Intent.createChooser(shareIntent, "choose one"));
                    } catch (Exception e) {
                        //e.toString();
                    }
                } else if (item.getItemId() == R.id.moreApps) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://play.google.com/store/apps/dev?id=5194175941837931502"));
                    startActivity(intent);

                } else if (item.getItemId() == R.id.rateApp) {
                    Intent intent1 = new Intent(Intent.ACTION_VIEW);
                    intent1.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.rameshwar.theuntoldlines"));
                    startActivity(intent1);
                }
//                } else if (item.getItemId() == R.id.follow) {
//                    Intent intent4 = new Intent(Intent.ACTION_VIEW);
//                    intent4.setData(Uri.parse("https://www.instagram.com/its_ram_patil._/"));
//                    startActivity(intent4);
//
//                } else if (item.getItemId() == R.id.calisthenics) {
//                    Intent intent4 = new Intent(Intent.ACTION_VIEW);
//                    intent4.setData(Uri.parse("https://www.instagram.com/untold__lines_01/"));
//                    startActivity(intent4);
//                }
                else {
                    Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        View header = navigationView.getHeaderView(0);
        ImageView navImage = (ImageView) header.findViewById(R.id.navImage);
        TextView navName = (TextView) header.findViewById(R.id.navName);

        DBHelper dbHelper = new DBHelper(this);
        Cursor cursor = dbHelper.getUser();

        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No profile details found", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()){
                navName.setText(""+cursor.getString(0));
                byte[] imageByte = cursor.getBlob(1);

                Bitmap bitmap = BitmapFactory.decodeByteArray(imageByte,0,imageByte.length);
                navImage.setImageBitmap(bitmap);
            }
        }

        navImage.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this,UploadSQLiteActivity.class);
            startActivity(intent);
        });

    //    recyclerView = findViewById(R.id.recyclerView);

//        reference = FirebaseDatabase.getInstance().getReference().child("Category");
//
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                list = new ArrayList<>();
//                loadingDialogue.show();
//                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
//                    NoticeData data = snapshot1.getValue(NoticeData.class);
//                    list.add(data);
//                }
//
//                GridLayoutManager gridLayoutManager=new GridLayoutManager(MainActivity.this,2);
//                recyclerView.setLayoutManager(gridLayoutManager);
//                gridLayoutManager.setOrientation(RecyclerView.VERTICAL);
//                adapter = new NoticeAdapter(MainActivity.this, list);
//                recyclerView.setAdapter(adapter);
//                loadingDialogue.dismiss();
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//                Toast.makeText(MainActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
//
//            }
//        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack();
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setMessage("Are you want to Exit...?")
//                .setCancelable(false)
//                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        MainActivity.super.onBackPressed();
//                    }
//                })
//                .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                })
//
//                .setNeutralButton("More Apps", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
//                    }
//                });
//
//        AlertDialog alertDialog = builder.create();
//        alertDialog.show();

        if (drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}