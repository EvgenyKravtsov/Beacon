package kgk.beacon.map;

import android.support.v4.app.Fragment;

import com.google.android.gms.maps.model.LatLng;

import kgk.beacon.util.AppController;
import kgk.beacon.view.map.GoogleMapFragment;
import kgk.beacon.view.map.GoogleMapFragmentForActis;
import kgk.beacon.view.map.OSMMapFragment;
import kgk.beacon.view.map.OSMMapFragmentForActis;

/**
 * Класс для управления типами карт
 */
public class MapManager {

    public static String PREFFERED_MAP;

    public static final String OSM_KGK_MAP = "kgk_map";
    public static final String OSM_MAP = "osm_map";
    public static final String SATELLITE_MAP = "satellite_map";
    public static final String HYBRID_MAP = "hybrid_map";
    public static final String GOOGLE_MAP = "google_map";

    private static final String KEY_PREFERRED_MAP = "key_preferred_map";

    private static LatLng cameraCoordinates;
    private static int cameraZoom;

    public MapManager() {
        PREFFERED_MAP = loadPreferredMapName();
    }

    public static LatLng getCameraCoordinates() {
        return cameraCoordinates;
    }

    public static void setCameraCoordinates(LatLng cameraCoordinates) {
        MapManager.cameraCoordinates = cameraCoordinates;
    }

    public static int getCameraZoom() {
        return cameraZoom;
    }

    public static void setCameraZoom(int cameraZoom) {
        MapManager.cameraZoom = cameraZoom;
    }

    /** Сохранить выбранный тип карты в локальное хранилище */
    public void savePreferredMap(String mapName) {
        PREFFERED_MAP = mapName;
        AppController.saveStringValueToSharedPreferences(KEY_PREFERRED_MAP, mapName);
    }

    /** Загрузить выбранный тип карты из локального хранилища */
    public String loadPreferredMapName() {
        String mapName = AppController.loadStringValueFromSharedPreferences(KEY_PREFERRED_MAP);
        return mapName.equals("default") ? OSM_KGK_MAP : mapName;
    }

    /** Возвращает графический контроллер в зависимости от выбранного типа карты */
    public Fragment loadPreferredMapFragment() {
        String mapName = AppController.loadStringValueFromSharedPreferences(KEY_PREFERRED_MAP);

        switch (mapName) {
            case "default":
                return new OSMMapFragment();
            case OSM_KGK_MAP:
                return new OSMMapFragment();
            case OSM_MAP:
                return new OSMMapFragment();
            case SATELLITE_MAP:
                return new GoogleMapFragment();
            case HYBRID_MAP:
                return new GoogleMapFragment();
            case GOOGLE_MAP:
                return new GoogleMapFragment();
            default:
                return new OSMMapFragment();
        }
    }

    /**
     * Возвращает графический контроллер в зависимости от выбранного типа карты, только для экранов,
     * связанных с устройством Actis
     */
    public Fragment loadPreferredMapFragmentForActis() {
        String mapName = AppController.loadStringValueFromSharedPreferences(KEY_PREFERRED_MAP);

        switch (mapName) {
            case "default":
                return new OSMMapFragmentForActis();
            case OSM_KGK_MAP:
                return new OSMMapFragmentForActis();
            case OSM_MAP:
                return new OSMMapFragmentForActis();
            case SATELLITE_MAP:
                return new GoogleMapFragmentForActis();
            case HYBRID_MAP:
                return new GoogleMapFragmentForActis();
            case GOOGLE_MAP:
                return new GoogleMapFragmentForActis();
            default:
                return new OSMMapFragmentForActis();
        }
    }
}
