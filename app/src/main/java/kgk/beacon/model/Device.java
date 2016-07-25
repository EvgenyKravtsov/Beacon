package kgk.beacon.model;

import java.util.ArrayList;

/** Абстракция устройства КГК */
public class Device {

    private String id;
    private String model;
    private String civilModel;
    private String mark;
    private String stateNumber;
    private String type;
    private int typeInt;
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

    public String getCivilModel() {
        return civilModel;
    }

    public void setCivilModel(String civilModel) {
        this.civilModel = civilModel;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getStateNumber() {
        return stateNumber;
    }

    public void setStateNumber(String stateNumber) {
        this.stateNumber = stateNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getTypeInt() {
        return typeInt;
    }

    public void setTypeInt(int typeInt) {
        this.typeInt = typeInt;
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
                + "mark - " + mark
                + "state number - " + stateNumber
                + "type - " + type;
    }
}
