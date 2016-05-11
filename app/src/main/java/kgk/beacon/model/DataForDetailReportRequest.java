package kgk.beacon.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class DataForDetailReportRequest implements Serializable {

    public static final String DATAKEY_DATE_FROM = "datakey_date_from";
    public static final String DATAKEY_DATE_TO = "datakey_date_to";
    public static final String DATAKEY_DELTA = "datakey_delta";
    public static final String DATAKEY_OFFSET_UTC = "datakey_offset_utc";

    private Map<String, String> data;

    ////

    public DataForDetailReportRequest() {
        data = new HashMap<>();
    }

    ////

    public Map<String, String> getDataMap() {
        return data;
    }

    ////

    @Override
    public String toString() {
        return "FROM DATE - " + data.get(DATAKEY_DATE_FROM) + " | "
                + "TO DATE - " + data.get(DATAKEY_DATE_TO) + " | "
                + "DELTA - " + data.get(DATAKEY_DELTA) + " | "
                + "OFFCET UTC - " + data.get(DATAKEY_OFFSET_UTC);
    }
}
