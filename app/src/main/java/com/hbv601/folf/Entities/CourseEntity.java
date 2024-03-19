package com.hbv601.folf.Entities;
import static androidx.core.content.ContextCompat.getSystemService;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class CourseEntity {

    private String courceName;
    private List<Integer> coursePars;
    private Location courseLocation;



    public CourseEntity(String name, List<Integer> par, Location coordinates){
        this.courceName = name;
        this.coursePars = par;
        this.courseLocation = coordinates;
    }

    public static CourseEntity generateDummy(){
        Location loc = new Location("dummy");
        loc.setLatitude(64.1397116);
        loc.setLongitude(-21.9478740); //hnitin á háskóla íslands
        int[] pars = {3,3,4,4,3};
        CourseEntity dummyCourse = new CourseEntity("dummy", Arrays.stream(pars).boxed().collect(Collectors.toList()), loc);
        return dummyCourse;
    }

    /**
     * Skilar fjarlægð frá brautinni í metrum
     * @param activity activityið sem er í keyrslu
     * @return fjarlægð frá vellinum í metrum
     */
    @SuppressLint("MissingPermission")
    public int getDistanceFrom(Activity activity){
        if (isLocationPermissionGranted(activity)) {
            final Location[] currentGpsLocation = {null};
            final Location[] currentNetworkLocation = {null};
            LocationManager locationManager;
            locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
            assert locationManager != null;
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

            currentGpsLocation[0] = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            currentNetworkLocation[0] = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Log.d("currentGPSlocation",currentGpsLocation[0].toString());
            Log.d("currentNetworklocation",currentNetworkLocation[0].toString());

            if(currentGpsLocation[0] != null){

                return (int) currentGpsLocation[0].distanceTo(courseLocation);
            } else if (currentNetworkLocation[0] != null) {

                return (int)currentNetworkLocation[0].distanceTo(courseLocation);
            }

        }
        Log.d("Kemst ekki í fall","cant make it");
        return 0;
    }

    public int[] getCoursePars() {
        return coursePars;
    }

    public void setCoursePars(int[] coursePars) {
        this.coursePars = coursePars;
    }

    public String getCourceName() {
        return courceName;
    }

    public void setCourceName(String courceName) {
        this.courceName = courceName;
    }

    public Location getCourseLocation() {
        return courseLocation;
    }

    public void setCourseLocation(Location courseLocation) {
        this.courseLocation = courseLocation;
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