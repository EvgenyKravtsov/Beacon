package kgk.beacon.monitoring.presentation.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import kgk.beacon.R;
import kgk.beacon.monitoring.domain.model.routereport.MovingEvent;
import kgk.beacon.monitoring.domain.model.routereport.MovingEventSignal;
import kgk.beacon.monitoring.domain.model.routereport.ParkingEvent;
import kgk.beacon.monitoring.domain.model.routereport.RouteReport;
import kgk.beacon.monitoring.domain.model.routereport.RouteReportEvent;
import kgk.beacon.monitoring.presentation.adapter.RouteReportDaysListAdapter;
import kgk.beacon.monitoring.presentation.adapter.RouteReportEventsListExpandableAdapter;
import kgk.beacon.monitoring.presentation.adapter.RouteReportGoogleMapAdapter;
import kgk.beacon.monitoring.presentation.adapter.RouteReportMapAdapter;
import kgk.beacon.monitoring.presentation.view.RouteReportView;

public class RouteReportActivity extends AppCompatActivity
        implements RouteReportView {

    public static final String EXTRA_ROUTE_REPORT = "extra_route_report";

    // Views
    private MapView googleMapView;
    private Button zoomInButton;
    private Button zoomOutButton;
    private RecyclerView daysRecyclerView;
    private LinearLayout currentEventLayout;
    private TextView currentEventTextView;
    private TextView currentEventSpeedTextView;
    private TextView currentEventGsmTextView;
    private TextView currenrEventSatellitesTextView;
    private LinearLayout detailedInformationLayout;
    private SeekBar dateSeekBar;
    private ExpandableListView eventsListView;

    private RouteReport routeReport;
    private Map<Long, List<RouteReportEvent>> routeReportDays;
    private RouteReportEventsListExpandableAdapter adapter;
    private RouteReportMapAdapter mapAdapter;

    // TODO Delete test code
    private Map<Long, LatLng> coordinatesByTime;
    private List<Long> sortedTimes;

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

        // TODO Delete test code
        testAlgo();

        initViews(savedInstanceState);
        initListeners();
        initMap();
        initDaysRecyclerView();
        initEventsListView();
    }

    ////

    @Override
    public void toggleDayDisplay(Calendar day, boolean enabled) {
        if (routeReportDays == null) {
            routeReportDays = new HashMap<>();
            for (Map.Entry<Long, List<RouteReportEvent>> entry : routeReport.getDays().entrySet())
                routeReportDays.put(entry.getKey(), entry.getValue());
        }

        if (enabled) {
            routeReportDays.put(
                    day.getTimeInMillis(),
                    routeReport.getDays().get(day.getTimeInMillis()));

            mapAdapter.showRouteReportDay(
                    day.getTimeInMillis(),
                    routeReport.getDays().get(day.getTimeInMillis()));
        } else {
            routeReportDays.remove(day.getTimeInMillis());
            clearEventDetails();

            mapAdapter.clearRouteReportDay(day.getTimeInMillis());
        }

        adapter.setExpandableListTitle(getKeysFromDaysMap(routeReportDays));
        adapter.setExpandableListDetail(routeReportDays);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showEventDetails(
            String eventType,
            double speed,
            int csq,
            int satellites) {

        currentEventTextView.setText(eventType);

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
    public void mapReadyForUse() {
        for (Map.Entry<Long, List<RouteReportEvent>> entry : routeReport.getDays().entrySet())
            mapAdapter.showRouteReportDay(entry.getKey(), entry.getValue());

        if (getLastEvent().getCoordinates() != null)
            mapAdapter.centerMap(getLastEvent().getCoordinates());
    }

    ////

    private void initViews(Bundle savedInstanceState) {
        googleMapView = (MapView)
                findViewById(R.id.monitoring_activity_route_report_google_map);

        assert googleMapView != null;
        googleMapView.onCreate(savedInstanceState);
        googleMapView.onResume();

        zoomInButton = (Button)
                findViewById(R.id.monitoring_activity_route_report_zoom_in_button);
        zoomOutButton = (Button)
                findViewById(R.id.monitoring_activity_route_report_zoom_out_button);

        daysRecyclerView = (RecyclerView)
                findViewById(R.id.monitoring_activity_route_report_days_recycler_view);
        if (routeReport.getDays().size() < 2) daysRecyclerView.setVisibility(View.GONE);

        currentEventLayout = (LinearLayout)
                findViewById(R.id.monitoring_activity_route_report_current_event_layout);
        currentEventTextView = (TextView)
                findViewById(R.id.monitoring_activity_route_report_current_event_text_view);
        currentEventSpeedTextView = (TextView)
                findViewById(R.id.monitoring_activity_route_report_current_event_speed_text_view);
        currentEventGsmTextView = (TextView)
                findViewById(R.id.monitoring_activity_route_report_current_event_gsm_text_view);
        currenrEventSatellitesTextView = (TextView)
                findViewById(R.id.monitoring_activity_route_report_current_event_satellites_text_view);
        detailedInformationLayout = (LinearLayout)
                findViewById(R.id.monitoring_activity_route_report_detailed_information_layout);
        dateSeekBar = (SeekBar)
                findViewById(R.id.monitoring_activity_route_report_date_seek_bar);
        //eventsRecyclerView = (RecyclerView)
        //        findViewById(R.id.monitoring_activity_route_report_events_recycler_view);
        eventsListView = (ExpandableListView)
                findViewById(R.id.monitoring_activity_route_report_events_list_view);
    }

    private void initListeners() {
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

        currentEventLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int visibility = detailedInformationLayout.getVisibility();
                if (visibility == View.VISIBLE) detailedInformationLayout.setVisibility(View.GONE);
                if (visibility == View.GONE) detailedInformationLayout.setVisibility(View.VISIBLE);
            }
        });

        dateSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                long time = getClosestValue(1470517200000L + progress, sortedTimes);

                // TODO Delete test code
                Log.d("debug", "Progress = " + time);

                LatLng coordinates = coordinatesByTime.get(time);
                mapAdapter.centerMap(coordinates);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

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
            dates.add(0, calendar);
        }

        Collections.sort(dates);

        RouteReportDaysListAdapter adapter = new RouteReportDaysListAdapter(
                this,
                dates
        );

        daysRecyclerView.setAdapter(adapter);
        daysRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void initEventsListView() {
        adapter = new RouteReportEventsListExpandableAdapter(
                this,
                getKeysFromDaysMap(routeReport.getDays()),
                routeReport.getDays()
        );

        eventsListView.setAdapter(adapter);
        eventsListView.setOnChildClickListener(adapter);
    }

    private List<Long> getKeysFromDaysMap(Map<Long, List<RouteReportEvent>> days) {
        List<Long> keys = new ArrayList<>();

        for (Map.Entry<Long, List<RouteReportEvent>> entry : days.entrySet()) {
            keys.add(entry.getKey());
        }

        return keys;
    }

    private void clearEventDetails() {
        currentEventTextView.setText("");
        currentEventSpeedTextView.setText("");
        currentEventGsmTextView.setText("");
        currenrEventSatellitesTextView.setText("");
    }

    private RouteReportEvent getLastEvent() {
        Set<Long> keySet = routeReport.getDays().keySet();
        long maxKey = 0;

        for (long key : keySet) {
            if (maxKey == 0) maxKey = key;
            if (maxKey < key) maxKey = key;
        }

        List<RouteReportEvent> events = routeReport.getDays().get(maxKey);
        return events.get(events.size() - 1);
    }







    private void testAlgo() {
        coordinatesByTime = new HashMap<>();

        List<RouteReportEvent> events = routeReport.getDays().get(1470517200000L);

        for (RouteReportEvent event : events) {

            if (event instanceof ParkingEvent) {
                coordinatesByTime.put(
                        event.getStartTime(),
                        new LatLng(
                                ((ParkingEvent) event).getLatitude(),
                                ((ParkingEvent) event).getLongitude()));
            }

            if (event instanceof MovingEvent) {
                List<MovingEventSignal> signals = ((MovingEvent) event).getSignals();

                coordinatesByTime.put(
                        event.getStartTime(),
                        new LatLng(
                                ((MovingEvent) event).getLatitude(),
                                ((MovingEvent) event).getLongitude()));

                for (MovingEventSignal signal : signals) {
                    coordinatesByTime.put(
                            signal.getTime(),
                            new LatLng(signal.getLatitude(), signal.getLongitude()));
                }
            }
        }

        sortedTimes = new ArrayList<>();
        for (Map.Entry<Long, LatLng> entry : coordinatesByTime.entrySet()) {
            sortedTimes.add(entry.getKey());
        }
        Collections.sort(sortedTimes);
    }

    private long getClosestValue(long value, List<Long> values) {
        int lo = 0;
        int hi = values.size() - 1;

        long lastValue = 0;

        while (lo <= hi) {
            int mid = (lo + hi) / 2;
            lastValue = values.get(mid);

            if (value < lastValue) {
                hi = mid - 1;
            } else if (value < lastValue) {
                lo = mid + 1;
            } else {
                return lastValue;
            }
        }

        return lastValue;
    }
}
