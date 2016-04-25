package kgk.beacon.view.actis;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kgk.beacon.R;

/**
 * Экран выбора временного периода
 */
public class DatePickerActivity extends SingleFragmentActivityDeviceRelated {

    @Bind(R.id.toolbar_title) TextView toolbarTitle;

    ////

    @Override
    protected Fragment createFragment() {
        return new DatePickerFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        toolbarTitle.setText(getString(R.string.select_period_button_label));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @OnClick(R.id.helpToolbarButton)
    public void onClickHelpToolbarButton(View view) {
        Intent helpScreenIntent = new Intent(this, HelpActivity.class);
        helpScreenIntent.putExtra(HelpActivity.KEY_SCREEN_NAME, HelpActivity.PERIOD_SCREEN);
        startActivity(helpScreenIntent);
    }
}
