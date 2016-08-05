package kgk.beacon.monitoring.presentation.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.Model.ParentObject;
import com.google.android.gms.maps.MapView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import kgk.beacon.R;
import kgk.beacon.monitoring.domain.model.routereport.RouteReport;
import kgk.beacon.monitoring.domain.model.routereport.RouteReportEvent;
import kgk.beacon.monitoring.presentation.adapter.RouteReportDaysListAdapter;
import kgk.beacon.monitoring.presentation.adapter.eventlistadapter.EventsListObject;
import kgk.beacon.monitoring.presentation.adapter.eventlistadapter.RouteReportEventsListAdapter;
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
    private RecyclerView eventsRecyclerView;

    private RouteReport routeReport;

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
        initDaysRecyclerView();
        initEventsRecyclerView();
    }

    ////

    @Override
    public void toggleDayDisplay(Calendar day, boolean enabled) {

        // TODO Delete test code
        Log.d("debug", "toggleDayDisplay ::called");
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
        eventsRecyclerView = (RecyclerView)
                findViewById(R.id.monitoring_activity_route_report_events_recycler_view);
    }

    private void initListeners() {

    }

    private void initMap() {

    }

    private void initDaysRecyclerView() {
        daysRecyclerView.setHasFixedSize(true);
        daysRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );

        List<Calendar> dates = new ArrayList<>();

        for (Map.Entry<Long, List<RouteReportEvent>> entry : routeReport.getDays().entrySet()) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date(entry.getKey() * 1000));
            dates.add(0, calendar);
        }

        RouteReportDaysListAdapter adapter = new RouteReportDaysListAdapter(
                this,
                dates
        );

        daysRecyclerView.setAdapter(adapter);
        daysRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void initEventsRecyclerView() {
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        RouteReportEventsListAdapter adapter =
                new RouteReportEventsListAdapter(this, daysToParentObjects());

        adapter.setCustomParentAnimationViewId(R.id.monitoring_activty_route_report_list_header_title);
        adapter.setParentClickableViewAnimationDefaultDuration();
        adapter.setParentAndIconExpandOnClick(true);

        eventsRecyclerView.setAdapter(adapter);
    }

    private List<ParentObject> daysToParentObjects() {
        List<ParentObject> parentObjects = new ArrayList<>();

        for (Map.Entry<Long, List<RouteReportEvent>> entry : routeReport.getDays().entrySet()) {
            ParentObject parentObject = new EventsListObject(entry.getKey());
            parentObject.setChildObjectList(routeReportsEventToObjects(entry.getValue()));
            parentObjects.add(parentObject);
        }

        return parentObjects;
    }

    private List<Object> routeReportsEventToObjects(List<RouteReportEvent> events) {
        List<Object> objects = new ArrayList<>();
        for (RouteReportEvent event : events) objects.add(event);
        return objects;
    }
}
