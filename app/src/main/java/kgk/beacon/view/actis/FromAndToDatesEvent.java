package kgk.beacon.view.actis;

import java.util.Date;

public class FromAndToDatesEvent {

    private Date fromDate;
    private Date toDate;

    public FromAndToDatesEvent(Date fromDate, Date toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public Date getToDate() {
        return toDate;
    }
}
