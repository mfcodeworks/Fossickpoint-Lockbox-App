package com.fossickpoint;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fossickpoint.Constants.Commons;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by dennisdarwis on 27/1/18.
 */

public class ViewPagerAdapter extends PagerAdapter {
    Activity activity;
    List<ArticleModel> articleModelList;
    private LayoutInflater layoutInflater;
    String TAG = "ViewPagerAdapter";

    public ViewPagerAdapter(Activity activity, List<ArticleModel> articleModelList) {
        this.activity = activity;
        this.articleModelList = articleModelList;
    }

    @Override
    public int getCount() {
        return (int)Math.ceil((double) articleModelList.size()/2);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) activity
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.list_item_article, null);
        RelativeLayout layout1 = view.findViewById(R.id.layout1);
        RelativeLayout layout2 = view.findViewById(R.id.layout2);
        ImageView image1 = view.findViewById(R.id.image1);
        ImageView image2 = view.findViewById(R.id.image2);
        TextView title1 = view.findViewById(R.id.title1);
        TextView title2 = view.findViewById(R.id.title2);

        int actualPosition = position*2;

        ArticleModel model1 = articleModelList.get(actualPosition);
        setInterface(image1, title1, model1);
        if(!(actualPosition+2>articleModelList.size())){
            ArticleModel model2 = articleModelList.get(actualPosition+1);
            setInterface(image2, title2, model2);
        } else{
            layout2.setVisibility(View.GONE);
        }

        container.addView(view);
        return view;
    }

    private void setInterface(ImageView image, TextView title, ArticleModel model) {
        title.setText(model.getArticleName());
        //String imageURL = model.getImgURL();
        String imageURL = Commons.PLACEHOLDER_IMG;
        Picasso.with(activity).load(imageURL).placeholder(R.drawable.logo)
            .error(R.drawable.logo)
            .into(image);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
       container.removeView((View) object);
    }
}
