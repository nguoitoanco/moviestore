package com.fsoft.sonic_larue.khanhnv10.moviestore.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by KhanhNV10 on 2015/12/17.
 */
public class Cinema implements Serializable {

    /** Place Id */
    @SerializedName("place_id")
    private String id;

    /** Cinema Name */
    @SerializedName("name")
    private String name;

    /** Vicinity */
    @SerializedName("vicinity")
    private String vicinity;

    /** Phone Number */
    @SerializedName("international_phone_number")
    private String phoneNumber;

    @SerializedName("geometry")
    private Geometry geometry;

    @SerializedName("formatted_address")
    private String address;

    public Cinema(String id, String name, String vicinity, String phoneNumber, Geometry geometry, String address) {
        this.id = id;
        this.name = name;
        this.vicinity = vicinity;
        this.phoneNumber = phoneNumber;
        this.geometry = geometry;
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getVicinity() {
        return vicinity;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
