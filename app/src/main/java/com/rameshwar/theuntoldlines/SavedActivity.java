package com.rameshwar.theuntoldlines;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SavedActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<ImageModel> list;
    private ImageAdapter imageAdapter;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved);

        String name = getIntent().getStringExtra("name");

        recyclerView = findViewById(R.id.recyclerView);

        reference = FirebaseDatabase.getInstance().getReference().child("poc");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list = new ArrayList<>();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    ImageModel data = snapshot1.getValue(ImageModel.class);
                    list.add(data);
                }

                LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView.setLayoutManager(layoutManager);
                imageAdapter = new ImageAdapter(getApplicationContext(), list);
                recyclerView.setAdapter(imageAdapter);
               // shimmerFrameLayout.stopShimmer();
               // shimmerLinearL.setVisibility(View.GONE);
                // loadingDialogue.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();

            }
        });
    }
}