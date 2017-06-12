package com.test.whereeeee;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

public class Compass implements SensorEventListener {
//	private static final String TAG = "Compass";
//    private Location location;
//    private Location target;
//	private SensorManager sensorManager;
//    private float currentDegree = 0f;
//
//	// compass arrow to rotate
//	public ImageView arrowView = null;
//
//	public Compass(Context context) {
//		sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
//        location = MapsActivity.mLastLocation;
//        target = MapsActivity.mEndLocation;
//	}
//
//	public void start() {
//		sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
//                SensorManager.SENSOR_DELAY_GAME);
//	}
//
//	public void stop() {
//        sensorManager.unregisterListener(this);
//	}
//
//	@Override
//	public void onSensorChanged(SensorEvent event) {
//        GeomagneticField geoField = new GeomagneticField(
//                (float) target.getLatitude(),
//                (float) target.getLongitude(),
//                (float) target.getAltitude(),
//                System.currentTimeMillis());
//
//        // get the angle around the z-axis rotated
//        float degree = Math.round(event.values[0]);
//        degree += geoField.getDeclination();
//
//        if(location != null && target != null){
//            float bearing = location.bearingTo(target);
//            degree = (bearing - degree) * -1;
//            degree = normalizeDegree(degree);
//        }
//
//        // create a rotation animation (reverse turn degree degrees)
//        RotateAnimation ra = new RotateAnimation(
//                currentDegree,
//                -degree,
//                Animation.RELATIVE_TO_SELF, 0.5f,
//                Animation.RELATIVE_TO_SELF, 0.5f);
//
//        // how long the animation will take place
//        ra.setDuration(210);
//
//        // set the animation after the end of the reservation status
//        ra.setFillAfter(true);
//
//        // Start the animation
//        arrowView.startAnimation(ra);
//        currentDegree = -degree;
//
//	}
//
//    private float normalizeDegree(float value) {
//        if (value >= 0.0f && value <= 180.0f) {
//            return value;
//        } else {
//            return 180 + (180 + value);
//        }
//    }
//
//	@Override
//	public void onAccuracyChanged(Sensor sensor, int accuracy) {
//	}
	//*************************************************************************

    private static final String TAG = "Compass";

    private SensorManager sensorManager;
    private Sensor gsensor;
    private Sensor msensor;
    private float[] mGravity = new float[3];
    private float[] mGeomagnetic = new float[3];
    private float azimuth = UserDetails.latitude;
    private float currectAzimuth = 0;

    // compass arrow to rotate
    public ImageView arrowView = null;

    public Compass(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        gsensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        msensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    public void start() {
        sensorManager.registerListener(this, gsensor,
                SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, msensor,
                SensorManager.SENSOR_DELAY_GAME);
    }

    public void stop() {
        sensorManager.unregisterListener(this);
    }

    private void adjustArrow() {
        if (arrowView == null) {
            Log.i(TAG, "arrow view is not set");
            return;
        }

        Log.i(TAG, "will set rotation from " + currectAzimuth + " to " + azimuth);

        Animation an = new RotateAnimation(-currectAzimuth, -azimuth, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        currectAzimuth = azimuth;

        an.setDuration(500);
        an.setRepeatCount(0);
        an.setFillAfter(true);

        arrowView.startAnimation(an);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        final float alpha = 0.97f;


//        GeomagneticField geoField = new GeomagneticField(
//                (float) target.getLatitude(),
//                (float) target.getLongitude(),
//                (float) target.getAltitude(),
//                System.currentTimeMillis());
//
//        // get the angle around the z-axis rotated
//        float degree = Math.round(event.values[0]);
//        degree += geoField.getDeclination();

        synchronized (this) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

                mGravity[0] = alpha * mGravity[0] + (1 - alpha) * event.values[0];
                mGravity[1] = alpha * mGravity[1] + (1 - alpha) * event.values[1];
                mGravity[2] = alpha * mGravity[2] + (1 - alpha) * event.values[2];
                // mGravity = event.values;
                // Log.e(TAG, Float.toString(mGravity[0]));
            }

            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                // mGeomagnetic = event.values;

                mGeomagnetic[0] = alpha * mGeomagnetic[0] + (1 - alpha) * event.values[0];
                mGeomagnetic[1] = alpha * mGeomagnetic[1] + (1 - alpha) * event.values[1];
                mGeomagnetic[2] = alpha * mGeomagnetic[2] + (1 - alpha) * event.values[2];
                // Log.e(TAG, Float.toString(event.values[0]));
            }

            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                // Log.d(TAG, "azimuth (rad): " + azimuth);
                azimuth = (float) Math.toDegrees(orientation[0]); // orientation
                azimuth = (azimuth + 360) % 360;
                // Log.d(TAG, "azimuth (deg): " + azimuth);
                adjustArrow();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
