package kgk.beacon.view;

import android.support.v4.app.Fragment;

public class DeviceListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new DeviceListFragment();
    }
}
