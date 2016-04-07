package kgk.beacon.view.actis;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import kgk.beacon.R;
import kgk.beacon.dispatcher.Dispatcher;
import kgk.beacon.stores.ActisStore;

public abstract class SingleFragmentActivityDeviceRelated extends SingleFragmentActivity {

    @Bind(R.id.batteryView) TextView batteryView;
    @Bind(R.id.helpToolbarButton) ImageButton helpToolbarButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateBatteryView(ActisStore.getInstance(Dispatcher.getInstance(EventBus.getDefault()))
                .getSignal().getCharge());
        updateToolbarButtons();
    }

    ////

    private void updateBatteryView(int charge) {
        if (batteryView != null) {
            batteryView.setVisibility(View.VISIBLE);

            if (charge >= 70) {
                batteryView.setBackgroundDrawable(getResources().getDrawable(R.drawable.battery_icon_high));
                batteryView.setTextColor(getResources().getColor(R.color.actis_app_green_accent));
            } else if (charge >= 35 && charge < 70) {
                batteryView.setBackgroundDrawable(getResources().getDrawable(R.drawable.battery_icon_average));
                batteryView.setTextColor(getResources().getColor(R.color.actis_app_yellow_accent));
            } else {
                batteryView.setBackgroundDrawable(getResources().getDrawable(R.drawable.battery_icon_low));
                batteryView.setTextColor(getResources().getColor(R.color.actis_app_red_accent));
            }

            batteryView.setText(charge + "%");
        }
    }

    private void updateToolbarButtons() {
        helpToolbarButton.setVisibility(View.VISIBLE);
    }
}
