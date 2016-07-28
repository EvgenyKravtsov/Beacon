package kgk.beacon.monitoring.presentation.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import kgk.beacon.R;
import kgk.beacon.monitoring.domain.model.MonitoringEntity;
import kgk.beacon.monitoring.presentation.adapter.MonitoringListActivityAdapter;
import kgk.beacon.monitoring.presentation.presenter.MonitoringListViewPresenter;
import kgk.beacon.monitoring.presentation.view.MonitoringListView;

public class MonitoringListActivity extends AppCompatActivity implements MonitoringListView {

    private static final int SEARCH_DISPLAY_CRITERIA = 20; // List size

    // Views
    private RecyclerView recyclerView;
    private Button alphabetOrderButton;
    private Button statusOrderButton;
    private SearchView searchView;

    private MonitoringListViewPresenter presenter;
    private MonitoringListActivityAdapter adapter;
    private List<MonitoringEntity> monitoringEntities;
    private MonitoringEntity activeMonitoringEntity;

    ////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monitoring_activity_list);
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
        presenter.requestMonitoringEntities();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindPresenter();
    }

    ////

    @Override
    public void showMonitoringEntities(
            List<MonitoringEntity> monitoringEntities,
            MonitoringEntity activeMonitoringEntity) {

        this.monitoringEntities = monitoringEntities;
        this.activeMonitoringEntity = activeMonitoringEntity;

        adapter = new MonitoringListActivityAdapter(this);
        adapter.setMonitoringEntities(this.monitoringEntities);
        adapter.setActiveMonitoringEntity(this.activeMonitoringEntity);
        recyclerView.setAdapter(adapter);

        if (monitoringEntities.size() > SEARCH_DISPLAY_CRITERIA) {
            searchView.setVisibility(View.VISIBLE);
            initSearchView();
        } else searchView.setVisibility(View.GONE);
    }

    ////

    private void initViews() {
        recyclerView = (RecyclerView) findViewById(R.id.monitoring_activity_list_recycler_view);

        alphabetOrderButton = (Button)
                findViewById(R.id.monitoring_activity_list_alphabet_order_button);
        statusOrderButton = (Button)
                findViewById(R.id.monitoring_activity_list_status_order_button);

        searchView = (SearchView) findViewById(R.id.monitoring_activity_list_search_view);
    }

    private void initListeners() {
        alphabetOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAlphabetOrderButtonClick();
            }
        });
        statusOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStatusOrderButtonClick();
            }
        });
    }

    private void initRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void initSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<MonitoringEntity> monitoringEntities = filterListWithSearchQuery(newText);
                adapter.setMonitoringEntities(monitoringEntities);
                adapter.notifyDataSetChanged();
                return true;
            }
        });
    }

    private void bindPresenter() {
        presenter = new MonitoringListViewPresenter(this);
    }

    private void unbindPresenter() {
        if (presenter != null) presenter.unbindView();
        presenter = null;
    }

    private List<MonitoringEntity> filterListWithSearchQuery(String query) {
        query = query.toLowerCase();
        List<MonitoringEntity> monitoringEntities = new ArrayList<>();

        for (MonitoringEntity monitoringEntity : this.monitoringEntities) {
            String monitoringEntityName = (monitoringEntity.getMark() +
                    monitoringEntity.getModel() +
                    monitoringEntity.getStateNumber()).toLowerCase();

            if (monitoringEntityName.contains(query)) monitoringEntities.add(monitoringEntity);
        }

        return monitoringEntities;
    }

    //// Control callbacks

    private void onAlphabetOrderButtonClick() {
        adapter.sortByAlphabet();
        adapter.notifyDataSetChanged();
    }

    private void onStatusOrderButtonClick() {
        adapter.sortByStatus();
        adapter.notifyDataSetChanged();
    }
}
