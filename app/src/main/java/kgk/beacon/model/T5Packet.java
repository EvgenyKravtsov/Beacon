package kgk.beacon.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/** Абстракция пакета с устройства типа Т5 */
public class T5Packet {

    private long idTerm;
    private long packetDate;
    private double latitude;
    private double longitude;
    private int direction;
    private double speed;
    private int satellites;
    private int csq;

    public long getIdTerm() {
        return idTerm;
    }

    public void setIdTerm(long idTerm) {
        this.idTerm = idTerm;
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

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
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

    public static T5Packet fromJson(JSONObject jsonPacket) {
        T5Packet packet = new T5Packet();

        try {
            packet.idTerm = jsonPacket.getLong("id_term");
            packet.packetDate = jsonPacket.getLong("packet_date");
            packet.latitude = jsonPacket.getDouble("lat");
            packet.longitude = jsonPacket.getDouble("lng");
            packet.direction = jsonPacket.getInt("az");
            packet.speed = jsonPacket.getDouble("speed");
            packet.satellites = jsonPacket.getInt("sat");
            packet.csq = jsonPacket.getInt("csq");
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
                + "idTerm - " + idTerm + " | "
                + "latitude - " + latitude + " | "
                + "longitude - " + longitude + " | "
                + "satellites - " + satellites + " | "
                + "speed - " + speed + " | "
                + "direction - " + direction + " | "
                + "csq - " + csq;
    }
}
