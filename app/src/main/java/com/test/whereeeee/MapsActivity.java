package com.test.whereeeee;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MapsActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    GoogleApiClient mGoogleApiClient;
    private LocationManager locationManager;

    static Location mLastLocation;
    static Location mEndLocation = new Location("");
    static String strDistance;
    static String strAzimuth;

    //Button mShowMap;
    String start;
    String destination;
    private Compass compass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        compass = new Compass(this);
        compass.arrowView = (ImageView) findViewById(R.id.main_image_hands);
        // setContentView(R.layout.activity_show_maps);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 99);
        } else {
            // permission has been granted, continue as usual
        }
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
        compass.start();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
        compass.stop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        compass.stop();
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }

    @Override
    protected void onResume() {

        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    0,          // 位置情報取得の時間間隔
                    0,          // 位置情報取得の距離間隔
                    this);      // LocationListener
        }
        super.onResume();
        compass.start();
    }


    // search with location names
    private void findFromTo() {
        //start = "Waterfront Station";
        //destination = "Stanley Park";

        // train:r
        String dir = "r";
        // Car:d
        //String dir = "d";
        // Walking:w
        //String dir = "w";

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        intent.setData(Uri.parse("http://maps.google.com/maps?saddr=" + start + "&daddr=" + destination + "&dirflg=" + dir));
        startActivity(intent);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        calDistanceAndShow();
    }

    public void calDistanceAndShow() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        mEndLocation.setLatitude(UserDetails.latitude2);
        mEndLocation.setLongitude(UserDetails.longitude2);

        TextView textViewDistance = (TextView) findViewById(R.id.distance);

        if (mLastLocation != null) {
            start = mLastLocation.getLatitude() + ", " + mLastLocation.getLongitude();
            destination = mEndLocation.getLatitude() + ", " + mEndLocation.getLongitude();
            double distance = calculateDistance(mLastLocation.getLatitude(), mLastLocation.getLongitude(), UserDetails.latitude2, UserDetails.longitude2, mLastLocation.getAltitude(), 0);
            strDistance = String.format("%1$.0f m", distance);
            textViewDistance.setText(strDistance);
//            Toast.makeText(this, strDistance, Toast.LENGTH_LONG).show();

            //findFromTo();
        } else {
            Log.d("tag", "Not connected");
        }
    }


    public static int getDirection(double latitude1, double longitude1, double latitude2, double longitude2) {
        double lat1 = Math.toRadians(latitude1);
        double lat2 = Math.toRadians(latitude2);
        double lng1 = Math.toRadians(longitude1);
        double lng2 = Math.toRadians(longitude2);
        double Y = Math.sin(lng2 - lng1) * Math.cos(lat2);
        double X = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(lng2 - lng1);
        double deg = Math.toDegrees(Math.atan2(Y, X));
        double angle = (deg + 360) % 360;
        return (int) (Math.abs(angle) + (1 / 7200));
    }

    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2, double el1, double el2) {
        final int R = 6371; // Radius of the earth
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters
        double height = el1 - el2;
        distance = Math.pow(distance, 2) + Math.pow(height, 2);
        return Math.sqrt(distance);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        calDistanceAndShow();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
        switch( status )
        {
            case LocationProvider.AVAILABLE:
                Log.v( "Status", "AVAILABLE" );
                break;
            case LocationProvider.OUT_OF_SERVICE:
                Log.v( "Status", "OUT_OF_SERVICE" );
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                Log.v( "Status", "TEMPORARILY_UNAVAILABLE" );
                break;
        }

        Log.v( "Status", "onStatusChanged" );
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    // change font
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}