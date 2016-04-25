package kgk.beacon.view.general;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import kgk.beacon.R;
import kgk.beacon.view.actis.SingleFragmentActivity;

/**
 * Экран со списком устройств
 */
public class DeviceListActivity extends SingleFragmentActivity {

    @Bind(R.id.helpToolbarButton) ImageButton helpToolbarButton;
    @Bind(R.id.toolbar_title) TextView toolbarTitle;

    ////

    @Override
    protected Fragment createFragment() {
        return new DeviceListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        prepareToolbar();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    ////

    private void prepareToolbar() {
        helpToolbarButton.setVisibility(View.GONE);
        toolbarTitle.setText(getString(R.string.device_list_activity_action_bar_label));
    }
}
