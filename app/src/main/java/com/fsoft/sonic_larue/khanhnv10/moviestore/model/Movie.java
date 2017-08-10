package com.fsoft.sonic_larue.khanhnv10.moviestore.model;

import android.provider.BaseColumns;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by KhanhNV10 on 2015/11/20.
 */
public class Movie implements Serializable, BaseColumns {

    public static final String IS_FAV = "1";
    public static final String IS_NOT_FAV = "0";
    private static final String SUM_VOTE = "/10.0";

    public static final String MOVIE_TABLE_NAME = "_MOVIE_TABLE";

    public static final String COLUMN_MOVIE_ID = "movie_id";
    public static final String COLUMN_MOVIE_TITLE = "movie_title";
    public static final String COLUMN_MOVIE_POSTER = "movie_poster";
    public static final String COLUMN_MOVIE_RELEASE_DATE = "movie_release_date";
    public static final String COLUMN_MOVIE_VOTE_AVERAGE = "movie_vote_average";
    public static final String COLUMN_MOVIE_FAV = "movie_fav";
    public static final String COLUMN_MOVIE_OVERVIEW = "movie_overview";

    public static final String MOVIE_TABLE_CREATE =
            "CREATE TABLE " + Movie.MOVIE_TABLE_NAME + " (" +
                    Movie.COLUMN_MOVIE_ID + " TEXT PRIMARY KEY, " +
                    Movie.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                    Movie.COLUMN_MOVIE_POSTER + " TEXT NOT NULL, " +
                    Movie.COLUMN_MOVIE_VOTE_AVERAGE + " TEXT NOT NULL, " +
                    Movie.COLUMN_MOVIE_RELEASE_DATE + " TEXT NOT NULL, " +
                    Movie.COLUMN_MOVIE_FAV + " TEXT, " +
                    Movie.COLUMN_MOVIE_OVERVIEW + " TEXT);";

    public static final String MOVIE_TABLE_DELETE =
            "DROP TABLE IF EXISTS " + Movie.MOVIE_TABLE_NAME;

    /** Id of movie */
    @SerializedName("id")
    private String movieId;

    /** Title of movie */
    @SerializedName("title")
    private String movieTitle;

    /* Poster of movie */
    @SerializedName("poster_path")
    private String moviePoster;

    /** Release date of movie */
    @SerializedName("release_date")
    private String releaseDate;

    /** Rate count of movie */
    @SerializedName("vote_count")
    private int rateCount;

    /** Adult movie icon */
    @SerializedName("backdrop_path")
    private String adultMovieIcon;

    /** Favourite movie icon */
//    @SerializedName("poster_path")
    private String favouriteIcon;

    /** OverView of movie */
    @SerializedName("overview")
    private String overView;

    @SerializedName("adult")
    private boolean adult;

    @SerializedName("vote_average")
    private String voteAverage;

    private String movieFav = IS_NOT_FAV;

    private String moviePosterPath;

    private String rating;
    public Movie() {
    }

    public Movie(String movieId, String movieTitle, String moviePoster, String voteAverage, String releaseDate, String movieFav, String overView) {
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.moviePoster = moviePoster;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
        this.movieFav = movieFav;
        this.overView = overView;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public String getMoviePoster() {
        return moviePoster;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public int getRateCount() {
        return rateCount;
    }

    public String getAdultMovieIcon() {
        return adultMovieIcon;
    }

    public String getFavouriteIcon() {
        return favouriteIcon;
    }

    public String getOverView() {
        return overView;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public void setMoviePoster(String moviePoster) {
        this.moviePoster = moviePoster;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setRateCount(int rateCount) {
        this.rateCount = rateCount;
    }

    public void setAdultMovieIcon(String adultMovieIcon) {
        this.adultMovieIcon = adultMovieIcon;
    }

    public void setFavouriteIcon(String favouriteIcon) {
        this.favouriteIcon = favouriteIcon;
    }

    public void setOverView(String overView) {
        this.overView = overView;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getMoviePosterPath() {
        return moviePosterPath;
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public void setMoviePosterPath(String moviePosterPath) {
        this.moviePosterPath = moviePosterPath;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public String getRating() {
        rating = voteAverage + SUM_VOTE;
        return rating;
    }

    public String getMovieFav() {
        return movieFav;
    }

    public void setMovieFav(String movieFav) {
        this.movieFav = movieFav;
    }

    public boolean isMovieFavourite() {
        return (movieFav.equals(IS_FAV));
    }
}
