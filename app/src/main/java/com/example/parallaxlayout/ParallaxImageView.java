package com.example.parallaxlayout;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

import java.util.Timer;
import java.util.TimerTask;

import timber.log.Timber;

public class ParallaxImageView extends AppCompatImageView implements SensorEventListener {
    float[] rotMat = new float[16];
    float[] vals = new float[3];
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private float verticalMultiplier = 1;
    private float horizontalMultiplier = 1;

    private float lastRoll = 0;
    private float lastPitch = 0;

    private int rollOrigin = 0;
    private int pitchOrigin = 0;

    private static final float ROTATION_LIMIT_DEGREES = 70;


    public ParallaxImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        senSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);

        // TODO: PK - This should be scoped to the view's lifecycle
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                int deltaY = (int) ((lastRoll - rollOrigin) * .1);
                rollOrigin += deltaY;
                int deltaX = (int) ((lastPitch - pitchOrigin) * .1);
                pitchOrigin += deltaX;
            }
        }, 1000, 500);
    }

    public ParallaxImageView(Context context) {
        super(context);
    }

    public void setMultipliers(float Vertical, float Horizontal) {
        this.verticalMultiplier = Vertical;
        this.horizontalMultiplier = Horizontal;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;

        if (mySensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {

            // Convert the rotation-vector to a 4x4 matrix.
            SensorManager.getRotationMatrixFromVector(rotMat, event.values);
//            SensorManager.remapCoordinateSystem(rotMat,
//                    SensorManager.AXIS_Y, SensorManager.AXIS_X,
//                    rotMat);

            SensorManager.getOrientation(rotMat, vals);

//            float pitch = (float) Math.toDegrees(Math.atan2(Math.sqrt(rotMat[9]*rotMat[9] + rotMat[10]*rotMat[10]), -rotMat[8]));
            float pitch = (float) Math.toDegrees(Math.atan2(rotMat[9], rotMat[10])); // This is technically the calc for roll
            float roll = (float) Math.toDegrees(vals[2]);
            Timber.d("Pitch: %s, Roll: %s", pitch, roll);


            lastPitch = pitch;
            if (pitch <= ROTATION_LIMIT_DEGREES && pitch >= -ROTATION_LIMIT_DEGREES) {
                lastRoll = roll;
            }
            float coercedPitch = coerceIn(lastPitch, -ROTATION_LIMIT_DEGREES, ROTATION_LIMIT_DEGREES);
            float coercedRoll = coerceIn(lastRoll, -ROTATION_LIMIT_DEGREES, ROTATION_LIMIT_DEGREES);
            int translationY = (int) ((coercedPitch - pitchOrigin) * -this.verticalMultiplier);
            int translationX = (int) (-((coercedRoll - rollOrigin) * this.horizontalMultiplier));
            setTranslationX(translationX);
            setTranslationY(translationY);
        }
    }

    float coerceIn(float value, float min, float max) {
        return Math.min(Math.max(value, min), max);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }

    public void onPause() {
        senSensorManager.unregisterListener(this);
    }

    public void onResume() {
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
    }

}
