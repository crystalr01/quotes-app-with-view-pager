package com.rameshwar.theuntoldlines;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.nativead.NativeAd;

import java.util.List;

public class ImageAdapter extends  RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private Context context;
    private List<ImageModel> imageModelList;

    private int ad_count = 0;

    public ImageAdapter(Context context, List<ImageModel> imageModelList) {
        this.imageModelList = imageModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public ImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_image_activity,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        if (ad_count == 5){
            AdLoader adLoader = new AdLoader.Builder(context, "ca-app-pub-6972616661119579/7414039288")
                    .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                        @Override
                        public void onNativeAdLoaded(NativeAd nativeAd) {
                            NativeTemplateStyle styles = new
                                    NativeTemplateStyle.Builder().build();
                            TemplateView template = holder.itemView.findViewById(R.id.my_template);
                            template.setStyles(styles);
                            template.setNativeAd(nativeAd);
                            template.setVisibility(View.VISIBLE);
                        }
                    })
                    .build();

            adLoader.loadAd(new AdRequest.Builder().build());
            ad_count = 0;


        } else {
            holder.setData(imageModelList.get(position).getImage());
            ad_count ++;
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,Full_Image_Activity.class);
                intent.putExtra("image",imageModelList.get(position).getImage());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image,customImage;
        TextView customName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.item_image);
            customImage = itemView.findViewById(R.id.customImage);
            customName = itemView.findViewById(R.id.customName);

            DBHelper dbHelper = new DBHelper(context.getApplicationContext());
            Cursor cursor = dbHelper.getUser();

            if (cursor.getCount() == 0) {
            //    Toast.makeText(context.getApplicationContext(), "No profile details found", Toast.LENGTH_SHORT).show();
            } else {
                while (cursor.moveToNext()){
                    customName.setText(""+cursor.getString(0));
                    byte[] imageByte = cursor.getBlob(1);

                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageByte,0,imageByte.length);
                    customImage.setImageBitmap(bitmap);
                }
            }
        }

        private void setData(String url){
            Glide.with(itemView.getContext()).load(url)
                    .into(image);
        }
    }
}
