package com.example.askforhelp.util;

import android.location.Location;

public class LocationDistanceCalculator {
    public static double findDistance(double latA, double latB, double lonA,
                                  double lonB) {

        Location locationA = new Location("point A");

        locationA.setLatitude(latA);
        locationA.setLongitude(lonA);

        Location locationB = new Location("point B");

        locationB.setLatitude(latB);
        locationB.setLongitude(lonB);

        return locationA.distanceTo(locationB);
    }
}
