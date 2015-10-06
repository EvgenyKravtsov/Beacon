package kgk.beacon.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import kgk.beacon.R;
import kgk.beacon.model.Signal;

public class MapCustomActivity extends AppCompatActivity {

    private static final String TAG = MapCustomActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_fragment);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Signal signal = getIntent().getParcelableExtra(MapCustomFragment.EXTRA_SIGNAL);
        setFragment(MapCustomFragment.newInstance(signal));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setFragment(Fragment fragmentToSet) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragmentContainer);

        if (fragment == null) {
            fragmentManager.beginTransaction()
                    .add(R.id.fragmentContainer, fragmentToSet)
                    .commit();
        }
    }
}
