package com.fsoft.sonic_larue.khanhnv10.moviestore.view.actor;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.toolbox.NetworkImageView;
import com.fsoft.sonic_larue.khanhnv10.moviestore.R;
import com.fsoft.sonic_larue.khanhnv10.moviestore.model.Cast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.fsoft.sonic_larue.khanhnv10.moviestore.view.actor.DetailActorFragment.*;

public class DetailActorActivity extends FragmentActivity {

    private ActorPagerAdapter actorPagerAdapter;
    private ViewPager viewPager;
    private ImageView leftArrowView;
    private ImageView rightArrowView;
    private Button btnDone;

    private List<Cast> castList;
    private int currentPos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_actor);
        currentPos = getIntent().getIntExtra("currentPos", 0);
        castList = getIntent().getParcelableArrayListExtra("castList");
        if (castList == null) {
            castList = new ArrayList<>();
        }

        actorPagerAdapter = new ActorPagerAdapter(getSupportFragmentManager(), castList);
        viewPager = (ViewPager)findViewById(R.id.detail_actor_pager);
        viewPager.setAdapter(actorPagerAdapter);
        viewPager.setCurrentItem(currentPos);
        addOnViewPageChangeAction();

        leftArrowView = (ImageView)findViewById(R.id.left_arrow);
        rightArrowView = (ImageView)findViewById(R.id.right_arrow);
        btnDone = (Button)findViewById(R.id.btn_close);
        addOnClickButtonAction();
    }

    private void setDisplayRightLeftArrow(final int pos, int sumPage) {
        if (pos == 0) {
            leftArrowView.setVisibility(View.GONE);
            leftArrowView.setOnClickListener(null);
        } else if (pos == sumPage - 1) {
            rightArrowView.setVisibility(View.GONE);
            rightArrowView.setOnClickListener(null);
        } else {
            leftArrowView.setVisibility(View.VISIBLE);
            rightArrowView.setVisibility(View.VISIBLE);

            leftArrowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewPager.setCurrentItem(pos - 1);
                }
            });

            rightArrowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewPager.setCurrentItem(pos + 1);
                }
            });
        }
    }

    private void addOnClickButtonAction() {
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void addOnViewPageChangeAction() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    setDisplayRightLeftArrow(position, castList.size());
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
    }
}
