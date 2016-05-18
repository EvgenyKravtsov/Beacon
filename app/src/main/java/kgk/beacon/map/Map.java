package kgk.beacon.map;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import kgk.beacon.model.Signal;

/**
 * Абстракный интрефейс для взимодействия с картой, вне зависимости от ее типа
 */
public interface Map {

    int DEFAULT_CIRCLE_ZONE_RADIUS = 800; // meters

    ////

    void setOnMapClickListener(MapClickListener listener);

    void setOnMarkerClickListener(MarkerClickListener listener);

    /** Добавить кастомный маркер на карту */
    void addCustomMarkerPoint(int direction, double latitude, double longitude);

    /** Добавить маркер с подробной информацией о сигнале */
    void addSignalInfoMarker(Signal signal);

    /** Удалить маркер с подробной информацией о сигнале */
    void removeSignalInfoMarker();

    /** Добавить маркер остановки */
    void addStopMarker(double latitude, double longitude);

    /** Добавить полилинию */
    void addPolyline(ArrayList<LatLng> coordinates);

    /** Переместить камеру */
    void moveCamera(double latitude, double longitude, int zoom);

    /** Добавить круговую зону */
    void addCircleZone(double latitude, double longitude, int radius); // meters

    /** Очистить карту от объектов */
    void clear();

    /** Активировать кнопку управления полноэкранным режимом карты */
    void turnOnFullscreenButton();

    int getCurrentZoom();
}
