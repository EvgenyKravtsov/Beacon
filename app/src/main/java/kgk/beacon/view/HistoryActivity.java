package kgk.beacon.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import java.util.Date;

import de.greenrobot.event.EventBus;


public class HistoryActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new HistoryFragment();
    }

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
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == HistoryFragment.REQUEST_DATE_PICKER) {
            if (resultCode == RESULT_OK) {
                Date fromDate = (Date) data.getSerializableExtra(DatePickerFragment.FROM_DATE);
                Date toDate = (Date) data.getSerializableExtra(DatePickerFragment.TO_DATE);
                EventBus.getDefault().post(new FromAndToDatesEvent(fromDate, toDate));
            }
        }
    }
}
