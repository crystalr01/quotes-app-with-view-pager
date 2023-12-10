package com.rameshwar.theuntoldlines;

import android.view.View;

import androidx.viewpager.widget.ViewPager;

public class VerticalPageTransformer implements ViewPager.PageTransformer {

    @Override
    public void transformPage(View view, float position) {
        view.setTranslationX(view.getWidth() * -position);
        view.setTranslationY(position < 0 ? position * view.getHeight() : 0f);
        float scaleFactor = position < 0 ? 1f : 1f - position;
        view.setScaleX(scaleFactor);
        view.setScaleY(scaleFactor);
    }
}
