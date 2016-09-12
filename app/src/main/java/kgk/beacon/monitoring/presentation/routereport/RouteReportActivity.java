package kgk.beacon.monitoring.presentation.routereport;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.*;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.maps.MapView;

import butterknife.Bind;
import butterknife.BindDrawable;
import butterknife.ButterKnife;
import kgk.beacon.R;
import kgk.beacon.monitoring.domain.model.routereport.RouteReport;
import kgk.beacon.monitoring.domain.model.routereport.RouteReportEventType;
import kgk.beacon.util.ImageProcessor;

import static kgk.beacon.monitoring.presentation.routereport.RouteReportContract.Map;
import static kgk.beacon.monitoring.presentation.routereport.RouteReportContract.Presenter;
import static kgk.beacon.monitoring.presentation.routereport.RouteReportContract.View;

public class RouteReportActivity extends AppCompatActivity
        implements View {

    private static final String EXTRA_ROUTE_REPORT = "extra_route_report";

    private Presenter presenter;

    ////

    @Bind(R.id.route_report_google_map_view)
    MapView googleMapView;
    @Bind(R.id.route_report_mark)
    TextView markTextView;
    @Bind(R.id.route_report_speed)
    TextView speedTextView;
    @Bind(R.id.route_report_satellites)
    TextView satellitesTextView;
    @Bind(R.id.route_report_gsm_level)
    ImageView gsmLevelImageView;
    @Bind(R.id.route_report_event_description)
    TextView eventDescriptionTextView;
    @Bind(R.id.route_report_timeline)
    SeekBar timelineSeekBar;

    @BindDrawable(R.drawable.monitoring_gsm_level_0)
    Drawable gsmLevel0Drawable;
    @BindDrawable(R.drawable.monitoring_gsm_level_1)
    Drawable gsmLevel1Drawable;
    @BindDrawable(R.drawable.monitoring_gsm_level_2)
    Drawable gsmLevel2Drawable;
    @BindDrawable(R.drawable.monitoring_gsm_level_3)
    Drawable gsmLevel3Drawable;
    @BindDrawable(R.drawable.monitoring_gsm_level_4)
    Drawable gsmLevel4Drawable;
    @BindDrawable(R.drawable.monitoring_gsm_level_5)
    Drawable gsmLevel5Drawable;

    ////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_report_mvp);
        ButterKnife.bind(this);
        prepareGoogleMap(savedInstanceState);
        prepareTimeline();
        attachPresenter();
        presenter.onCreateView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    ////

    @Override
    public void displayMark(String mark) {
        markTextView.setText(mark);
    }

    @Override
    public void displaySpeed(String speed) {
        speedTextView.setText(String.format(
                "%s %s",
                speed,
                getString(R.string.monitoring_route_report_speed_measure)));
    }

    @Override
    public void displaySatellites(String satellites) {
        satellitesTextView.setText(String.format(
                "%s - %s",
                getString(R.string.monitoring_route_report_satellites),
                satellites));
    }

    @Override
    public void displayGsmLevel(int gsmLevel) {
        if (gsmLevel > 0 && gsmLevel <= 6) gsmLevelImageView.setImageDrawable(gsmLevel1Drawable);
        if (gsmLevel > 6 && gsmLevel <= 12) gsmLevelImageView.setImageDrawable(gsmLevel2Drawable);
        if (gsmLevel > 12 && gsmLevel <= 18) gsmLevelImageView.setImageDrawable(gsmLevel3Drawable);
        if (gsmLevel > 18 && gsmLevel <= 24) gsmLevelImageView.setImageDrawable(gsmLevel4Drawable);
        if (gsmLevel > 24) gsmLevelImageView.setImageDrawable(gsmLevel5Drawable);
        if (gsmLevel == 0 || gsmLevel == 99) gsmLevelImageView.setImageDrawable(gsmLevel0Drawable);
    }

    @Override
    public void displayEventDescription(RouteReportEventType eventType, String eventTime) {
        String eventTypeString = "";
        switch (eventType) {
            case Parking: eventTypeString = getString(R.string.monitoring_route_report_parking); break;
            case Moving: eventTypeString = getString(R.string.monitoring_route_report_moving); break;
        }

        eventDescriptionTextView.setText(String.format("%s  %s", eventTypeString, eventTime));
    }

    ////

    private void prepareGoogleMap(Bundle savedInstanceState) {
        googleMapView.onCreate(savedInstanceState);
        googleMapView.onResume();
    }

    private void prepareTimeline() {
        timelineSeekBar.setThumbOffset(36);
        timelineSeekBar.setThumb(prepareTimelineThumbDrawable());
        timelineSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void attachPresenter() {
        Map map = new RouteReportGoogleMap(googleMapView);

        Intent startingIntent = getIntent();
        RouteReport routeReport = (RouteReport) startingIntent.getSerializableExtra(EXTRA_ROUTE_REPORT);

        presenter = new RouteReportPresenter(map, routeReport);
        presenter.attachView(this);

        map.setPresenter(presenter);
    }

    private Drawable prepareTimelineThumbDrawable() {
        @SuppressLint("InflateParams")
        android.view.View thumbLayout = LayoutInflater.from(this)
                .inflate(R.layout.layout_monitoring_custom_seekbar, null);

        int width = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                96,
                getResources().getDisplayMetrics());
        int height = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                72,
                getResources().getDisplayMetrics());

        Bitmap thumbBitmap = ImageProcessor.bitmapFromView(thumbLayout, width, height);
        return new BitmapDrawable(thumbBitmap);
    }
}
