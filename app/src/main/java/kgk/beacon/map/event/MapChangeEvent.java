package kgk.beacon.map.event;

/**
 * Абстракия события смены типа используемой карты
 */
public class MapChangeEvent {

    private String preferredMap;

    public String getPreferredMap() {
        return preferredMap;
    }

    public void setPreferredMap(String preferredMap) {
        this.preferredMap = preferredMap;
    }
}
