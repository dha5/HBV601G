package com.hbv601.folf.Entities;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;


public class CourseEntity  {

    private String courceName;
    private String coursePars;
    private Location courseLocation;

    public CourseEntity(String name, String par, Location coordinates){
        this.courceName = name;
        this.coursePars = par;
        this.courseLocation = coordinates;
    }

    public double getDistanceFrom(){

    }

}