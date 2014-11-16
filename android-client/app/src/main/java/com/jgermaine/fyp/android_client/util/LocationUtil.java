package com.jgermaine.fyp.android_client.util;

import android.location.Location;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by jason on 16/11/14.
 */
public final class LocationUtil {
    public static final int INTERVAL = 5000;
    public static final int ZOOM_LEVEL = 15;

    public static boolean isLocationEquals(Location prev, Location current) {
        boolean isEqual = false;
        if (prev != null && (prev.getLatitude() == current.getLatitude()) && (prev.getLongitude() == current.getLongitude())) {
            isEqual = true;
        }
        return isEqual;
    }

    public static LatLng getCoordinates(Location location) {
        return new LatLng(location.getLatitude(), location.getLongitude());
    }


    public static Marker getMarker(GoogleMap map, Marker marker, Location current) {

        if (marker != null) {
            marker.remove();
        }
        marker = map.addMarker(
                new MarkerOptions()
                        .position(getCoordinates(current))
                        .title("PotHole")
                        .snippet("Pothole on the road"));
        marker.showInfoWindow();
        return marker;
    }
}
