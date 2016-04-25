package kgk.beacon.map.event;

import kgk.beacon.map.Map;

/**
 * Абстракция события готовности объекта карты для взаимодействия
 */
public class MapReadyForUseEvent {

    private Map map;

    public MapReadyForUseEvent(Map map) {
        this.map = map;
    }

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }
}
