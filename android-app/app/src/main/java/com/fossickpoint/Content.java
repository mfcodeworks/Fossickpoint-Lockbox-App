package com.fossickpoint;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.fossickpoint.Constants.Commons;
import com.fossickpoint.Constants.UserConstants;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by dennisdarwis on 27/1/18.
 */

public class Content extends Fragment {

    RelativeLayout layout_content;
    RelativeLayout layout_redeem_code;
    RelativeLayout button_verify;
    RelativeLayout action_bar;

    ViewPager view_pager_identity, view_pager_communication;
    TabLayout tab_dots_identity, tab_dots_communication;
    String TAG = "Content";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_content, container, false);

        layout_content = v.findViewById(R.id.layout_content);
        layout_redeem_code = v.findViewById(R.id.layout_redeem_code);
        button_verify = v.findViewById(R.id.button_verify);
        action_bar = v.findViewById(R.id.action_bar);

        view_pager_identity = v.findViewById(R.id.view_pager_identity);
        tab_dots_identity = v.findViewById(R.id.tab_dots_identity);
        tab_dots_identity.setupWithViewPager(view_pager_identity, true);

        view_pager_communication = v.findViewById(R.id.view_pager_communication);
        tab_dots_communication = v.findViewById(R.id.tab_dots_communication);
        tab_dots_communication.setupWithViewPager(view_pager_communication, true);

        setPlaceHolderArticles(view_pager_identity);
        setPlaceHolderArticles(view_pager_communication);

        SharedPreferences userPrefs = getActivity().getSharedPreferences(UserConstants.USER_PREFS, MODE_PRIVATE);
        int userType = userPrefs.getInt(UserConstants.USER_TYPE, 1);
        if(userType==1){
            layout_redeem_code.setVisibility(View.VISIBLE);
            layout_content.setVisibility(View.GONE);
            action_bar.setVisibility(View.GONE);
        } else{
            layout_redeem_code.setVisibility(View.GONE);
            layout_content.setVisibility(View.VISIBLE);
            action_bar.setVisibility(View.VISIBLE);
        }
        button_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toRedeemCode();
            }
        });

        return v;
    }

    private void setPlaceHolderArticles(ViewPager viewPager) {
        List<ArticleModel> articleModelList = new ArrayList<>();
        articleModelList.add(new ArticleModel("Article 1", Commons.PLACEHOLDER_IMG));
        articleModelList.add(new ArticleModel("Article 2", Commons.PLACEHOLDER_IMG));
        articleModelList.add(new ArticleModel("Article 3", Commons.PLACEHOLDER_IMG));
        articleModelList.add(new ArticleModel("Article 4", Commons.PLACEHOLDER_IMG));
        articleModelList.add(new ArticleModel("Article 5", Commons.PLACEHOLDER_IMG));
        ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity(), articleModelList);
        viewPager.setAdapter(adapter);
        Log.d(TAG, "getCount: "+adapter.getCount());
    }

    private void toRedeemCode() {
        Intent intent = new Intent(getActivity(), RedeemCode.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }
}
