package kgk.beacon.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import kgk.beacon.util.AppController;

public class Signal implements Parcelable {

    private static final String TAG = Signal.class.getSimpleName();

    private long deviceId;
    private int mode;
    private double latitude;
    private double longitude;
    private long date; // Unix seconds
    private long actisDate; // Unix seconds
    private double voltage;  // Volts
    private double balance; // Russian roubles
    private int satellites;
    private int charge; // Percentage
    private int speed;  // km/h
    private int direction;
    private int temperature; // Celsius

    private int mcc;
    private int mnc;
    private String cellId;
    private String lac;

    ////

    public Signal() {}

    public long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public double getLatitude() {
        int roundedLatitude = (int) (latitude * 1E6);
        return roundedLatitude * 1E-6;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        int roundedLongitude = (int) (longitude * 1E6);
        return roundedLongitude * 1E-6;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getActisDate() {
        return actisDate;
    }

    public void setActisDate(long actisDate) {
        this.actisDate = actisDate;
    }

    public double getVoltage() {
        return voltage;
    }

    public void setVoltage(double voltage) {
        this.voltage = voltage;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public int getSatellites() {
        return satellites;
    }

    public void setSatellites(int satellites) {
        this.satellites = satellites;
    }

    public int getCharge() {
        return charge;
    }

    public void setCharge(int charge) {
        this.charge = charge;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getMcc() {
        return mcc;
    }

    public void setMcc(int mcc) {
        this.mcc = mcc;
    }

    public int getMnc() {
        return mnc;
    }

    public void setMnc(int mnc) {
        this.mnc = mnc;
    }

    public String getCellId() {
        return cellId;
    }

    public void setCellId(String cellId) {
        this.cellId = cellId;
    }

    public String getLac() {
        return lac;
    }

    public void setLac(String lac) {
        this.lac = lac;
    }

    public static Signal signalFromJsonForLastState(JSONObject signalJson) {
        Signal signal = new Signal();

        try {
            String params = signalJson.getString("params_json");
            JSONObject rawParamsJson = new JSONObject(params);
            JSONObject rawParamsJsonKey769 = rawParamsJson.getJSONObject("769");
            JSONObject paramsJson = rawParamsJsonKey769.getJSONObject("0");
            signal.setDeviceId(AppController.getInstance().getActiveDeviceId());
            signal.setMode(0);
            signal.setLatitude(signalJson.getDouble("lat"));
            signal.setLongitude(signalJson.getDouble("lng"));
            signal.setDate(signalJson.getLong("server_date"));
            signal.setVoltage(5);
            signal.setBalance(paramsJson.getInt("SIM_BALANCE") / 1000);
            signal.setSatellites(signalJson.getInt("sat"));

            if (paramsJson.getInt("BAT") < 200) {
                signal.setCharge(0);
            } else {
                signal.setCharge(300 - paramsJson.getInt("BAT"));
            }

            signal.setSpeed((int) signalJson.getDouble("speed"));
            signal.setDirection(signalJson.getInt("az"));
            signal.setTemperature(paramsJson.getInt("TEMP"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return signal;
    }

    public static Signal signalFromJson(JSONObject signalJson) {
        Signal signal = new Signal();

        try {
            String params = signalJson.getString("params_json");
            JSONObject rawParamsJson = new JSONObject(params);
            JSONObject paramsJson = rawParamsJson.getJSONObject("0");
            signal.setDeviceId(AppController.getInstance().getActiveDeviceId());
            signal.setMode(0);
            signal.setLatitude(signalJson.getDouble("lat"));
            signal.setLongitude(signalJson.getDouble("lng"));
            signal.setDate(signalJson.getLong("server_date"));
            signal.setActisDate(signalJson.getLong("packet_date"));
            double voltageFromPacket = paramsJson.getInt("BAT");
            signal.setVoltage(voltageFromPacket / 100.0);

            signal.setBalance(paramsJson.getInt("SIM_BALANCE") / 1000);
            signal.setSatellites(signalJson.getInt("sat"));

            if (paramsJson.getInt("BAT") < 200) {
                signal.setCharge(0);
            } else {
                signal.setCharge(paramsJson.getInt("BAT") - 200);
            }

            signal.setSpeed((int) signalJson.getDouble("speed"));
            signal.setDirection(signalJson.getInt("az"));
            signal.setTemperature(paramsJson.getInt("TEMP"));

            try {
                signal.setMcc(paramsJson.getInt("MCC"));
                signal.setMnc(paramsJson.getInt("MNC"));
                signal.setCellId(paramsJson.getString("CID"));
                signal.setLac(paramsJson.getString("LAC"));
            } catch (JSONException je) {
                je.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return signal;
    }

    @Override
    public String toString() {
        String date = new Date(this.date * 1000).toString();
        return "Signal:  "
                + "deviceId - " + deviceId + " | "
                + "mode - " + mode + " | "
                + "latitude - " + latitude + " | "
                + "longitude - " + longitude + " | "
                + "date - " + date + " | "
                + "actis date - " + actisDate + " | "
                + "voltage - " + voltage + " | "
                + "balance - " + balance + " | "
                + "satellites - " + satellites + " | "
                + "charge - " + charge + " | "
                + "speed - " + speed + " | "
                + "direction - " + direction + " | "
                + "temperature - " + temperature;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(deviceId);
        dest.writeInt(mode);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeLong(date);
        dest.writeDouble(voltage);
        dest.writeDouble(balance);
        dest.writeInt(satellites);
        dest.writeInt(charge);
        dest.writeInt(speed);
        dest.writeInt(direction);
        dest.writeInt(temperature);
    }

    public static final Parcelable.Creator<Signal> CREATOR =
            new Parcelable.Creator<Signal>() {

                public Signal createFromParcel(Parcel in) {
                    return new Signal(in);
                }

                public Signal[] newArray(int size) {
                    return new Signal[size];
                }
            };

    private Signal(Parcel in) {
        deviceId = in.readLong();
        mode = in.readInt();
        latitude = in.readDouble();
        longitude = in.readDouble();
        date = in.readLong();
        voltage = in.readDouble();
        balance = in.readDouble();
        satellites = in.readInt();
        charge = in.readInt();
        speed = in.readInt();
        direction = in.readInt();
        temperature = in.readInt();
    }
}
