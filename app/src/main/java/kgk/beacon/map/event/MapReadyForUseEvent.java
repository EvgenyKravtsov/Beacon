package kgk.beacon.map.event;

import kgk.beacon.map.Map;

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
