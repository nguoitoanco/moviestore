package com.fsoft.sonic_larue.khanhnv10.moviestore.view.movie;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.fsoft.sonic_larue.khanhnv10.moviestore.R;
import com.fsoft.sonic_larue.khanhnv10.moviestore.dao.MovieDao;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.movie.DetailCallBackListener;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.manager.SharedPreferencesManager;
import com.fsoft.sonic_larue.khanhnv10.moviestore.model.Movie;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.movie.MovieService;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.network.AppController;

import java.util.List;

import static com.fsoft.sonic_larue.khanhnv10.moviestore.view.movie.MovieListFragment.*;

/**
 * Created by KhanhNV10 on 2015/11/23.
 */
public class MovieRVAdapter extends RecyclerView.Adapter<MovieRVAdapter.MovieViewHolder> {

    private ViewType viewType;
    private List<Movie> movieList;
    private List<String> favMovieIds;
    private Context context;
    private DetailCallBackListener detailCallBackListener;

    public class MovieViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitleView;
        private NetworkImageView mIconView;
        private TextView mDateView;
        private TextView mRateView;
        private TextView mOverView;
        private ImageView favImageView;
        private ImageView adultImage;

        public MovieViewHolder(View itemView) {
            super(itemView);
            mTitleView = (TextView) itemView.findViewById(R.id.movie_title);
            mRateView = (TextView) itemView.findViewById(R.id.movie_rate);
            mIconView = (NetworkImageView) itemView.findViewById(R.id.movie_icon);
            mDateView = (TextView) itemView.findViewById(R.id.release_date);
            mOverView = (TextView) itemView.findViewById(R.id.movie_overview);
            favImageView = (ImageView) itemView.findViewById(R.id.favourite_image);
            adultImage = (ImageView) itemView.findViewById(R.id.adult_image);
        }


        public TextView getTitleView() {
            return mTitleView;
        }

        public NetworkImageView getIconView() {
            return mIconView;
        }

        public TextView getDateView() {
            return mDateView;
        }

        public TextView getOverView() {
            return mOverView;
        }

        public TextView getRateView() {
            return mRateView;
        }

        public ImageView getFavImageView() {
            return favImageView;
        }

        public ImageView getAdultImage() {
            return adultImage;
        }

        public void setAdultImage(ImageView adultImage) {
            this.adultImage = adultImage;
        }
    }


    public MovieRVAdapter(List<Movie> movies, ViewType viewType, Context context,
                          DetailCallBackListener detailCallBackListener) {
        this.viewType = viewType;
        this.movieList = movies;
        this.context = context;
        MovieDao movieDao = new MovieDao(context);
        favMovieIds = movieDao.getFavMovieIds();
        this.detailCallBackListener = detailCallBackListener;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        if (this.viewType.isGridView()) {
            view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.movie_list_items_grid, parent, false);
        } else {
            view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.movie_list_items_default, parent, false);
        }

        MovieViewHolder movieViewHolder = new MovieViewHolder(view);
        return movieViewHolder;
    }

    @Override
    public void onBindViewHolder(final MovieViewHolder movieViewHolder, final int position) {
        movieViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailCallBackListener.displayDetailMovie(position);
            }
        });

        setViewContent(movieViewHolder, position);
    }

    private void setViewContent(MovieViewHolder mvh, int pos) {
        final Movie movie = movieList.get(pos);
        mvh.getTitleView().setText(movie.getMovieTitle());

        // Load image
        ImageLoader imageLoader = AppController.getInstance().getImageLoader();
        String moviePoster = movie.getMoviePoster();
        NetworkImageView imageView = mvh.getIconView();
        if(!TextUtils.isEmpty(moviePoster)) {
            imageView.setImageUrl(MovieService.BASE_IMAGE_URL + moviePoster, imageLoader);
            movie.setMoviePosterPath(MovieService.BASE_IMAGE_URL + moviePoster);
        }

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float mWidth = displayMetrics.widthPixels;
        if (viewType.isDefaultView()) {
            // Set move poster
            mvh.getIconView().getLayoutParams().width = (int)(mWidth / 3);
            mvh.getIconView().getLayoutParams().height = (int)(mWidth / 3);
            final ImageView favImageView = mvh.getFavImageView();
            if (favMovieIds.contains(movie.getMovieId())) {
                favImageView.setImageResource(R.mipmap.ic_start_selected);
                movie.setMovieFav(Movie.IS_FAV);
            } else {
                favImageView.setImageResource(R.mipmap.ic_star_border_black);
                movie.setMovieFav(Movie.IS_NOT_FAV);
            }

            // If movie is not favourite yet
            if (!movie.isMovieFavourite()) {
                favImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        movie.setMovieFav(Movie.IS_FAV);
                        favImageView.setImageResource(R.mipmap.ic_start_selected);
                        MovieDao movieDao = new MovieDao(context);
                        movieDao.insert(movie);

                        // Send broad cast to update favourite ui
                        Intent intent = new Intent("UpdateUI");
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                        String favCount = String.valueOf(movieDao.countFavMovie());
                        SharedPreferencesManager.savePreference(context,
                                SharedPreferencesManager.FAV_NUMBER, favCount);
                    }
                });
            } else {
                favImageView.setOnClickListener(null);
            }

            if (movie.isAdult()) {
                mvh.getAdultImage().setVisibility(View.VISIBLE);
            }

            // Set movie rate
            TextView rateView =  mvh.getRateView();
            rateView.setText(movie.getRating());

            // Set movie date release
            TextView dateView = mvh.getDateView();
            dateView.setText(movie.getReleaseDate());

            // Set movie overview
            String overView = movie.getOverView();
            mvh.getOverView().setText(overView);
        } else {
            mvh.getIconView().getLayoutParams().width = (int)(mWidth / 2);
            mvh.getIconView().getLayoutParams().height = (int)(mWidth / 2);
        }
    }
    @Override
    public int getItemCount() {
        return this.movieList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
