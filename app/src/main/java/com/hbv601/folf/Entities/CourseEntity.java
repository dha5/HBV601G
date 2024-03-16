package com.hbv601.folf.Entities;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;


public class CourseEntity extends Context {

    private String courceName;
    private String coursePars;
    private Location courseLocation;

    public CourseEntity(String name, String par, Location coordinates){
        this.courceName = name;
        this.coursePars = par;
        this.courseLocation = coordinates;
    }

    public double getDistanceFrom(Activity activity){
        if (isLocationPermissionGranted(activity)) {
            Location currentLocation = null;
            LocationManager locationManager;
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            boolean hasGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        }
    }


    private boolean isLocationPermissionGranted(Activity activity){
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED) {


            int requestCode = 1001;
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    requestCode
            );
            return false;
        } else {
            return true;
        }
    }
}