package com.rameshwar.theuntoldlines;

import static androidx.appcompat.content.res.AppCompatResources.getDrawable;

import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CategoryFragment extends Fragment {

    public CategoryFragment() {

    }

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ArrayList<NoticeData> list;
    private NoticeAdapter adapter;
    private DatabaseReference reference;

    private Dialog loadingDialogue;

    ShimmerFrameLayout shimmerFrameLayout;
    LinearLayout shimmerLinearL;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View  view = inflater.inflate(R.layout.fragment_category,container,false);

        recyclerView = view.findViewById(R.id.recyclerView);
        shimmerFrameLayout = view.findViewById(R.id.shimmer_view_container);
        shimmerLinearL = view.findViewById(R.id.shimmerLinearL);

        loadingDialogue = new Dialog(getContext());
        loadingDialogue.setContentView(R.layout.loading);
        loadingDialogue.getWindow().setBackgroundDrawable(getDrawable(getContext(),R.drawable.back_round));
        loadingDialogue.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialogue.setCancelable(false);

        //loadingDialogue.show();

        recyclerView = view.findViewById(R.id.recyclerView);

        reference = FirebaseDatabase.getInstance().getReference().child("Untold").child("Category");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list = new ArrayList<>();
             //   loadingDialogue.show();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    NoticeData data = snapshot1.getValue(NoticeData.class);
                    list.add(data);
                }

                GridLayoutManager gridLayoutManager=new GridLayoutManager(getContext(),2);
                recyclerView.setLayoutManager(gridLayoutManager);
                gridLayoutManager.setOrientation(RecyclerView.VERTICAL);
                adapter = new NoticeAdapter(getContext(), list);
                recyclerView.setAdapter(adapter);
                shimmerFrameLayout.stopShimmer();
                shimmerLinearL.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();

            }
        });
        return view;
    }
}