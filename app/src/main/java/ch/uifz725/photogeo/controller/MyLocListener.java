package ch.uifz725.photogeo.controller;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

/**
 * Die Klasse stellt die aktuelle Location in Längen- und Breitengrad als Double zur Verfügung
 * Created by eMZetta March 2019.
 */


public class MyLocListener implements LocationListener {

    Double langitude; //Längengrad
    Double longitude; //Breitengrad

    public Double getLangitude() {
        return langitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null){
            langitude = location.getLatitude();
            longitude = location.getLongitude();
        }
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
}
