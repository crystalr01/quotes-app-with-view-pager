package com.rameshwar.theuntoldlines;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.firebase.database.collection.BuildConfig;

import java.util.ArrayList;
import java.util.Random;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    ArrayList<Data> shayariData;
    Context context;
    Activity activity;

    private int ad_count = 0;
    private InterstitialAd mInterstitialAd;

    public Adapter(ArrayList<Data> sharariData, Context context) {
        this.shayariData = sharariData;
        this.context = context;
        this.activity = activity;
    }

    @NonNull

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_shayari_layout,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint({"NewApi", "RestrictedApi"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

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
            holder.shayariText.setText(shayariData.get(position).getShayari());
            ad_count ++;
        }


        setAnimation(holder.itemView);

        Random random = new Random();
        int color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
        holder.cardview.setCardBackgroundColor(color);
      //  holder.cardview.setCardBackgroundColor(holder.itemView.getResources().getColor(getRandomColor(),null));

        holder.cardview.setOnClickListener(v -> {
            Random random1 = new Random();
            int color1 = Color.argb(255, random1.nextInt(256), random1.nextInt(256), random1.nextInt(256));
       //     int color1 = getRandomColor();
            holder.cardview.setCardBackgroundColor(color1);
      //      holder.cardview.setCardBackgroundColor(color1);

        });

//        if (position %5 == 0){
//            MobileAds.initialize(context);
//            AdLoader adLoader = new AdLoader.Builder(context, "ca-app-pub-3940256099942544/2247696110")
//                    .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
//                        @Override
//                        public void onNativeAdLoaded(NativeAd nativeAd) {
//                            NativeTemplateStyle styles = new
//                                    NativeTemplateStyle.Builder().build();
//                            TemplateView template = holder.itemView.findViewById(R.id.my_template);
//                            template.setStyles(styles);
//                            template.setNativeAd(nativeAd);
//                            template.setVisibility(View.VISIBLE);
//                        }
//                    })
//                    .build();
//
//            adLoader.loadAd(new AdRequest.Builder().build());
//        }

        holder.copyBtn.setOnClickListener(v -> {

            showAd();

            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", shayariData.get(position).getShayari().concat("\n\nDownload App - shorturl.at/nsDI8"));
            clipboard.setPrimaryClip(clip);

            Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show();

            if (Admob.mInterstitial != null){
                Admob.mInterstitial.show(activity);
                Admob.mInterstitial.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();
                        Admob.mInterstitial = null;
                        Admob.loadInter(activity);
                    }
                });
            }

        });

        holder.shareBtn.setOnClickListener(v -> {

            try {

                if (Admob.mInterstitial != null){
                    Admob.mInterstitial.show(activity);
                    Admob.mInterstitial.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent();
                            Admob.mInterstitial = null;
                            Admob.loadInter(activity);
                        }
                    });
                }

                Intent txtIntent = new Intent(Intent.ACTION_SEND);
                txtIntent.setType("text/plain");
                //    txtIntent.putExtra(Intent.EXTRA_SUBJECT, "Shayari");
                txtIntent.putExtra(Intent.EXTRA_TEXT,shayariData.get(position).getShayari().concat("\n\nDownload App - shorturl.at/nsDI8"));
                txtIntent = Intent.createChooser(txtIntent,"Shre by");
                holder.itemView.getContext().startActivity(txtIntent);

                Toast.makeText(context, "Sharing", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                e.printStackTrace();
            }
        });


        holder.editBtn.setOnClickListener(view -> {

                Intent intent = new Intent(context,Fullshayari.class);
                intent.putExtra("shayariText",shayariData.get(position).getShayari());
                context.startActivity(intent);

        });

        holder.whatsappBtn.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, shayariData.get(position).getShayari().concat("\n\nDownload this amazing app. \n\nhttps://play.google.com/store/apps/details?id="+BuildConfig.APPLICATION_ID));
            intent.setType("text/plain");
            intent.setPackage("com.whatsapp");
        //    intent.putExtra(Intent.EXTRA_TEXT,"https://play.google.com/store/apps/details?id="+ BuildConfig.APPLICATION_ID);
            context.startActivity(Intent.createChooser(intent, "Share with"));

        });
    }

   /* private int getRandomColor() {

        List<Integer> colorcode = new ArrayList<>();
        colorcode.add(R.color.color1);
        colorcode.add(R.color.color2);
        colorcode.add(R.color.color3);
        colorcode.add(R.color.color4);
        colorcode.add(R.color.color5);
        colorcode.add(R.color.color6);
        colorcode.add(R.color.color7);

        Random random = new Random();
        int number = random.nextInt(colorcode.size());

        return colorcode.get(number);

    }

    */

    private void setAnimation(View view){
        Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
        view.setAnimation(animation);
    }

    @Override
    public int getItemCount() {
        return shayariData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView shayariText;
        TextView customeShayariName;
        ImageView customShayariImage;
        ImageView shareBtn;
        ImageView editBtn,whatsappBtn;
        ImageView copyBtn;
        CardView cardview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            shayariText = itemView.findViewById(R.id.shayariText);
            copyBtn = itemView.findViewById(R.id.copyButton);
            shareBtn = itemView.findViewById(R.id.shareButton);
            cardview = itemView.findViewById(R.id.item_card);
            editBtn = itemView.findViewById(R.id.editButton);
            whatsappBtn = itemView.findViewById(R.id.whatsappButton);

            customeShayariName = itemView.findViewById(R.id.customShayariName);
            customShayariImage = itemView.findViewById(R.id.customShayariImage);

            DBHelper dbHelper = new DBHelper(context.getApplicationContext());
            Cursor cursor = dbHelper.getUser();

            if (cursor.getCount() == 0) {
            //    Toast.makeText(context.getApplicationContext(), "No profile details found", Toast.LENGTH_SHORT).show();
            } else {
                while (cursor.moveToNext()){
                    customeShayariName.setText(""+cursor.getString(0));
                    byte[] imageByte = cursor.getBlob(1);

                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageByte,0,imageByte.length);
                    customShayariImage.setImageBitmap(bitmap);
                }
            }

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