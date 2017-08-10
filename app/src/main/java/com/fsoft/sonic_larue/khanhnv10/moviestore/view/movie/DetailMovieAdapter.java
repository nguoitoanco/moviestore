package com.fsoft.sonic_larue.khanhnv10.moviestore.view.movie;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Cache;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.fsoft.sonic_larue.khanhnv10.moviestore.R;
import com.fsoft.sonic_larue.khanhnv10.moviestore.model.Cast;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.movie.DetailCallBackListener;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.movie.MovieService;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.network.AppController;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.network.LruBitmapCache;

import java.util.List;

/**
 * Created by KhanhNV10 on 2015/11/23.
 */
public class DetailMovieAdapter extends RecyclerView.Adapter<DetailMovieAdapter.MovieViewHolder> {

    private List<Cast> castList;
    private Context context;
    private DetailCallBackListener detailCallBackListener;

    public class MovieViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private TextView mCastNameView;
        private NetworkImageView mCastProfileView;
        Button btnReminder;

        public MovieViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cast_and_crew_card);
            mCastNameView = (TextView) itemView.findViewById(R.id.cast_and_crew_name);
//            movieIconView = (NetworkImageView) itemView.findViewById(R.id.movie_icon);
            mCastProfileView = (NetworkImageView) itemView.findViewById(R.id.cast_and_crew_avatar);
            btnReminder =(Button) itemView.findViewById(R.id.remind_button);

        }

        public NetworkImageView getCastProfileView() {
            return mCastProfileView;
        }

        public TextView getCastNameView() {
            return mCastNameView;
        }
    }


    public DetailMovieAdapter(List<Cast> casts, Context context, DetailCallBackListener callback) {
        this.castList = casts;
        this.context = context;
        this.detailCallBackListener = callback;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.cast_and_crew_list, parent, false);
        MovieViewHolder movieViewHolder = new MovieViewHolder(view);
        return movieViewHolder;
    }

    @Override
    public void onBindViewHolder(final MovieViewHolder movieViewHolder, final int position) {
            final Cast cast = castList.get(position);

            movieViewHolder.itemView.getViewTreeObserver().addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {

                @Override
                public void onGlobalLayout() {

                    movieViewHolder.getCastNameView().setText(cast.getActorName());
                    int cardViewHeight = movieViewHolder.getCastProfileView().getHeight();
                    movieViewHolder.getCastProfileView().getLayoutParams().width = cardViewHeight;
                    movieViewHolder.getCastProfileView().getLayoutParams().height = cardViewHeight;

                    // Load image
                    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
                    NetworkImageView imageView = movieViewHolder.getCastProfileView();
                    String castProFile = cast.getProFilePath();

                    if (!TextUtils.isEmpty(castProFile)) {
                        imageView.setImageUrl(MovieService.BASE_IMAGE_URL + castProFile, imageLoader);
                    }

                    if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        movieViewHolder.itemView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        movieViewHolder.itemView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }

                }
            });

            movieViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    detailCallBackListener.displayDetailCastCrew(position);
                }
            });
    }

    @Override
    public int getItemCount() {
        return this.castList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
