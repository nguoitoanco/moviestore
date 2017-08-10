package com.fsoft.sonic_larue.khanhnv10.moviestore.view.movie;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.fsoft.sonic_larue.khanhnv10.moviestore.R;
import com.fsoft.sonic_larue.khanhnv10.moviestore.model.Movie;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.movie.DetailCallBackListener;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.movie.MovieService;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.network.AppController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KhanhNV10 on 2015/11/23.
 */
public class FavouriteMovieAdapter
        extends ContextMenuRecyclerView.Adapter<FavouriteMovieAdapter.MovieViewHolder> implements Filterable{

    private FavouriteMovieFragment favouriteMovieFragment;
    private List<Movie> movieList;
    private List<Movie> saveMovieList;
    private Context context;
    private DetailCallBackListener detailCallBackListener;

    public class MovieViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitleView;
        private NetworkImageView mIconView;
        private TextView mDateView;
        private TextView mRateView;
        private TextView mOverView;

        public MovieViewHolder(View itemView) {
            super(itemView);
            mTitleView = (TextView) itemView.findViewById(R.id.movie_title);
            mRateView = (TextView) itemView.findViewById(R.id.movie_rate);
            mIconView = (NetworkImageView) itemView.findViewById(R.id.movie_icon);
            mDateView = (TextView) itemView.findViewById(R.id.release_date);
            mOverView = (TextView) itemView.findViewById(R.id.movie_overview);
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
    }


    public FavouriteMovieAdapter(FavouriteMovieFragment mff, List<Movie> movies, DetailCallBackListener callBack) {
        favouriteMovieFragment = mff;
        this.movieList = movies;
        saveMovieList = new ArrayList<>(movieList);
        this.context = mff.getContext();
        this.detailCallBackListener = callBack;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.fragment_favourite_movie_list, parent, false);

        MovieViewHolder movieViewHolder = new MovieViewHolder(view);
        return movieViewHolder;
    }

    @Override
    public void onBindViewHolder(final MovieViewHolder movieViewHolder, final int position) {
        movieViewHolder.itemView.setLongClickable(true);
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

            // Set move poster
            mvh.getIconView().getLayoutParams().width = (int)(mWidth / 3);
            mvh.getIconView().getLayoutParams().height = (int)(mWidth / 3);
            // Set movie rate
            TextView rateView =  mvh.getRateView();
            rateView.setText(movie.getRating());

            // Set movie date release
            TextView dateView = mvh.getDateView();
            dateView.setText(movie.getReleaseDate());

            // Set movie overview
            String overView = movie.getOverView();
            mvh.getOverView().setText(overView);
    }

    @Override
    public int getItemCount() {
        return this.movieList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public Filter  getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                constraint = constraint.toString().toLowerCase();
                FilterResults result = new FilterResults();

                if (constraint != null && constraint.toString().length() > 0) {
                    List<Movie> founded = new ArrayList<>();
                    for (Movie item : saveMovieList) {
                        if (item.getMovieTitle().toLowerCase().contains(constraint)) {
                            founded.add(item);
                        }
                    }

                    result.values = founded;
                    result.count = founded.size();
                } else {
                    result.values = saveMovieList;
                    result.count = saveMovieList.size();
                }
                return result;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                movieList = (ArrayList<Movie>) results.values;
                notifyDataSetChanged();
            }
        };
    }


}
