package kgk.beacon.monitoring.presentation.activity;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    private FrameLayout backButton;
    private TextView actionBarTitleTextView;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private LinearLayout sortingModeButton;

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
        backButton = (FrameLayout) findViewById(R.id.monitoring_action_bar_back_button);
        actionBarTitleTextView = (TextView) findViewById(R.id.monitoring_action_bar_title_text_view);
        actionBarTitleTextView.setText(R.string.monitoring_choose_vehicle_screen_title);

        recyclerView = (RecyclerView) findViewById(R.id.monitoring_activity_list_recycler_view);
        searchView = (SearchView) findViewById(R.id.monitoring_activity_list_search_view);

        sortingModeButton = (LinearLayout)
                findViewById(R.id.monitoring_activity_list_sorting_mode_button);
    }

    private void initListeners() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackButtonClick();
            }
        });

        sortingModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSortingModeButtonClick();
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

    private void onBackButtonClick() {
        NavUtils.navigateUpFromSameTask(this);
    }

    private void onSortingModeButtonClick() {
        PopupMenu menu = new PopupMenu(this, sortingModeButton);
        final MenuInflater inflater = menu.getMenuInflater();
        inflater.inflate(R.menu.menu_monitoring_sorting_mode, menu.getMenu());

        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.monitoring_sorting_mode_alphabet:
                        adapter.sortByAlphabet();
                        adapter.notifyDataSetChanged();
                        break;
                    case R.id.monitoring_sorting_mode_status:
                        adapter.sortByStatus();
                        adapter.notifyDataSetChanged();
                        break;
                }

                return false;
            }
        });

        menu.show();
    }
}
