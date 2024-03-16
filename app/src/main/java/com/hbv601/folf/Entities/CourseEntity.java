package com.hbv601.folf.Entities;
import static androidx.core.content.ContextCompat.getSystemService;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;


public class CourseEntity {

    private String courceName;
    private String coursePars;
    private Location courseLocation;

    public CourseEntity(String name, String par, Location coordinates){
        this.courceName = name;
        this.coursePars = par;
        this.courseLocation = coordinates;
    }

    @SuppressLint("MissingPermission")
    public double getDistanceFrom(Activity activity){
        if (isLocationPermissionGranted(activity)) {
            final Location[] currentGpsLocation = {null};
            final Location[] currentNetworkLocation = {null};
            LocationManager locationManager;
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            boolean hasGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            LocationListener gpslocationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    currentGpsLocation[0] = location;
                }
            };
            LocationListener networklocationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    currentNetworkLocation[0] = location;
                }
            };

            if (hasGPS){
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        5000,
                        0F,
                        gpslocationListener
                );
            }

            if (hasNetwork){
                locationManager.requestLocationUpdates(
                       LocationManager.NETWORK_PROVIDER,
                       5000,
                       0F,
                       networklocationListener
                );
            }

            if(currentGpsLocation[0] != null){
                return currentGpsLocation[0].distanceTo(courseLocation);
            } else if (currentNetworkLocation[0] != null) {
                return currentNetworkLocation[0].distanceTo(courseLocation);
            }

        }
        return 0;
    }


    private boolean isLocationPermissionGranted(Activity activity){
        if (ActivityCompat.checkSelfPermission(
                activity,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                        activity,
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