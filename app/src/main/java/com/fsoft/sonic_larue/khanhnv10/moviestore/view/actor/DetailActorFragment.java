package com.fsoft.sonic_larue.khanhnv10.moviestore.view.actor;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Cache;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.fsoft.sonic_larue.khanhnv10.moviestore.R;
import com.fsoft.sonic_larue.khanhnv10.moviestore.model.Cast;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.movie.MovieService;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.movie.ServiceCallBackListener;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.network.AppController;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.manager.SharedPreferencesManager;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.util.StringUtil;

import java.io.UnsupportedEncodingException;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailActorFragment extends Fragment{

    private View view;
    private ViewMode viewMode;

    private Cast actor;
    private BroadcastReceiver receiver;

    public enum ViewMode {
        DEFAULT(0),
        FULL_SCREEN(1);

        private int type;
        ViewMode(int type) {
            this.type = type;
        }

        private int toInt() {
            return type;
        }

        public ViewMode toViewMode(int newType) {
            if (newType == DEFAULT.toInt()) {
                return ViewMode.DEFAULT;
            } else {
                return ViewMode.FULL_SCREEN;
            }
        }

        public boolean isDefault() {
            return this.type == ViewMode.DEFAULT.toInt();
        }
    }

    public static DetailActorFragment newInstance() {
        DetailActorFragment detailActorFragment = new DetailActorFragment();
        return detailActorFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_actor_detail, container, false);

        // The first load
        viewMode = ViewMode.DEFAULT;
        int viewModeType = SharedPreferencesManager.loadIntPreference(getContext(), "viewModeType");
        viewMode = viewMode.toViewMode(viewModeType);
        changeViewDisplay();

        actor = getArguments().getParcelable("actorDetail");
        if (actor != null) {
            String proFilePath = actor.getProFilePath();

            // Download actor profile and display onto view pager
            if (!StringUtil.isEmpty(proFilePath)) {
                downloadActorProFile(proFilePath);
            }

            // Download actor info and display onto view pager
            downloadActorInfo(actor.getActorId());
        }

        // Register broadcast Change View mode
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int viewModeType = intent.getIntExtra("viewModeType", 0);
                // Re-set Reminder text view
                viewMode = viewMode.toViewMode(viewModeType);
                changeViewDisplay();
                displayHeaderAndFooter(view, actor);
            }
        };

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(
                receiver, new IntentFilter("ChangeViewMode"));

        return view;
    }

    private void downloadActorProFile(String proFilePath) {

        // Load and display actor profile
        ImageLoader imageLoader = AppController.getInstance().getImageLoader();
        NetworkImageView imageView = (NetworkImageView) view.findViewById(R.id.cast_crew_image);
//        String castProFile = cast.getProFilePath();

//        displayHeaderAndFooter(itemView, cast, position);
        imageView.setImageUrl(MovieService.BASE_IMAGE_URL + proFilePath, imageLoader);
        view.findViewById(R.id.loading_panel).setVisibility(View.GONE);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewMode.isDefault()) {
                    viewMode = ViewMode.FULL_SCREEN;

                } else {
                    viewMode = ViewMode.DEFAULT;
                }

                SharedPreferencesManager.savePreference(getContext(),
                        "viewModeType", String.valueOf(viewMode.toInt()));

                // Send broad cast to inform that change view mode
                Intent intentInform = new Intent("ChangeViewMode");
                intentInform.putExtra("viewModeType", viewMode.toInt());
                LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intentInform);
            }
        });
//
    }

    private void downloadActorInfo(String actorId) {

            String conUrl = MovieService.URL_JSON_DETAIL_CASTS;
            conUrl = conUrl.replace("{castId}", actorId);
            Cache cache = AppController.getInstance().getRequestQueue().getCache();
            Cache.Entry entry = cache.get(conUrl);
            try {
                if (entry != null) {
                    // Load data from cached first
                    String resultJson = new String(entry.data, "UTF-8");
                    Cast newActor = MovieService.parsJsonToDetailActor(resultJson);
                    actor = newActor;
                    displayHeaderAndFooter(view, actor);
                } else {
                    // Cached response doesn't exists. Make network call here
                    MovieService.makeCacheData(conUrl, new ServiceCallBackListener() {
                        @Override
                        public void onResult(Object data) {
                            Cast newActor = MovieService.parsJsonToDetailActor(data.toString());
                            actor = newActor;
                            displayHeaderAndFooter(view, actor);
                        }

                        @Override
                        public void onError() {
                            Log.d("onLoadDetailCastAndCrew", "Error!!!!!!!!!!");
                        }
                    });
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

    private void displayHeaderAndFooter(View itemView, Cast cast) {
        TextView actorNameView = (TextView) itemView.findViewById(R.id.actor_name);
        TextView actorBioView = (TextView) itemView.findViewById(R.id.actor_overview_content);
        TextView birthDayView = (TextView) itemView.findViewById(R.id.actor_birthday);

        // Display actor name
        String actorName = cast.getActorName();
        if (!TextUtils.isEmpty(actorName)) {
            actorNameView.setText(actorName);
        }

        // Display actor biography
        String actorBio = cast.getOverView();
        if (!TextUtils.isEmpty(actorBio)) {
            actorBioView.setText(actorBio);
            actorBioView.setMovementMethod(new ScrollingMovementMethod());
        }

        // Display actor birthday
        String actorBirthDay = cast.getBirthDay();
        if (!TextUtils.isEmpty(actorBirthDay)) {
            birthDayView.setText(actorBirthDay);
        }
    }

    private void changeViewDisplay() {
        if (viewMode.isDefault()) {
            view.findViewById(R.id.actor_header).setVisibility(View.VISIBLE);
            view.findViewById(R.id.actor_footer).setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.actor_header).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.actor_footer).setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiver);
    }
}
