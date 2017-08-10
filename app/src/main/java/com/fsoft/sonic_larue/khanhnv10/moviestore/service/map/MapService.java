package com.fsoft.sonic_larue.khanhnv10.moviestore.service.map;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fsoft.sonic_larue.khanhnv10.moviestore.model.Cinema;
import com.fsoft.sonic_larue.khanhnv10.moviestore.model.Movie;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.movie.ServiceCallBackListener;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.network.AppController;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

/**
 * Created by KhanhNV10 on 2015/12/17.
 */
public class MapService {
    private static String PLACE_BASE_URL = "https://maps.googleapis.com/maps/api/place/";
    private static String KEY = "&key=AIzaSyCkg3TE9JsvD6QgDVf_bkcdoXxU1wvJIko";
    public static final String PLACES_SEARCH_URL
            = PLACE_BASE_URL
            + "nearbysearch/json?location={lat,lng}&radius=10000&name=cinema"
            + KEY;

    public static final String CONTACT_SEARCH_URL = PLACE_BASE_URL
            + "details/json?placeid={placeid}"
            + KEY;

    public static void makeCacheData(String conUrl, final ServiceCallBackListener callback) {
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

}
