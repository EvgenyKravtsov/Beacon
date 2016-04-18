package kgk.beacon.view.generator.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import kgk.beacon.R;
import kgk.beacon.view.generator.adapter.GeneratorHistoryListAdapter;

public class HistoryActivity extends AppCompatActivity {

    private static final String TAG = HistoryActivity.class.getSimpleName();

    @Bind(R.id.activityGeneratorHistory_toolbar) Toolbar toolbar;
    @Bind(R.id.activityGeneratorHistory_list) ExpandableListView listView;

    private GeneratorHistoryListAdapter adapter;

    // TODO Delete test code
    private List<String> listHeaders;
    private HashMap<String, List<String>> listChildren;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generator_history);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        populateHistoryList();
    }

    private void populateHistoryList() {
        prepareListData();
        adapter = new GeneratorHistoryListAdapter(this, listHeaders, listChildren);
        listView.setAdapter(adapter);
    }

    private void prepareListData() {
        listHeaders = new ArrayList<String>();
        listChildren = new HashMap<String, List<String>>();

        listHeaders.add("Top 250");
        listHeaders.add("Now Showing");
        listHeaders.add("Coming Soon..");

        List<String> top250 = new ArrayList<String>();
        top250.add("The Shawshank Redemption");
        top250.add("The Godfather");
        top250.add("The Godfather: Part II");
        top250.add("Pulp Fiction");
        top250.add("The Good, the Bad and the Ugly");
        top250.add("The Dark Knight");
        top250.add("12 Angry Men");

        List<String> nowShowing = new ArrayList<String>();
        nowShowing.add("The Conjuring");
        nowShowing.add("Despicable Me 2");
        nowShowing.add("Turbo");
        nowShowing.add("Grown Ups 2");
        nowShowing.add("Red 2");
        nowShowing.add("The Wolverine");

        List<String> comingSoon = new ArrayList<String>();
        comingSoon.add("2 Guns");
        comingSoon.add("The Smurfs 2");
        comingSoon.add("The Spectacular Now");
        comingSoon.add("The Canyons");
        comingSoon.add("Europa Report");

        listChildren.put(listHeaders.get(0), top250);
        listChildren.put(listHeaders.get(1), nowShowing);
        listChildren.put(listHeaders.get(2), comingSoon);
    }
}
