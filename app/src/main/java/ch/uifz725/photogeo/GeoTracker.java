package ch.uifz725.photogeo;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.Toast;

public class GeoTracker extends Service implements LocationListener {

    private Context context;
    boolean isGeoEnabled =false; //Geo-Status
    boolean isNetworkEnabled =false; //Netzwerk-Status
    boolean isLocation=false; //Verbindung funktioniert
    Location location;
    double latitude; //Breitengrad
    double longitude; //Längengrad

    private static final long MIN_DISTANCE_UPDATE = 10; //10 Meter
    private static final long MIN_TIME_UPDATE = 60000; //1 Minute

    protected LocationManager locationManager;

    public GeoTracker(Context context){
        this.context = context;
        getLocation();
    }

    @SuppressLint("MissingPermission")
    public Location getLocation(){
        try {
            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

            //Geo-Status
            isGeoEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            //Netwerk-Status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            this.isLocation = true;
            if (isNetworkEnabled) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_UPDATE, MIN_DISTANCE_UPDATE, this);
                Log.d("Network", "Network");
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                }
            }

            //Wenn Geo eingeschaltet, get Längen- und Breitengrade von Geo-Services
            if (isGeoEnabled) {
                if (location == null) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME_UPDATE,MIN_DISTANCE_UPDATE, this);
                    Log.d("Geo", "Geo");
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }

            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }


    /**
     * Function to get longitude
     * */
    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {

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
