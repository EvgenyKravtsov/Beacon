package kgk.beacon.stores;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Событие, содержащее пакеты для построения детального отчета
 */
public class PacketsForDetailReportEvent {

    private ArrayList<LatLng> coordinatesForMoving;
    private ArrayList<LatLng> coordinatesForStops;

    public ArrayList<LatLng> getCoordinatesForMoving() {
        return coordinatesForMoving;
    }

    public void setCoordinatesForMoving(ArrayList<LatLng> coordinatesForMoving) {
        this.coordinatesForMoving = coordinatesForMoving;
    }

    public ArrayList<LatLng> getCoordinatesForStops() {
        return coordinatesForStops;
    }

    public void setCoordinatesForStops(ArrayList<LatLng> coordinatesForStops) {
        this.coordinatesForStops = coordinatesForStops;
    }
}
