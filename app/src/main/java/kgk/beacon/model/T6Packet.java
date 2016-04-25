package kgk.beacon.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/** Абстракция пакета с устройства типа Т6 */
public class T6Packet {

    private long deviceId;
    private long packetDate;
    private long serverDate;
    private double latitude;
    private double longitude;
    private int direction;
    private double speed;
    private int satellites;
    private int csq;
    private int ext;

    public long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
    }

    public long getPacketDate() {
        return packetDate;
    }

    public void setPacketDate(long packetDate) {
        this.packetDate = packetDate;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public long getServerDate() {
        return serverDate;
    }

    public void setServerDate(long serverDate) {
        this.serverDate = serverDate;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public int getSatellites() {
        return satellites;
    }

    public void setSatellites(int satellites) {
        this.satellites = satellites;
    }

    public int getCsq() {
        return csq;
    }

    public void setCsq(int csq) {
        this.csq = csq;
    }

    public int getExt() {
        return ext;
    }

    public void setExt(int ext) {
        this.ext = ext;
    }

    public static T6Packet fromJson(JSONObject jsonPacket) {
        T6Packet packet = new T6Packet();

        try {
            packet.deviceId = jsonPacket.getLong("device_id");
            packet.packetDate = jsonPacket.getLong("packet_date");
            packet.serverDate = jsonPacket.getLong("server_date");
            packet.latitude = jsonPacket.getDouble("lat");
            packet.longitude = jsonPacket.getDouble("lng");
            packet.direction = jsonPacket.getInt("az");
            packet.speed = jsonPacket.getDouble("speed");
            packet.satellites = jsonPacket.getInt("sat");
            packet.csq = jsonPacket.getInt("csq");
            packet.ext = jsonPacket.getInt("ext");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return packet;
    }

    @Override
    public String toString() {
        String date = new Date(this.packetDate * 1000).toString();
        return "Signal:  "
                + "date - " + date + " | "
                + "deviceId - " + deviceId + " | "
                + "latitude - " + latitude + " | "
                + "longitude - " + longitude + " | "
                + "satellites - " + satellites + " | "
                + "speed - " + speed + " | "
                + "direction - " + direction + " | "
                + "csq - " + csq + " | "
                + "ext - " + ext;
    }
}
