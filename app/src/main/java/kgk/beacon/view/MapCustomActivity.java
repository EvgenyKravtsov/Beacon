package kgk.beacon.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

public class MapCustomActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new MapCustomFragment();
    }

    //// Activity Methods

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
}
