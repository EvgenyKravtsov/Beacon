package kgk.beacon.networking.event;

public class SearchModeStatusEvent {

    private boolean status;

    public SearchModeStatusEvent(boolean status) {
        this.status = status;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
