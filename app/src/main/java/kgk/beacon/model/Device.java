package kgk.beacon.model;

import java.util.ArrayList;

/** Абстракция устройства КГК */
public class Device {

    private String id;
    private String model;
    private String type;
    private ArrayList<String> groups = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<String> getGroups() {
        return groups;
    }

    public void setGroups(ArrayList<String> groups) {
        this.groups = groups;
    }

    @Override
    public String toString() {
        return  "id - " + id + "\n"
                + "model - " + model
                + "type - " + type;
    }
}
