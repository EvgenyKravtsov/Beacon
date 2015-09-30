package kgk.beacon.model;

public class Device {

    private String id;
    private String model;

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

    @Override
    public String toString() {
        return  "id - " + id + "\n"
                + "model - " + model;
    }
}
