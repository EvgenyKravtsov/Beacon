package kgk.beacon.monitoring.presentation.routereport;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.maps.MapView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import butterknife.Bind;
import butterknife.BindDrawable;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kgk.beacon.R;
import kgk.beacon.monitoring.domain.model.routereport.RouteReport;
import kgk.beacon.monitoring.domain.model.routereport.RouteReportEventType;
import kgk.beacon.monitoring.presentation.activity.RouteReportSettingsActivity;
import kgk.beacon.monitoring.presentation.model.MapType;
import kgk.beacon.util.ImageProcessor;

import static kgk.beacon.monitoring.presentation.routereport.RouteReportContract.DaysView;
import static kgk.beacon.monitoring.presentation.routereport.RouteReportContract.EventsView;
import static kgk.beacon.monitoring.presentation.routereport.RouteReportContract.Map;
import static kgk.beacon.monitoring.presentation.routereport.RouteReportContract.Presenter;

public class RouteReportActivity extends AppCompatActivity
        implements RouteReportContract.View {

    public static final String EXTRA_ROUTE_REPORT = "extra_route_report";

    private Presenter presenter;
    private AlertDialog mapLayerDialog;

    ////

    @Bind(R.id.monitoring_action_bar_title_text_view)
    TextView titleTextView;
    @Bind(R.id.route_report_google_map_view)
    MapView googleMapView;
    @Bind(R.id.route_report_days_list)
    RecyclerView daysListRecyclerView;
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
    @Bind(R.id.route_report_events_list)
    ExpandableListView eventsListExpandableListView;
    @Bind(R.id.route_report_slider)
    SlidingUpPanelLayout routeReportSlider;

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

    @OnClick(R.id.monitoring_action_bar_back_button)
    public void onClickBackButton() {
        NavUtils.navigateUpFromSameTask(this);
    }

    @OnClick(R.id.route_report_go_to_settings)
    public void onClickGoToSettingsButton() {
        Intent intent = new Intent(this, RouteReportSettingsActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.route_report_layers_button)
    public void onClickLayersButton() {
        if (mapLayerDialog == null) {
            android.view.View dialogLayout = getLayoutInflater()
                    .inflate(R.layout.route_report_map_layer_dialog, null);

            Button kgkLayerButton = (Button) dialogLayout.findViewById(R.id.kgk_layer);
            kgkLayerButton.setOnClickListener(new android.view.View.OnClickListener() {
                @Override
                public void onClick(android.view.View view) {
                    presenter.onMapLayerSelected(MapType.KGK);
                    if (mapLayerDialog != null) mapLayerDialog.dismiss();
                }
            });

            Button osmLayerButton = (Button) dialogLayout.findViewById(R.id.osm_layer);
            osmLayerButton.setOnClickListener(new android.view.View.OnClickListener() {
                @Override
                public void onClick(android.view.View view) {
                    presenter.onMapLayerSelected(MapType.OSM);
                    if (mapLayerDialog != null) mapLayerDialog.dismiss();
                }
            });

            Button googleLayerButton = (Button) dialogLayout.findViewById(R.id.google_layer);
            googleLayerButton.setOnClickListener(new android.view.View.OnClickListener() {
                @Override
                public void onClick(android.view.View view) {
                    presenter.onMapLayerSelected(MapType.GOOGLE);
                    if (mapLayerDialog != null) mapLayerDialog.dismiss();
                }
            });

            Button satelliteLayerButton = (Button) dialogLayout.findViewById(R.id.satellite_layer);
            satelliteLayerButton.setOnClickListener(new android.view.View.OnClickListener() {
                @Override
                public void onClick(android.view.View view) {
                    presenter.onMapLayerSelected(MapType.SATELLITE);
                    if (mapLayerDialog != null) mapLayerDialog.dismiss();
                }
            });

            AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(dialogLayout);
            mapLayerDialog = builder.create();
        }

        mapLayerDialog.show();
        mapLayerDialog.getWindow().setLayout(512, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    @OnClick(R.id.route_report_zoom_in_button)
    public void onClickZoomInButton() {
        presenter.onMapZoomInButtonClick();
    }

    @OnClick(R.id.route_report_zoom_out_button)
    public void onClickZoomOutButton() {
        presenter.onMapZoomOutButtonClick();
    }

    @OnClick(R.id.slider_inner_area)
    public void onClickSliderInnerAreaLinearLayout() {
        SlidingUpPanelLayout.PanelState sliderState = routeReportSlider.getPanelState();
        switch (sliderState) {
            case COLLAPSED:
                routeReportSlider.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                break;
            case EXPANDED:
                routeReportSlider.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                break;
        }
    }

    ////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_report_mvp);
        ButterKnife.bind(this);
        titleTextView.setText(getString(R.string.monitoring_route_report_screen_title));
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

    @Override
    public void moveTimeline(long timestamp) {
        timelineSeekBar.setProgress((int) timestamp);
    }

    @Override
    public void resetTimeline() {
        timelineSeekBar.setProgress(0);
    }

    @Override
    public void scrollEventsListToPosition(int position) {
        eventsListExpandableListView.scrollTo(0, 0);
    }

    @Override
    public void collapseSlider() {
        routeReportSlider.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
    }

    ////

    private void prepareGoogleMap(Bundle savedInstanceState) {
        googleMapView.onCreate(savedInstanceState);
        googleMapView.onResume();
    }

    private DaysView prepareDaysListRecycler() {
        daysListRecyclerView.setHasFixedSize(true);
        daysListRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );

        RouteReportDaysAdapter adapter = new RouteReportDaysAdapter();
        daysListRecyclerView.setAdapter(adapter);
        daysListRecyclerView.setItemAnimator(new DefaultItemAnimator());

        return adapter;
    }

    private EventsView prepareEventsListRecycler() {
        RouteReportEventsAdapter adapter = new RouteReportEventsAdapter(getLayoutInflater());
        eventsListExpandableListView.setAdapter(adapter);
        eventsListExpandableListView.setOnGroupExpandListener(adapter);
        return adapter;
    }

    private void prepareTimeline() {
        timelineSeekBar.setThumbOffset(36);
        timelineSeekBar.setThumb(prepareTimelineThumbDrawable(millisecondsToTimeString(0)));
        timelineSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                timelineSeekBar.setThumb(prepareTimelineThumbDrawable(millisecondsToTimeString(progress)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                presenter.onTimeChosenFromTimeline(seekBar.getProgress());
            }
        });
    }

    private void attachPresenter() {
        Map map = new RouteReportGoogleMap(googleMapView);
        DaysView daysView = prepareDaysListRecycler();
        EventsView eventsView = prepareEventsListRecycler();

        Intent startingIntent = getIntent();
        RouteReport routeReport = (RouteReport) startingIntent.getSerializableExtra(EXTRA_ROUTE_REPORT);

        presenter = new RouteReportPresenter(map, daysView, eventsView, routeReport);
        presenter.attachView(this);

        map.setPresenter(presenter);
        daysView.setPresenter(presenter);
        eventsView.setPresenter(presenter);
    }

    private Drawable prepareTimelineThumbDrawable(String timeString) {
        @SuppressLint("InflateParams")
        android.view.View thumbLayout = LayoutInflater.from(this)
                .inflate(R.layout.layout_monitoring_custom_seekbar, null);

        TextView timeTextView = (TextView) thumbLayout.findViewById(R.id.route_report_timeline_time);
        timeTextView.setText(timeString);

        boolean isTablet = getResources().getBoolean(R.bool.isTablet);

        int width = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                isTablet ? 48 : 96,
                getResources().getDisplayMetrics());
        int height = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                isTablet ? 36 : 72,
                getResources().getDisplayMetrics());

        Bitmap thumbBitmap = ImageProcessor.bitmapFromView(thumbLayout, width, height);
        return new BitmapDrawable(thumbBitmap);
    }

    private String millisecondsToTimeString(int milliseconds) {
        int seconds = milliseconds / 1000;
        int hours = seconds / 3600;
        int minutes = (seconds - (hours * 3600)) / 60;

        String hoursString = hours > 9 ? Integer.toString(hours) : "0" + hours;
        String minutesString = minutes > 9 ? Integer.toString(minutes) : "0" + minutes;

        return String.format("%s:%s", hoursString, minutesString);
    }
}





























