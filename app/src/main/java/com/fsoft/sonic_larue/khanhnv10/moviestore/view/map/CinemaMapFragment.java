package com.fsoft.sonic_larue.khanhnv10.moviestore.view.map;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Cache;
import com.fsoft.sonic_larue.khanhnv10.moviestore.R;
import com.fsoft.sonic_larue.khanhnv10.moviestore.model.Cinema;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.map.MapService;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.movie.MovieService;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.movie.ServiceCallBackListener;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.network.AppController;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.util.StringUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CinemaMapFragment extends Fragment implements OnMapReadyCallback, LocationListener {

    protected LocationManager locationManager;
    protected LocationListener locationListener;
    private Location location;
    List<Cinema> cinemaList = null;
    private GoogleMap googleMap;

    public static CinemaMapFragment newInstance() {
        // Required empty public constructor
        CinemaMapFragment cinemaMapFragment = new CinemaMapFragment();
        return cinemaMapFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cinema_map, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        locationManager = (LocationManager) getContext()
                .getSystemService(Context.LOCATION_SERVICE);

        // getting GPS status
        boolean isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        // getting network status
        boolean isNetworkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (isGPSEnabled || isNetworkEnabled) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (getContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && getContext().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60, 1000, this);
                }
            }

            if (isNetworkEnabled) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                if (locationManager != null) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60, 1000, this);
                }
            }

            cinemaList = new ArrayList<>();
        }


        return view;
    }

    @Override
    public void onMapReady(GoogleMap gMap) {
        googleMap = gMap;
        double latitude = 0;
        double longitude = 0;
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            loadData(latitude, longitude);

        }


    }

    private void loadData(double la, double ln) {
        String conUrl = MapService.PLACES_SEARCH_URL;
        conUrl = conUrl.replace("{lat,lng}", la + "," + ln);
        Log.d("loadData", conUrl);
        Cache cache = AppController.getInstance().getRequestQueue().getCache();
        Cache.Entry entry = cache.get(conUrl);

        // Cached response doesn't exists. Make network call here
        MapService.makeCacheData(conUrl, new ServiceCallBackListener() {
            @Override
            public void onResult(Object data) {
                try {
                    displayOnMap(data.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError() {
            }
        });

    }

    public void displayOnMap(String strJson) throws JSONException {
        JSONObject jsonRootObject = new JSONObject(strJson);
        String jsonResult = jsonRootObject.getJSONArray("results").toString();
        Gson googleJson = new Gson();
        Type listType = new TypeToken<List<Cinema>>() {
        }.getType();
        Log.d("parsJsonToObject", strJson);
        final List<Cinema> cinemas = googleJson.fromJson(jsonResult, listType);


        if (cinemas != null && cinemas.size() > 0) {
            final List<LatLng> latLngList = new ArrayList<>();
            for (final Cinema c : cinemas) {
                double lat = c.getGeometry().getLocation().getLat();
                double lng = c.getGeometry().getLocation().getLng();
                LatLng latLng = new LatLng(lat, lng);
                latLngList.add(latLng);

                final MarkerOptions maker = new MarkerOptions().position(latLng);
                googleMap.addMarker(maker);
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));


            }

            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    int index = latLngList.indexOf(marker.getPosition());
                    loadMoreDetail(cinemas.get(index));
                    Log.d("MarkerClickListener", "onMarkerClick");
                    return false;
                }
            });

        }

    }

    private void loadMoreDetail(final Cinema cinema) {
        String conUrl = MapService.CONTACT_SEARCH_URL;
        conUrl = conUrl.replace("{placeid}", cinema.getId());
        Log.d("loadData", conUrl);
        Cache cache = AppController.getInstance().getRequestQueue().getCache();
        Cache.Entry entry = cache.get(conUrl);

        // Cached response doesn't exists. Make network call here
        MapService.makeCacheData(conUrl, new ServiceCallBackListener() {
            @Override
            public void onResult(Object data) {
                try {
                    JSONObject jsonRootObject = new JSONObject(data.toString());
                    String jsonResult = jsonRootObject.getJSONObject("result").toString();
                    Log.d("jsonRootObject", jsonResult);
                    Gson googleJson = new Gson();
                    Type type = new TypeToken<Cinema>() {
                    }.getType();

                    Cinema c = googleJson.fromJson(jsonResult, type);
                    if (c != null) {
                        cinema.setAddress(c.getAddress());
                        cinema.setPhoneNumber(c.getPhoneNumber());
                    }

                    showAlertDialog(cinema);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError() {
            }
        });
    }

    private void showAlertDialog(Cinema c) {


        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.map_dialog, null);
        TextView nameView = (TextView) view.findViewById(R.id.cinema_name);
        TextView addressView = (TextView) view.findViewById(R.id.cinema_address);
        final TextView phoneView = (TextView) view.findViewById(R.id.cinema_phone);
        nameView.setText(c.getName());
        addressView.setText(c.getAddress());
        phoneView.setText(c.getPhoneNumber());

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);
        builder.setNegativeButton(getString(R.string.do_cancel), null);

        if (!StringUtil.isEmpty(c.getPhoneNumber())) {
            builder.setPositiveButton(getString(R.string.do_call), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:+" + phoneView.getText().toString().trim()));

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (getActivity().checkSelfPermission(
                                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            getActivity().startActivity(callIntent);
                        }
                    }
                }
            });
        }

        AlertDialog alert = builder.create();
        alert.show();

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
