package kgk.beacon.monitoring.presentation.adapter.eventlistadapter;

import com.bignerdranch.expandablerecyclerview.Model.ParentObject;

import java.util.List;

public class EventsListObject implements ParentObject {

    private final long timestamp;
    private List<Object> childList;

    ////

    public EventsListObject(long timestamp) {
        this.timestamp = timestamp;
    }

    ////

    public long getTimestamp() {
        return timestamp;
    }

    ////

    @Override
    public List<Object> getChildObjectList() {
        return null;
    }

    @Override
    public void setChildObjectList(List<Object> list) {

    }
}
