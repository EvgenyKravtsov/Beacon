package kgk.beacon.networking.event;

import kgk.beacon.networking.DownloadDataStatus;

public class DownloadDataInProgressEvent {

    private DownloadDataStatus status;

    public DownloadDataStatus getStatus() {
        return status;
    }

    public void setStatus(DownloadDataStatus status) {
        this.status = status;
    }
}