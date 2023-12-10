package com.rameshwar.theuntoldlines;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TrendingFragment extends Fragment {

    public TrendingFragment() {

    }

//    private ViewPager viewPager;
//    private ImagePagerAdapter imagePagerAdapter;
//    private List<String> imageUrls;
    ShimmerFrameLayout shimmerFrameLayout;
    LinearLayout shimmerLinearL;

    private ViewPager viewPager;
    private ImageSliderAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_trending,container,false);

        viewPager = view.findViewById(R.id.viewPager);
        shimmerFrameLayout = view.findViewById(R.id.shimmer_view_container);
        shimmerLinearL = view.findViewById(R.id.shimmerLinearL);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Untold").child("viewPager");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<ImageItem> imageItemList = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ImageItem imageItem = snapshot.getValue(ImageItem.class);
                    imageItemList.add(imageItem);
                    shimmerFrameLayout.stopShimmer();
                    shimmerLinearL.setVisibility(View.GONE);
                    adapter = new ImageSliderAdapter(getContext(), imageItemList);
                    viewPager.setAdapter(adapter);
                }

                // Set up ViewPager with the data

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
                shimmerFrameLayout.stopShimmer();
                shimmerLinearL.setVisibility(View.GONE);
            }
        });

//        viewPager = view.findViewById(R.id.viewPager);
//        shimmerFrameLayout = view.findViewById(R.id.shimmer_view_container);
//        shimmerLinearL = view.findViewById(R.id.shimmerLinearL);
//
//        imageUrls = new ArrayList<>();
//        imagePagerAdapter = new ImagePagerAdapter(imageUrls, getContext());
//        viewPager.setAdapter(imagePagerAdapter);
//
//        loadImagesFromFirebase();

        return view;
    }
//    private void loadImagesFromFirebase() {
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Category").child("viewPager");
//
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                imageUrls.clear();
//
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    // Assuming your image URLs are stored as strings
//                    String imageUrl = snapshot.getValue(String.class);
//                    imageUrls.add(imageUrl);
//                    shimmerFrameLayout.stopShimmer();
//                    shimmerLinearL.setVisibility(View.GONE);
//                }
//
//                imagePagerAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // Handle the error
//                Toast.makeText(getContext(), (CharSequence) databaseError, Toast.LENGTH_SHORT).show();
//
//                shimmerFrameLayout.stopShimmer();
//                shimmerLinearL.setVisibility(View.GONE);
//            }
//        });
//    }
}