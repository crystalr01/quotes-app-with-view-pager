package com.rameshwar.theuntoldlines;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Random;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.NoticeViewAdapter> {

    private Context context;
    private ArrayList<NoticeData> list;

    public NoticeAdapter(Context context, ArrayList<NoticeData> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public NoticeViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.categories_item, parent, false);
        return new NoticeViewAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeViewAdapter holder, int position) {

        final NoticeData currentItem = list.get(position);

        holder.newsText.setText(currentItem.getTitle());

        try {
            if (currentItem.getImage() != null)
                Glide.with(context).load(currentItem.getImage())
                        .into(holder.newsImage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Random random = new Random();
        int color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(1));

        holder.newsText.setBackgroundColor(color);
        holder.itemView.setOnClickListener(new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            Intent intent = new Intent(context,ShowDataActivity.class);
            intent.putExtra("name",currentItem.getTitle());
            context.startActivity(intent);


        }
    });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class NoticeViewAdapter extends RecyclerView.ViewHolder {

        private TextView newsText;
        private ImageView newsImage;

        public NoticeViewAdapter(@NonNull View itemView) {
            super(itemView);

            newsText = itemView.findViewById(R.id.categoryName);
            newsImage = itemView.findViewById(R.id.categoryImage);
        }
    }
}
