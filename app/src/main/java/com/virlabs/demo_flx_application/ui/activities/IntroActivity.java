package com.virlabs.demo_flx_application.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.github.vivchar.viewpagerindicator.ViewPagerIndicator;
import com.virlabs.demo_flx_application.Provider.PrefManager;
import com.virlabs.demo_flx_application.R;
import com.virlabs.demo_flx_application.ui.Adapters.IntroAdapter;
import com.virlabs.demo_flx_application.ui.views.ClickableViewPager;

import android.os.Bundle;
import android.view.View;

import android.content.Intent;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class IntroActivity extends AppCompatActivity {
    private ClickableViewPager view_pager_slide;
    private final List<Integer> slideList= new ArrayList<>();
    private PrefManager prefManager;
    private TextView text_view_next_done;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        prefManager= new PrefManager(getApplicationContext());

        slideList.add(1);
        slideList.add(2);
        slideList.add(3);
        slideList.add(4);
        slideList.add(5);

        this.text_view_next_done=(TextView) findViewById(R.id.text_view_next_done);
        LinearLayout linear_layout_next = (LinearLayout) findViewById(R.id.linear_layout_next);
        LinearLayout linear_layout_skip = (LinearLayout) findViewById(R.id.linear_layout_skip);
        ViewPagerIndicator view_pager_indicator = (ViewPagerIndicator) findViewById(R.id.view_pager_indicator);
        this.view_pager_slide=(ClickableViewPager) findViewById(R.id.view_pager_slide);
        IntroAdapter slide_adapter = new IntroAdapter(getApplicationContext(), slideList);
        view_pager_slide.setAdapter(slide_adapter);
        view_pager_slide.setOffscreenPageLimit(1);


        view_pager_slide.setOnItemClickListener(position -> {
            if (position <6){
                view_pager_slide.setCurrentItem(position+1);
            }else{
                redirect();
            }
        });
        linear_layout_next.setOnClickListener(view -> {
            if (text_view_next_done.getText().equals("DONE")){
                redirect();
            }
            if ( view_pager_slide.getCurrentItem() < slideList.size()) {
                view_pager_slide.setCurrentItem(view_pager_slide.getCurrentItem() + 1);
            }

        });
        view_pager_slide.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position+1==slideList.size()){
                    text_view_next_done.setText("DONE");
                }else{
                    text_view_next_done.setText("NEXT");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        linear_layout_skip.setOnClickListener(v -> redirect());
        this.view_pager_slide.setClipToPadding(false);
        this.view_pager_slide.setPageMargin(0);
        view_pager_indicator.setupWithViewPager(view_pager_slide);
    }

    public void redirect(){
        if (prefManager.getString("APP_LOGIN_REQUIRED").equals("TRUE")){
            if (prefManager.getString("LOGGED").equals("TRUE")){
                Intent intent = new Intent(IntroActivity.this,HomeActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
                finish();
            }else{
                Intent intent= new Intent(IntroActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
                finish();
            }
        }else{
            Intent intent = new Intent(IntroActivity.this,HomeActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);
            finish();
        }
    }
}
