package kgk.beacon.monitoring.presentation.routereport;

import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
    @Bind(R.id.route_report_timeline_time)
    TextView timelineTimeTextView;
    @Bind(R.id.route_report_timeline)
    RouteReportSeekBar timelineSeekBar;
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
        timelineSeekBar.setThumb(
                getResources().getDrawable(R.drawable.monitoring_route_report_seekbar_thumb));
        updateTimelineIcon(0);

        timelineSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                updateTimelineIcon(progress);
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
        //RouteReport routeReport = (RouteReport) startingIntent.getSerializableExtra(EXTRA_ROUTE_REPORT);

        presenter = new RouteReportPresenter(map, daysView, eventsView, RouteReport.RouteReportInstance);
        presenter.attachView(this);

        map.setPresenter(presenter);
        daysView.setPresenter(presenter);
        eventsView.setPresenter(presenter);
    }

    private void updateTimelineIcon(int progress) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        Rect thumbRect = timelineSeekBar.getSeekBarThumb().getBounds();

        layoutParams.setMargins(
                thumbRect.centerX() + 8,
                0,
                0,
                (int) getResources().getDimension(R.dimen.route_report_timeline_icon_bottom_margin));

        timelineTimeTextView.setLayoutParams(layoutParams);
        timelineTimeTextView.setText(millisecondsToTimeString(progress));
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





























