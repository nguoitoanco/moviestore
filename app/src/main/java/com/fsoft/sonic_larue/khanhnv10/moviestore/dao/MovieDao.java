package com.fsoft.sonic_larue.khanhnv10.moviestore.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.fsoft.sonic_larue.khanhnv10.moviestore.model.Movie;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.manager.SharedPreferencesManager;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by KhanhNV10 on 2015/11/16.
 */
public class MovieDao {

    private static String TAG = MovieDao.class.getSimpleName();

    private Context context;
    private String profileId;

    String[] selectAll = {
            Movie.COLUMN_MOVIE_ID,
            Movie.COLUMN_MOVIE_TITLE,
            Movie.COLUMN_MOVIE_POSTER,
            Movie.COLUMN_MOVIE_VOTE_AVERAGE,
            Movie.COLUMN_MOVIE_RELEASE_DATE,
            Movie.COLUMN_MOVIE_FAV,
            Movie.COLUMN_MOVIE_OVERVIEW,
    };

    public MovieDao(Context context, String profileId) {
        this.context = context;
        this.profileId = profileId;
    }

    public MovieDao(Context context) {
        this.context = context;
        this.profileId = SharedPreferencesManager
                .loadPreference(context, "profileId", StringUtil.DEFAULT);
    }

    public long insert(Movie movie) {
        DBConfig dbConfig = DBConfig.getInstance(context, profileId);
        SQLiteDatabase db = dbConfig.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Movie.COLUMN_MOVIE_ID, movie.getMovieId());
        contentValues.put(Movie.COLUMN_MOVIE_TITLE, movie.getMovieTitle());
        contentValues.put(Movie.COLUMN_MOVIE_POSTER, movie.getMoviePoster());
        contentValues.put(Movie.COLUMN_MOVIE_VOTE_AVERAGE, movie.getVoteAverage());
        contentValues.put(Movie.COLUMN_MOVIE_RELEASE_DATE, movie.getReleaseDate());
        contentValues.put(Movie.COLUMN_MOVIE_FAV, movie.getMovieFav());
        contentValues.put(Movie.COLUMN_MOVIE_OVERVIEW, movie.getOverView());

        long rowId = db.replace(Movie.MOVIE_TABLE_NAME, null, contentValues);
        return rowId;
    }

    public Movie getMovieById(String movieId) {
        DBConfig dbConfig = DBConfig.getInstance(context, profileId);
        SQLiteDatabase db = dbConfig.getReadableDatabase();

        Cursor cursor = db.query(Movie.MOVIE_TABLE_NAME, selectAll, Movie.COLUMN_MOVIE_ID + "=" + movieId, null, null, null, null);

        Log.d(TAG, "read successfully:" + cursor.getCount());
        Movie movie = new Movie();
        if (cursor != null) {
            if (cursor.moveToNext()) {
                movie = new Movie(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6));
                Log.d(TAG, "read successfully:" + cursor.getString(1));
            }
        }

        cursor.close();
        return movie;
    }

    public List<Movie> getAllMovies() {
        DBConfig dbConfig = DBConfig.getInstance(context, profileId);
        SQLiteDatabase db = dbConfig.getReadableDatabase();



        Cursor cursor = db.query(Movie.MOVIE_TABLE_NAME, selectAll, null, null, null, null, null);

        Log.d(TAG, "read successfully:" + cursor.getCount());
        List<Movie> movies = new ArrayList<Movie>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                movies.add(
                    new Movie(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6))
                );
            }
        }

        cursor.close();

        return movies;
    }


    public List<Movie> getFavMovies() {
        DBConfig dbConfig = DBConfig.getInstance(context, profileId);
        SQLiteDatabase db = dbConfig.getReadableDatabase();

        Cursor cursor = db.query(Movie.MOVIE_TABLE_NAME, selectAll, Movie.COLUMN_MOVIE_FAV + "=" + Movie.IS_FAV, null, null, null, null);

        Log.d(TAG, "read successfully:" + cursor.getCount());
        List<Movie> movies = new ArrayList<Movie>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                movies.add(
                        new Movie(
                                cursor.getString(0),
                                cursor.getString(1),
                                cursor.getString(2),
                                cursor.getString(3),
                                cursor.getString(4),
                                cursor.getString(5),
                                cursor.getString(6))
                );
            }
        }

        cursor.close();
        return movies;
    }

    public List<String> getFavMovieIds() {
        DBConfig dbConfig = DBConfig.getInstance(context, profileId);
        SQLiteDatabase db = dbConfig.getReadableDatabase();

        String[] projection = {
                Movie.COLUMN_MOVIE_ID
        };

        Cursor cursor = db.query(Movie.MOVIE_TABLE_NAME, projection, Movie.COLUMN_MOVIE_FAV + "=" + Movie.IS_FAV, null, null, null, null);

        List<String> movieIds = new ArrayList<String>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                movieIds.add(cursor.getString(0));
            }
        }

        cursor.close();
        return movieIds;

    }

    public int countFavMovie() {
        DBConfig dbConfig = DBConfig.getInstance(context, profileId);
        SQLiteDatabase db = dbConfig.getReadableDatabase();

        String[] projection = {
                Movie.COLUMN_MOVIE_ID
        };

        Cursor cursor = db.query(Movie.MOVIE_TABLE_NAME, projection, Movie.COLUMN_MOVIE_FAV + "=" + Movie.IS_FAV, null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public int updateMovie(Movie movie) {
        DBConfig dbConfig = DBConfig.getInstance(context, profileId);
        SQLiteDatabase db = dbConfig.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Movie.COLUMN_MOVIE_FAV, "0");

        // updating row
        int record = db.update(Movie.MOVIE_TABLE_NAME, contentValues, Movie.COLUMN_MOVIE_ID + " = ? ",
                new String[]{movie.getMovieId()});
        return record;
    }
}
