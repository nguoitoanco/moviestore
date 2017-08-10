package com.fsoft.sonic_larue.khanhnv10.moviestore.view.movie;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.volley.Cache;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.fsoft.sonic_larue.khanhnv10.moviestore.R;
import com.fsoft.sonic_larue.khanhnv10.moviestore.dao.MovieDao;
import com.fsoft.sonic_larue.khanhnv10.moviestore.dao.ReminderDao;
import com.fsoft.sonic_larue.khanhnv10.moviestore.model.Cast;
import com.fsoft.sonic_larue.khanhnv10.moviestore.model.Movie;
import com.fsoft.sonic_larue.khanhnv10.moviestore.model.Reminder;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.movie.DetailCallBackListener;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.movie.MovieService;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.movie.ReminderService;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.movie.ServiceCallBackListener;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.network.AppController;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.util.DateUtil;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.util.MovieDatePicker;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.util.MovieTimePicker;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.manager.SharedPreferencesManager;
import com.fsoft.sonic_larue.khanhnv10.moviestore.view.actor.DetailActorActivity;
import com.google.gson.Gson;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DetailMovieFragment extends Fragment {

    private DetailMovieAdapter detailMovieAdapter;
    private RecyclerView recyclerView;
    private List<Cast> castList;
    private Movie movie;
    Calendar calendar;
    private long timeAlarm = 0;
    private View view ;
    private DetailCallBackListener callBack;

    private BroadcastReceiver receiverFavMovie;
    private BroadcastReceiver receiverReminder;

    public static DetailMovieFragment newInstance() {
        DetailMovieFragment fragment = new DetailMovieFragment();
        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        castList = new ArrayList<>();
        setHasOptionsMenu(true);

        callBack = new DetailCallBackListener() {
            @Override
            public void displayDetailMovie(Object data) {

            }

            @Override
            public void displayDetailCastCrew(int pos) {
                showDetailCastAndCrew(pos);
            }
        };


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        movie = (Movie) getArguments().getSerializable("movie_detail");

        view = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        setViewLayout(view);
        recyclerView = (RecyclerView) view.findViewById(R.id.cast_and_crew_list);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager lineLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(lineLayoutManager);

        detailMovieAdapter = new DetailMovieAdapter(castList, getContext(), callBack);
        recyclerView.setAdapter(detailMovieAdapter);

        // Get cast and crew list of movie
        Log.d("DetailMovieFragment", "" + movie.getMovieId());
        onLoadCastAndCrew(movie.getMovieId());

        Button btnReminder = (Button) view.findViewById(R.id.remind_button);
        btnReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupAlarmTime();
                Log.d("DetailMovieFragment", timeAlarm + "");
            }
        });

        // Register broadcast update fave movie
        receiverFavMovie = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String movieId = intent.getStringExtra("movie_id");
                if (movie.getMovieId().equals(movieId)) {
                    movie.setMovieFav(Movie.IS_NOT_FAV);
                    drawImageIcon();
                }
            }
        };

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(
                receiverFavMovie, new IntentFilter("UpdateFavMovie"));

        // Register broadcast update reminder movie
        receiverReminder = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Re-set Reminder text view
                setReminderDateView(view);
            }
        };

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(
                receiverReminder, new IntentFilter("ReminderUpdated"));

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.movies_detail, menu);
        Log.d("DetailMovieFragment", "onCreateOptionsMenu:show");
    }

    private void setViewLayout(View view) {
        // Draw image icon
        drawImageIcon();

        TextView releaseDateView = (TextView) view.findViewById(R.id.release_date);
        releaseDateView.setText(movie.getReleaseDate());

        TextView rateCountView = (TextView) view.findViewById(R.id.movie_rate_count);
        rateCountView.setText(movie.getRating());

        NetworkImageView movieIconView = (NetworkImageView) view.findViewById(R.id.movie_icon);
        // Load image profile
        ImageLoader imageLoader = AppController.getInstance().getImageLoader();

        if (!TextUtils.isEmpty(movie.getMoviePosterPath())) {
            DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
            float mHeight = displayMetrics.heightPixels;
            float mWidth = displayMetrics.widthPixels;
            movieIconView.getLayoutParams().width = (int) (mWidth / 3);
            movieIconView.getLayoutParams().height = (int) (mWidth / 3);
            movieIconView.setImageUrl(movie.getMoviePosterPath(), imageLoader);
        }

        TextView overView = (TextView) view.findViewById(R.id.movie_overview);
        overView.setText(movie.getOverView());
        overView.setMovementMethod(new ScrollingMovementMethod());

        // Set display reminder date if the movie in reminder list
        setReminderDateView(view);
    }

    private void setReminderDateView(View view) {
        TextView reminderDateView = (TextView) view.findViewById(R.id.reminder_date);
        ReminderDao reminderDao = new ReminderDao(getContext());
        Reminder reminder = reminderDao.getReminderById(movie.getMovieId());
        if (reminder != null) {
            reminderDateView.setVisibility(View.VISIBLE);
            reminderDateView.setText(
                    DateUtil.timeStampToString(
                            reminder.getReminderDate(), DateUtil.FORMAT_YYYY_MM_DD_HH_MM_SS));
        } else {
            reminderDateView.setVisibility(View.GONE);
        }
    }

    private void drawImageIcon() {
        final ImageView favImageView = (ImageView) view.findViewById(R.id.fav_icon);
        if (movie.isMovieFavourite()) {
            favImageView.setImageResource(R.mipmap.ic_start_selected);
            favImageView.setOnClickListener(null);
        } else if (!movie.isMovieFavourite()) {
            favImageView.setImageResource(R.mipmap.ic_star_border_black);
            favImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    movie.setMovieFav(Movie.IS_FAV);
                    favImageView.setImageResource(R.mipmap.ic_start_selected);
                    MovieDao movieDao = new MovieDao(getContext());
                    movieDao.insert(movie);

                    // Send broad cast to update favourite ui
                    Intent intent = new Intent("UpdateUI");
                    LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);

                    String favCount = String.valueOf(movieDao.countFavMovie());
                    SharedPreferencesManager.savePreference(getContext(),
                            SharedPreferencesManager.FAV_NUMBER, favCount);
                }
            });
        }

    }

    private void onLoadCastAndCrew(String movieId) {
        String conUrl = MovieService.URL_JSON_CASTS;
        conUrl = conUrl.replace("{movieId}", movieId);
        Cache cache = AppController.getInstance().getRequestQueue().getCache();
        Cache.Entry entry = cache.get(conUrl);
        try {
            if (entry != null) {
                // Load data from cached first
                String resultJson = new String(entry.data, "UTF-8");
                displayCastAndCrew(resultJson);
            } else {
                // Cached response doesn't exists. Make network call here
                MovieService.makeCacheData(conUrl, new ServiceCallBackListener() {
                    @Override
                    public void onResult(Object data) {
                        displayCastAndCrew(data.toString());
                    }

                    @Override
                    public void onError() {
                    }
                });
            }

        } catch (UnsupportedEncodingException e) {
        }
    }

    private void displayCastAndCrew(String resultJson) {
        List<Cast> casts = null;
        if (resultJson != null && resultJson.length() > 0) {
            try {
                casts = MovieService.parsJsonToCasts(resultJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (casts != null && casts.size() > 0) {
            castList.addAll(casts);
            if (detailMovieAdapter != null) {
                detailMovieAdapter.notifyDataSetChanged();
            } else {
                detailMovieAdapter = new DetailMovieAdapter(castList, getContext(), callBack);

                recyclerView.setAdapter(detailMovieAdapter);
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void setupAlarmTime() {
        calendar = Calendar.getInstance();
        final MovieDatePicker movieDatePicker = new MovieDatePicker();
        movieDatePicker.show(getFragmentManager(), getString(R.string.start_datepicker_title));
        movieDatePicker.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker dateView, final int year, final int monthOfYear, final int dayOfMonth) {
                if (dateView.isShown()) {
                    MovieTimePicker movieTimePicker = new MovieTimePicker();
                    movieTimePicker.show(getFragmentManager(), getString(R.string.start_datepicker_title));
                    movieTimePicker.setOnTimeSetListener(new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timeView, int hourOfDay, int minute) {
                            if (timeView.isShown()) {
                                calendar.set(year, monthOfYear, dayOfMonth, hourOfDay, minute, 0);
                                ReminderService reminderService = new ReminderService();
                                reminderService.createNewReminder(getContext(), calendar.getTimeInMillis(), movie);
                                setReminderDateView(view);

                                // send broad cast create reminder
                                Intent intentInform = new Intent("ReminderUpdated");
                                intentInform.putExtra("reminder_id", movie.getMovieId());

                                LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intentInform);
                                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intentInform);
                            }
                        }
                    });
                }
            }
        });
    }

    private void showDetailCastAndCrew(int currentPos) {
        Intent intent = new Intent(getActivity(), DetailActorActivity.class);
        Gson gson = new Gson();
        intent.putParcelableArrayListExtra("castList", (ArrayList<Cast>) castList);
        intent.putExtra("currentPos", currentPos);

        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("movie_title", movie.getMovieTitle());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiverReminder);
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiverFavMovie);
    }

    public String getMovieTitle() {
        return movie.getMovieTitle();
    }
}
