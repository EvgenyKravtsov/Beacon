package kgk.beacon.monitoring.presentation.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.List;

import kgk.beacon.R;
import kgk.beacon.monitoring.DependencyInjection;
import kgk.beacon.monitoring.domain.interactor.SetActiveMonitoringEntityGroup;
import kgk.beacon.monitoring.domain.interactor.UpdateMonitoringEntities;
import kgk.beacon.monitoring.domain.model.MonitoringEntity;
import kgk.beacon.monitoring.domain.model.MonitoringEntityGroup;
import kgk.beacon.monitoring.presentation.adapter.MonitoringGroupListActivityAdapter;
import kgk.beacon.monitoring.presentation.presenter.MonitoringGroupListViewPresenter;
import kgk.beacon.monitoring.presentation.view.MonitoringGroupListView;

public class MonitoringGroupListActivity extends AppCompatActivity
        implements MonitoringGroupListView {

    // Views
    private FrameLayout backButton;
    private TextView actionBarTitleTextView;
    private RecyclerView recyclerView;
    private Button allButton;

    ProgressDialog progressDialog;

    private MonitoringGroupListViewPresenter presenter;
    private MonitoringGroupListActivityAdapter adapter;
    private List<MonitoringEntityGroup> monitoringEntityGroups;
    private MonitoringEntityGroup activeMonitoringEntityGroup;

    ////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monitoring_activity_group_list);
        initViews();
        initListeners();
        initRecyclerView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindPresenter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.requestMonitoringEntityGroups();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindPresenter();
    }

    ////

    @Override
    public void showMonitoringEntityGroups(
            List<MonitoringEntityGroup> groups,
            MonitoringEntityGroup activeGroup) {

        this.monitoringEntityGroups = groups;
        this.activeMonitoringEntityGroup = activeGroup;

        adapter = new MonitoringGroupListActivityAdapter(this, this);
        adapter.setGroups(this.monitoringEntityGroups);
        adapter.setActiveGroup(this.activeMonitoringEntityGroup);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void toggleDownloadDataProgressDialog(boolean status) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getString(R.string.monitoring_downloading_data));
            progressDialog.setIndeterminate(true);
            progressDialog.setCanceledOnTouchOutside(false);
        }

        if (status) progressDialog.show();
        else progressDialog.dismiss();
    }

    ////

    private void initViews() {
        backButton = (FrameLayout) findViewById(R.id.monitoring_action_bar_back_button);
        actionBarTitleTextView = (TextView) findViewById(R.id.monitoring_action_bar_title_text_view);
        actionBarTitleTextView.setText(R.string.monitoring_choose_vehicle_group_screen_title);

        recyclerView = (RecyclerView)
                findViewById(R.id.monitoring_activity_group_list_recycler_view);
        allButton = (Button)
                findViewById(R.id.monitoring_activity_group_list_all_button);
    }

    private void initListeners() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackButtonClick();
            }
        });

        allButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickAllButton();
            }
        });
    }

    private void initRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void bindPresenter() {
        presenter = new MonitoringGroupListViewPresenter(this);
    }

    private void unbindPresenter() {
        if (presenter != null) presenter.unbindView();
        presenter = null;
    }

    //// Control callbacks

    private void onBackButtonClick() {
        NavUtils.navigateUpFromSameTask(this);
    }

    private void onClickAllButton() {
        toggleDownloadDataProgressDialog(true);

        // TODO Delete test code
        for (MonitoringEntity entity : DependencyInjection.provideMonitoringManager().getMonitoringEntities())
            Log.d("debug", entity.toString());

        UpdateMonitoringEntities updateInteractor =
                new UpdateMonitoringEntities(DependencyInjection.provideMonitoringManager().getMonitoringEntities());
        updateInteractor.setListener(new UpdateMonitoringEntities.Listener() {
            @Override
            public void onMonitoringEntitiesUpdated(List<MonitoringEntity> monitoringEntities) {
                SetActiveMonitoringEntityGroup interactor =
                        new SetActiveMonitoringEntityGroup(null);
                interactor.execute();
                toggleDownloadDataProgressDialog(false);
                Intent intent = new Intent(MonitoringGroupListActivity.this, MonitoringListActivity.class);
                startActivity(intent);
            }
        });
        updateInteractor.execute();
    }
}