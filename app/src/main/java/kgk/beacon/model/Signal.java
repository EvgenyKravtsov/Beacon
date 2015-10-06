package kgk.beacon.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Signal implements Parcelable {

    private long deviceId;
    private int mode;
    private double latitude;
    private double longitude;
    private long date; // Unix seconds
    private double voltage;  // Volts
    private double balance; // Russian roubles
    private int satellites;
    private int charge; // Percentage
    private int speed;  // km/h
    private int direction;
    private int temperature; // Celsius

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
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
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

    @Override
    public String toString() {
        String date = new Date(this.date * 1000).toString();
        return "Signal:  "
                + "deviceId - " + deviceId + " | "
                + "mode - " + mode + " | "
                + "latitude - " + latitude + " | "
                + "longitude - " + longitude + " | "
                + "date - " + date + " | "
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
