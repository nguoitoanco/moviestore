package com.fsoft.sonic_larue.khanhnv10.moviestore.service.movie;

import android.app.ProgressDialog;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fsoft.sonic_larue.khanhnv10.moviestore.model.Cast;
import com.fsoft.sonic_larue.khanhnv10.moviestore.model.Movie;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.network.AppController;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by KhanhNV10 on 2015/11/23.
 */
public class MovieService {

    public enum Category {
        POPULAR(0, URL_JSON_MOVIE_POPULAR),
        TOP_RATE(1, URL_JSON_MOVIE_TOP_RATE),
        UPCOMING(2, URL_JSON_MOVIE_UP_COMING),
        NOW_PLAYING(3, URL_JSON_MOVIE_NOW_PLAYING);

        private int index;
        private String url;
        Category(int index, String url) {
            this.index = index;
            this.url = url;
        }

        public String getUrl() {
            return url;
        }

        private int getIndex() {
            return index;
        }

        public static String getUrlFromIndex(int index) {
            if (Category.POPULAR.getIndex() == index){
                return Category.POPULAR.getUrl();
            } else if (Category.TOP_RATE.getIndex() == index){
                return Category.TOP_RATE.getUrl();
            } else if (Category.UPCOMING.getIndex() == index){
                return Category.UPCOMING.getUrl();
            } else if (Category.NOW_PLAYING.getIndex() == index){
                return Category.NOW_PLAYING.getUrl();
            }
            return Category.POPULAR.getUrl();
        }
    }

    public static final String URL_JSON_MOVIE_POPULAR =
            "http://api.themoviedb.org/3/movie/popular?api_key=e7631ffcb8e766993e5ec0c1f4245f93&page=";

    public static final String URL_JSON_MOVIE_TOP_RATE =
            "http://api.themoviedb.org/3/movie/top_rated?api_key=e7631ffcb8e766993e5ec0c1f4245f93&page=";

    public static final String URL_JSON_MOVIE_UP_COMING =
            "http://api.themoviedb.org/3/movie/upcoming?api_key=e7631ffcb8e766993e5ec0c1f4245f93&page=";

    public static final String URL_JSON_MOVIE_NOW_PLAYING =
            "http://api.themoviedb.org/3/movie/now_playing?api_key=e7631ffcb8e766993e5ec0c1f4245f93&page=";

    public static final String URL_JSON_DETAIL_CASTS =
            "http://api.themoviedb.org/3/person/{castId}?api_key=e7631ffcb8e766993e5ec0c1f4245f93";


    public static final String URL_JSON_CASTS =
            "http://api.themoviedb.org/3/movie/{movieId}/credits?api_key=e7631ffcb8e766993e5ec0c1f4245f93";

    public static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/w500";

    public static List<Movie> movieFilter(List<Movie> movies, int rate, int year) {
        List<Movie> executeMovies = new ArrayList<>();
        for (Movie movie : movies) {
            String strRate = movie.getRating().substring(0, 1);
            int mRate = Integer.parseInt(strRate);
            String strYear = movie.getReleaseDate().substring(0, 4);
            int mYear = Integer.parseInt(strYear);

            if (mRate >= rate && mYear >= year) {
                executeMovies.add(movie);
            }
        }
        return executeMovies;
    }

    public static void makeCacheData(String conUrl, final ServiceCallBackListener callback) {
        setDefaultAuthen();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                conUrl, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                callback.onResult(response);

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, conUrl);
    }

    public static List<Movie> parsJsonToMovies(String strJson) throws JSONException{
        JSONObject jsonRootObject = new JSONObject(strJson);
        String jsonResult = jsonRootObject.getJSONArray("results").toString();
        Gson googleJson = new Gson();
        Type listType = new TypeToken<List<Movie>>() {}.getType();

        List<Movie> movies = googleJson.fromJson(jsonResult, listType);
        if (movies == null) {
            return Collections.EMPTY_LIST;
        }
        return movies;
    }

    public static List<Cast> parsJsonToCasts(String strJson) throws JSONException{
        JSONObject jsonRootObject = new JSONObject(strJson);
        String jsonResult = jsonRootObject.getJSONArray("cast").toString();
        Gson googleJson = new Gson();
        Type listType = new TypeToken<List<Cast>>() {}.getType();

        List<Cast> casts = googleJson.fromJson(jsonResult, listType);
        if (casts == null) {
            return Collections.EMPTY_LIST;
        }
        return casts;
    }

    public static Cast parsJsonToDetailActor(String strJson){

        JSONObject jsonRootObject = null;
        Cast actor = new Cast();
        try {
            jsonRootObject = new JSONObject(strJson);
            String jsonResult = jsonRootObject.toString();
            Gson googleJson = new Gson();
            Type type = new TypeToken<Cast>() {}.getType();

            actor = googleJson.fromJson(jsonResult, type);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return actor;
    }
    private static void setDefaultAuthen() {
        Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("KhanhNV10", "Kmmynmmym2015".toCharArray());
            }
        });
    }

    public static void clearCache() {
        AppController.getInstance().getRequestQueue().getCache().clear();
    }
}
