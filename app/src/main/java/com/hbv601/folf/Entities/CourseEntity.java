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
import androidx.lifecycle.LifecycleOwnerKt;
import androidx.lifecycle.ViewModel;
import com.hbv601.folf.network.FolfApi;
import com.hbv601.folf.network.FolfApiService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import retrofit2.Response;


public class CourseEntity extends ViewModel {

    private String courceName;

    private String description;

    private List<HoleData> holes;
    private int id;
    private List<Integer> coursePars;
    private Location courseLocation;



    public CourseEntity(String name, List<Integer> par, String coordinates, String desc, int id){
        this.courceName = name;
        this.coursePars = par;
        this.id=id;
        this.description = desc;
        String[] parts = coordinates.split(";");
        double lat = Double.parseDouble(parts[0].trim());
        double lon = Double.parseDouble(parts[1].trim());
        Location loc = new Location(name);
        loc.setLatitude(lat);
        loc.setLongitude(lon);
        this.courseLocation = loc;
    }

    public static CourseEntity generateDummy(){
        Location loc = new Location("dummy");
        loc.setLatitude(64.1397116);
        loc.setLongitude(-21.9478740); //hnitin á háskóla íslands
        int[] pars = {3,3,4,4,3};
        CourseEntity dummyCourse = new CourseEntity("dummy", Arrays.stream(pars).boxed().collect(Collectors.toList()), "64.1397116;-21.9478740","test",0);
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

    public List<HoleData> getHoles() {
        return holes;
    }

    public void setHoles(List<HoleData> holes) {
        this.holes = holes;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Integer> getCoursePars() {
        return coursePars;
    }

    public void setCoursePars(List<Integer> coursePars) {
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