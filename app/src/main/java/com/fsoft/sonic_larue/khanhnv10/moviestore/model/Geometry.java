package com.fsoft.sonic_larue.khanhnv10.moviestore.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by KhanhNV10 on 2015/12/17.
 */
public class Geometry {

    @SerializedName("location")
    private CinemaLocation location;

    public Geometry(CinemaLocation location) {
        this.location = location;
    }

    public CinemaLocation getLocation() {
        return location;
    }

    public void setLocation(CinemaLocation location) {
        this.location = location;
    }
}
