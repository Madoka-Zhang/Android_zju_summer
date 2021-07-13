package com.example.hw2.MPager;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class MpAdapter extends androidx.viewpager.widget.PagerAdapter{
    private int [] mImage;

    public MpAdapter(int[] mImage) {
        this.mImage = mImage;
    }

    @Override
    public int getCount() {
        return mImage.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = new ImageView(container.getContext());
        imageView.setImageResource(mImage[position]);
        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
