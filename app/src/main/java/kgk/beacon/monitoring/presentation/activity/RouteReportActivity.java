package kgk.beacon.monitoring.presentation.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.maps.MapView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;

import kgk.beacon.R;
import kgk.beacon.monitoring.domain.model.routereport.MovingEvent;
import kgk.beacon.monitoring.domain.model.routereport.MovingEventSignal;
import kgk.beacon.monitoring.domain.model.routereport.ParkingEvent;
import kgk.beacon.monitoring.domain.model.routereport.RouteReport;
import kgk.beacon.monitoring.domain.model.routereport.RouteReportEvent;
import kgk.beacon.monitoring.presentation.adapter.RouteReportDaysListAdapter;
import kgk.beacon.monitoring.presentation.adapter.RouteReportEventsListAdapter;
import kgk.beacon.monitoring.presentation.adapter.RouteReportGoogleMapAdapter;
import kgk.beacon.monitoring.presentation.adapter.RouteReportMapAdapter;
import kgk.beacon.monitoring.presentation.view.RouteReportView;

public class RouteReportActivity extends AppCompatActivity
        implements RouteReportView {

    public static final String EXTRA_ROUTE_REPORT = "extra_route_report";

    // Views
    private SlidingUpPanelLayout slider;
    private ImageButton backButton;
    private MapView googleMapView;
    private ImageButton zoomInButton;
    private ImageButton zoomOutButton;
    private RecyclerView daysRecyclerView;
    private TextView currentEventTextView;
    private TextView currentEventDateTextView;
    private TextView currentEventSpeedTextView;
    private TextView currentEventGsmTextView;
    private TextView currenrEventSatellitesTextView;
    private TextView dateSeekBarTitle;
    private SeekBar dateSeekBar;
    private RecyclerView eventsListView;

    private RouteReport routeReport;
    private RouteReportEventsListAdapter adapter;
    private RouteReportMapAdapter mapAdapter;

    private List<Long> eventDatesForActiveDay;
    private long selectedDate;

    ////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monitoring_activity_route_report);

        Intent startingIntent = getIntent();
        RouteReport routeReport = (RouteReport)
                startingIntent.getSerializableExtra(EXTRA_ROUTE_REPORT);

        if (routeReport == null) finish();
        else this.routeReport = routeReport;

        initViews(savedInstanceState);
        initListeners();
        initMap();
    }

    ////

    @Override
    public void toggleDayDisplay(Calendar day, boolean enabled) {
        if (enabled) {
            mapAdapter.showRouteReportDay(
                    day.getTimeInMillis(),
                    routeReport.getDays().get(day.getTimeInMillis()));
        } else {
            mapAdapter.clearRouteReportDay(day.getTimeInMillis());
            clearEventDetails();
        }
    }

    @Override
    public void showEventDetails(
            String eventType,
            String eventDate,
            double speed,
            int csq,
            int satellites) {

        currentEventTextView.setText(eventType);
        currentEventDateTextView.setText(eventDate);

        currentEventSpeedTextView.setText(
                String.format(Locale.ROOT, "%.2f", speed)
        );

        currentEventGsmTextView.setText(
                String.format(Locale.ROOT, "%d", csq)
        );

        currenrEventSatellitesTextView.setText(
                String.format(Locale.ROOT, "%d", satellites)
        );
    }

    @Override
    public void showEventStartTime(long date) {
        long progress = date - selectedDate;
        dateSeekBar.setProgress((int) progress);
    }

    @Override
    public void centerOnParkingEvent(ParkingEvent event) {
        mapAdapter.centerOnParkingEvent(event);
    }

    @Override
    public void centerOnMovingEvent(MovingEvent event) {
        mapAdapter.centerOnMovingEvent(event);
    }

    @Override
    public void centerOnMovingEventSignal(MovingEventSignal signal) {
        mapAdapter.centerOnMovingEventSignal(signal);
    }

    @Override
    public void mapReadyForUse() {
        initDaysRecyclerView();
        initEventsListView();

        RouteReportEvent event = getFirstEvent();
        if (event.getCoordinates() != null) {
            if (event instanceof ParkingEvent) mapAdapter.centerOnParkingEvent((ParkingEvent) event);
            if (event instanceof MovingEvent) mapAdapter.centerOnMovingEvent((MovingEvent) event);
        }
    }

    @Override
    public void selectDay(long date) {
        adapter.setEvents(routeReport.getDays().get(date));
        adapter.notifyDataSetChanged();

        prepareDataForSeekbar(date);
        selectedDate = date;
    }

    @Override
    public void clearEventDetails() {
        currentEventTextView.setText("Event type");
        currentEventDateTextView.setText("--");
        currentEventSpeedTextView.setText("--");
        currentEventGsmTextView.setText("--");
        currenrEventSatellitesTextView.setText("--");
    }

    ////

    private void initViews(Bundle savedInstanceState) {
        slider = (SlidingUpPanelLayout) findViewById(R.id.monitoring_activity_route_report_slider);
        backButton = (ImageButton) findViewById(R.id.monitoring_activity_route_report_back_button);

        googleMapView = (MapView)
                findViewById(R.id.monitoring_activity_route_report_google_map);

        assert googleMapView != null;
        googleMapView.onCreate(savedInstanceState);
        googleMapView.onResume();

        zoomInButton = (ImageButton)
                findViewById(R.id.monitoring_activity_route_report_zoom_in_button);
        zoomOutButton = (ImageButton)
                findViewById(R.id.monitoring_activity_route_report_zoom_out_button);

        daysRecyclerView = (RecyclerView)
                findViewById(R.id.monitoring_activity_route_report_days_recycler_view);
        if (routeReport.getDays().size() < 2) daysRecyclerView.setVisibility(View.GONE);

        currentEventTextView = (TextView)
                findViewById(R.id.monitoring_activity_route_report_current_event_text_view);
        currentEventDateTextView = (TextView)
                findViewById(R.id.monitoring_activity_route_report_current_event_date_text_view);
        currentEventSpeedTextView = (TextView)
                findViewById(R.id.monitoring_activity_route_report_current_event_speed_text_view);
        currentEventGsmTextView = (TextView)
                findViewById(R.id.monitoring_activity_route_report_current_event_gsm_text_view);
        currenrEventSatellitesTextView = (TextView)
                findViewById(R.id.monitoring_activity_route_report_current_event_satellites_text_view);
        dateSeekBarTitle = (TextView)
                findViewById(R.id.onitoring_activity_route_report_date_seek_bar_title);
        dateSeekBar = (SeekBar)
                findViewById(R.id.monitoring_activity_route_report_date_seek_bar);
        eventsListView = (RecyclerView)
                findViewById(R.id.monitoring_activity_route_report_events_list_view);
    }

    @SuppressLint("SimpleDateFormat")
    private void initListeners() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackButtonClick();
            }
        });

        zoomInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapAdapter.zoomIn();
            }
        });

        zoomOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapAdapter.zoomOut();
            }
        });

        dateSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                long time = selectedDate + progress;
                dateSeekBarTitle.setText(new SimpleDateFormat("HH:mm").format(time));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                long time = getClosestValue(selectedDate + progress, eventDatesForActiveDay);

                adapter.eventSelectedByTime(time);

                for (Map.Entry<Long, List<RouteReportEvent>> entry : routeReport.getDays().entrySet()) {
                    for (RouteReportEvent event : entry.getValue()) {
                        if (event.getStartTime() == time) {
                            if (event instanceof ParkingEvent) {
                                mapAdapter.centerOnParkingEvent((ParkingEvent) event);
                                break;
                            }
                            if (event instanceof MovingEvent) {
                                mapAdapter.centerOnMovingEvent((MovingEvent) event);
                                break;
                            }
                        }

                        if (event instanceof MovingEvent) {
                            for (MovingEventSignal signal : ((MovingEvent) event).getSignals()) {
                                if (signal.getTime() == time) {
                                    mapAdapter.centerOnMovingEventSignal(signal);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    private void initMap() {
        mapAdapter = new RouteReportGoogleMapAdapter(this, googleMapView);
    }

    private void initDaysRecyclerView() {
        daysRecyclerView.setHasFixedSize(true);
        daysRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );

        List<Calendar> dates = new ArrayList<>();

        for (Map.Entry<Long, List<RouteReportEvent>> entry : routeReport.getDays().entrySet()) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date(entry.getKey()));
            dates.add(calendar);
        }

        RouteReportDaysListAdapter adapter = new RouteReportDaysListAdapter(
                this,
                dates
        );

        daysRecyclerView.setAdapter(adapter);
        daysRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void initEventsListView() {
        eventsListView.setHasFixedSize(true);
        eventsListView.setLayoutManager(new LinearLayoutManager(this));

        SortedMap<Long, List<RouteReportEvent>> days = routeReport.getDays();
        long lastDayDate = days.lastKey();
        adapter = new RouteReportEventsListAdapter(this, days.get(days.lastKey()));

        eventsListView.setAdapter(adapter);
        prepareDataForSeekbar(lastDayDate);
        selectedDate = lastDayDate;
    }

    private RouteReportEvent getFirstEvent() {
        List<RouteReportEvent> events = routeReport.getDays().get(routeReport.getDays().lastKey());
        return events.get(0);
    }

    private void prepareDataForSeekbar(long date) {
        List<RouteReportEvent> events = routeReport.getDays().get(date);
        eventDatesForActiveDay = new ArrayList<>();

        for (RouteReportEvent event : events) {
            if (event instanceof ParkingEvent) {
                eventDatesForActiveDay.add(event.getStartTime());
            }

            if (event instanceof MovingEvent) {
                List<MovingEventSignal> signals = ((MovingEvent) event).getSignals();
                eventDatesForActiveDay.add(event.getStartTime());

                for (MovingEventSignal signal : signals) {
                    eventDatesForActiveDay.add(signal.getTime());
                }
            }
        }
    }

    private long getClosestValue(long value, List<Long> values) {
        int lo = 0;
        int hi = values.size() - 1;

        long lastValue = 0;

        while (lo <= hi) {
            int mid = (lo + hi) / 2;
            lastValue = values.get(mid);

            if (value < lastValue) hi = mid - 1;
            else if (value > lastValue) lo = mid + 1;
            else return lastValue;
        }

        return lastValue;
    }

    //// Control callbacks

    private void onBackButtonClick() {
        NavUtils.navigateUpFromSameTask(this);
    }
}





























