package com.egyeso.gps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {

    private static final String SHARED_PREF_NAME = "SHARED_PREF_NAME";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 200;
    Location myLocation = null;
    MyLocationProvider locationProvider;
    //AlertDialog with message and ok
    public AlertDialog showMessage(String message, String postActionName){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setPositiveButton(postActionName, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return builder.show();
    }
    //AlertDialog with message and ok setCancelable true or false(true show chancel)
    public AlertDialog showMessage (String message, String postActionName
            ,DialogInterface.OnClickListener onClickListener,boolean isCancelable){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setPositiveButton(postActionName,onClickListener);
        builder.setCancelable(isCancelable);
        return builder.show();
    }
    //AlertDialog with message and ok and cancel and setCancelable true or false(not show chancel)
    public AlertDialog showMessage (String message, String postActionName
            ,DialogInterface.OnClickListener onPosClick, String negativeText,
                                    DialogInterface.OnClickListener onNegativeClick, boolean isCancelable){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setPositiveButton(postActionName, onPosClick);
        builder.setNegativeButton(negativeText,onNegativeClick);
        builder.setCancelable(isCancelable);
        return builder.show();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (isLocationPermissionGranted()){
            //check permission is access show toast
            Toast.makeText(this, "Permission Allow", Toast.LENGTH_SHORT).show();
        }else {
            //else requestPermission
            requestLocationPermission();
        }
        super.onCreate(savedInstanceState);
    }

    public void getUserLocation(){
        //GPS User
        if (locationProvider == null)
            locationProvider = new MyLocationProvider(this);
        myLocation = locationProvider.getCurrentLocation(new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                myLocation = location;
                drawUserLocation();
                Toast.makeText(BaseActivity.this, location.getLatitude()+" "+ location.getLongitude(), Toast.LENGTH_SHORT).show();
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
        });
    }

    GoogleMap googleMap;
    Marker marker;
    public void drawUserLocation() {
        if (myLocation == null || googleMap == null) return;
        marker = googleMap.addMarker(new MarkerOptions()
        .title("I,m here")
        .position(new LatLng(myLocation.getLatitude(),myLocation.getLongitude()))
        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_name)));
        marker.setPosition(new LatLng(myLocation.getLatitude(),myLocation.getLongitude()));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(),myLocation.getLongitude()),15));

    }

    //Check if permission is granted or not
    public boolean isLocationPermissionGranted(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            return true;
        }
        return false;
    }

    //Request Permission
    public void requestLocationPermission(){
        // Permission is not granted
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            //Show AlertDialog with close this permission
            showMessage("app wants to access location to find nearby cafe",
                    "ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //if click on "ok" show request permission
                            ActivityCompat.requestPermissions((Activity) getApplicationContext(),
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    LOCATION_PERMISSION_REQUEST_CODE);
                        }
                    },true);
        } else {
            //Show AlertDialog for user
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
}
