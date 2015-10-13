package kgk.beacon.util;

public class DownloadUrlReceivedEvent {
    private String downloadUrl;

    public DownloadUrlReceivedEvent(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }
}
