package com.fsoft.sonic_larue.khanhnv10.moviestore.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by KhanhNV10 on 2015/12/17.
 */
public class CinemaLocation {

    @SerializedName("lat")
    private double lat;

    @SerializedName("lng")
    private double lng;

    public CinemaLocation(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
