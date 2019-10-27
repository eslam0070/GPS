package com.egyeso.gps;


import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import java.util.List;

public class MyLocationProvider {
    private LocationManager locationManager;
    private Location location;
    public static final long MINIMUM_TIME_BETWEEN_UPDATES = 5*1000;
    public static final long MINIMUM_DISTANCE_BETWEEN_UPDATES = 10;


    public MyLocationProvider(Context context) {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        location = null;
    }

    //get location and GPS or not
    public boolean canGetLocation(){
        boolean isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return isGPSEnable || isNetworkEnable;
    }

    @SuppressLint("MissingPermission")
    public Location getCurrentLocation(LocationListener locationListener){
        if (!canGetLocation()){
            location = null;
            return null;
        }
        String provider = LocationManager.GPS_PROVIDER;
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            provider = LocationManager.NETWORK_PROVIDER;
        }
        location = locationManager.getLastKnownLocation(provider);
        if (location == null){
            location = getBestLastKnownLocation();
        }
        if (locationListener != null){
            locationManager.requestLocationUpdates(provider,MINIMUM_TIME_BETWEEN_UPDATES
                    ,MINIMUM_DISTANCE_BETWEEN_UPDATES,locationListener);
        }
        return location;
    }

    @SuppressLint("MissingPermission")
    private Location getBestLastKnownLocation() {
        List<String> providers = locationManager.getAllProviders();
        /*for (int i = 0 ; i<providers.size() ; i++){
            String provider = providers.get(i);
        }*/
        Location bestLocation = null;

        for(String provider : providers){
            Location temp = locationManager.getLastKnownLocation(provider);
            if (temp == null)continue;
            if (bestLocation == null){
                bestLocation = temp;
            }else if (temp.getTime() > bestLocation.getTime()){
                //double check if can apply
            }
        }
        return bestLocation;
    }
}
