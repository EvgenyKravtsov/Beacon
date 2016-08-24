package kgk.beacon.monitoring.presentation.model;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import kgk.beacon.R;
import kgk.beacon.monitoring.domain.model.routereport.ParkingEvent;
import kgk.beacon.monitoring.domain.model.routereport.RouteReportEvent;
import kgk.beacon.util.AppController;
import kgk.beacon.util.ImageProcessor;

public class ParkingEventMapObject extends RouteReportMapObject {

    private final GoogleMap map;
    private final RouteReportEvent event;
    private Marker parkingMarker;
    private ValueAnimator fadeOut;

    ////

    public ParkingEventMapObject(GoogleMap map, RouteReportEvent event) {
        this.map = map;
        this.event = event;

        fadeOut = prepareFadeOutAnimator();
    }

    ////

    public void startFadeOut() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            fadeOut.start();
        }
    }

    public void stopFadeOut() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            fadeOut.cancel();
            parkingMarker.setAlpha(1.0f);
        }
    }

    ////

    @Override
    public RouteReportEvent getEvent() {
        return event;
    }

    @Override
    public void draw() {
        ParkingEvent event = (ParkingEvent) this.event;
        parkingMarker = map.addMarker(generateParkingMarker(
                event.getLatitude(),
                event.getLongitude()));
    }

    @Override
    public void clear() {
        parkingMarker.remove();
    }

    ////

    private BitmapDescriptor generateParkingMarkerIcon() {
        Context context = AppController.getInstance();
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater
                .inflate(R.layout.map_stop_marker, null);

        int width = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                15,
                context.getResources().getDisplayMetrics());
        int height = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                15,
                context.getResources().getDisplayMetrics());

        Bitmap markerBitmap = ImageProcessor.bitmapFromView(layout, width, height);
        return BitmapDescriptorFactory.fromBitmap(markerBitmap);
    }

    private MarkerOptions generateParkingMarker(double latitude, double longitude) {
        return new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .icon(generateParkingMarkerIcon())
                .anchor(0.5f, 0.5f);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private ValueAnimator prepareFadeOutAnimator() {
        ValueAnimator animator = ValueAnimator.ofFloat(0.2f, 1f);
        animator.setDuration(800);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setRepeatCount(ValueAnimator.INFINITE);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if (android.os.Build.VERSION.SDK_INT >=
                        android.os.Build.VERSION_CODES.HONEYCOMB_MR1) {

                    float value = valueAnimator.getAnimatedFraction();
                    parkingMarker.setAlpha(value);
                }

            }
        });

        return animator;
    }
}
