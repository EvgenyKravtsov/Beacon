package kgk.beacon.view;

import android.support.v4.app.Fragment;

public class DeviceListActivity extends SingleFragmentActivity {

    // TODO Add back button

    @Override
    protected Fragment createFragment() {
        return new DeviceListFragment();
    }
}
