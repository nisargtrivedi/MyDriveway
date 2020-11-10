package com.driveway.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.driveway.Model.HowAppWorkModel;
import com.driveway.R;


import java.util.ArrayList;

public class HowAppWorkAdapter extends PagerAdapter {


    public Context context;
    public LayoutInflater inflater;
    public ArrayList<HowAppWorkModel> list;
   // public int[] images = {R.drawable.banner_one,R.drawable.banner_two};

    public HowAppWorkAdapter(Context context, ArrayList<HowAppWorkModel> list){
        this.context=context;
        this.list=list;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=inflater.inflate(R.layout.work_row,null);

        WebView pagerImg=view.findViewById(R.id.wbView);
        pagerImg.loadData(list.get(position).Name,"text/html", "UTF-8");
        ViewPager viewPager= (ViewPager) container;
        viewPager.addView(view,0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ViewPager viewPager= (ViewPager) container;
        View view= (View) object;
        viewPager.removeView(view);
    }
}
