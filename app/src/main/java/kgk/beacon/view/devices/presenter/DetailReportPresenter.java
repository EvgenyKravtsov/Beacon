package kgk.beacon.view.devices.presenter;

import java.util.Date;
import java.util.TimeZone;

public class DetailReportPresenter {

    private static final String TAG = DetailReportPresenter.class.getSimpleName();

    ////

    public int calculateOffsetUtc() {
        TimeZone timeZone = TimeZone.getDefault();
        Date currentDate = new Date();
        return timeZone.getOffset(currentDate.getTime()) / 1000 / 60;
    }
}
