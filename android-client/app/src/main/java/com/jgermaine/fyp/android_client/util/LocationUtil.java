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
    public static final int START_ZOOM_LEVEL = 15;
    public static final int COMPLETE_ZOOM_LEVEL = 17;
    public static final int CUSTOM_ZOOM_TIME = 1000;

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


    public static Marker getMarker(GoogleMap map, Marker marker, Location current, String title, String desc) {

        removeMarker(marker);

        marker = map.addMarker(
                new MarkerOptions()
                        .position(getCoordinates(current))
                        .title(title)
                        .snippet(desc));
        marker.showInfoWindow();
        return marker;
    }

    public static void removeMarker(Marker marker) {
        if (marker != null) {
            marker.remove();
        }
    }
}
