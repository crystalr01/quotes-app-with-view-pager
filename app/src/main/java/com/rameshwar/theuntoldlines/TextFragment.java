package com.rameshwar.theuntoldlines;

import static androidx.appcompat.content.res.AppCompatResources.getDrawable;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TextFragment extends Fragment {

    public TextFragment() {
        // Required empty public constructor
    }

    ArrayList<Data> shayariData = new ArrayList<>();

    private Dialog loadingDialogue;

    LinearLayout shimmerLinearL;
    ShimmerFrameLayout shimmerFrameLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_text,container,false);

        loadingDialogue = new Dialog(getContext());
        loadingDialogue.setContentView(R.layout.loading);
        loadingDialogue.getWindow().setBackgroundDrawable(getDrawable(getContext(),R.drawable.back_round));
        loadingDialogue.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialogue.setCancelable(false);

     //   loadingDialogue.show();

        shimmerFrameLayout = view.findViewById(R.id.shimmer_view_container);
        shimmerLinearL = view.findViewById(R.id.shimmerLinearL);


        FirebaseDatabase mData = FirebaseDatabase.getInstance();
        DatabaseReference mRef = mData.getReference().child("Untold").child("texts");

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {


                    loadingDialogue.dismiss();

                    for (DataSnapshot snap : snapshot.getChildren()

                    ) {

                        Data data = new Data(snap.getValue().toString());
                        shayariData.add(data);

                        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                        Adapter adapter = new Adapter(shayariData, getContext());
                        layoutManager.setStackFromEnd(true);
                        layoutManager.setReverseLayout(true);
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(adapter);

                     //   loadingDialogue.dismiss();
                        shimmerFrameLayout.stopShimmer();
                        shimmerLinearL.setVisibility(View.GONE);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            //    loadingDialogue.dismiss();

                shimmerFrameLayout.stopShimmer();
                shimmerLinearL.setVisibility(View.GONE);

                Toast.makeText(getContext(), (CharSequence) error, Toast.LENGTH_SHORT).show();

            }
        });

        return view;
    }
}