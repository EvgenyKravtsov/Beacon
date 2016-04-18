package kgk.beacon.view.actis;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import kgk.beacon.R;


public class HistoryActivity extends SingleFragmentActivityDeviceRelated {

    @Bind(R.id.toolbar_title) TextView toolbarTitle;

    @Override
    protected Fragment createFragment() {
        return new HistoryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        toolbarTitle.setText(getString(R.string.history_action_bar_label));
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

    @OnClick(R.id.helpToolbarButton)
    public void onClickHelpToolbarButton(View view) {
        Intent helpScreenIntent = new Intent(this, HelpActivity.class);
        helpScreenIntent.putExtra(HelpActivity.KEY_SCREEN_NAME, HelpActivity.HISTORY_SCREEN);
        startActivity(helpScreenIntent);
    }
}
