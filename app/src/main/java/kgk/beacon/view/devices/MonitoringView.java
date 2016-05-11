package kgk.beacon.view.devices;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import kgk.beacon.map.Map;
import kgk.beacon.model.UniversalPacket;

public interface MonitoringView {

    void setMap(Map map);

    void addCustomMarkerPoint(int direction, double latitude, double longitude);

    void moveCamera(double latitude, double longitude, int zoom);

    void changeMap(String map);

    boolean isTrackShown();

    void setTrackShown(boolean trackStatus);

    void clearMap();

    void drawPath(List<UniversalPacket> packets);

    void addPolyline(ArrayList<LatLng> coordinates);

    void addStopMarker(double latitude, double longitude);

    void showDownloadDataProgressDialog();

    void dismissDownloadProgressDialog();

    void showToastMessage(String message);
}
