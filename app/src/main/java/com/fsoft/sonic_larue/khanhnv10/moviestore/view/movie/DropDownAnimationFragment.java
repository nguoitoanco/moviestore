package com.fsoft.sonic_larue.khanhnv10.moviestore.view.movie;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.fsoft.sonic_larue.khanhnv10.moviestore.R;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.movie.ServiceCallBackListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class DropDownAnimationFragment extends Fragment {

    private ServiceCallBackListener callBackListener;

    public enum DropDown {
        SHOW_OPTION(1),
        HIDE_OPTION(0);

        private int type;

        DropDown( int type) {
            this.type = type;
        }

        private int toInt() {
            return type;
        }
        public boolean isShown() {
            return type == DropDown.SHOW_OPTION.toInt();
        }
    }

    public enum OptionView {
        POPULAR_OPTION(0, R.id.popular_option, R.string.category_pop_movie),
        TOP_RATE_OPTION(1, R.id.top_rate_option, R.string.category_rate_movie),
        UPCOMING_OPTION(2, R.id.upcoming_option, R.string.category_Upcoming_movie),
        NOW_PLAY_OPTION(3, R.id.now_playing_option, R.string.category_Now_movie);

        private int id;
        private int type;
        private int titleId;

        OptionView(int id, int type, int titleId) {
            this.id = id;
            this.type = type;
            this.titleId = titleId;
        }

        public int getId() {
            return id;
        }

        public int getType() {
            return type;
        }

        public int getTitleId() {
            return titleId;
        }
    }

    private  View view;


    public DropDownAnimationFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_drop_down_animation, container, false);
        Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.main_slid_down);
        view.findViewById(R.id.main_anim).startAnimation(anim);
        view.findViewById(R.id.redundant).setVisibility(View.GONE);

        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.findViewById(R.id.redundant).setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        registerViewOnClickListener();
        return view;
    }

    private void registerViewOnClickListener() {
        for (int i = 0; i < OptionView.values().length; i++) {
            final TextView textView = (TextView) view
                    .findViewById(OptionView.values()[i].getType());
            final int index = i;
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callBackListener.onResult(index);
                    getFragmentManager().popBackStack();
                }
            });
        }
    }

    public void setServiceCallBackListener(ServiceCallBackListener serviceCallBackListener) {
        this.callBackListener = serviceCallBackListener;
    }

    @Override
    public void onPause() {
        view.findViewById(R.id.redundant).setVisibility(View.GONE);
        Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.main_slid_up);
        view.startAnimation(anim);
        super.onPause();
    }
}
