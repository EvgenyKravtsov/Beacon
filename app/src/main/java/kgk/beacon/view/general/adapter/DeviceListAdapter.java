package kgk.beacon.view.general.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;
import kgk.beacon.R;
import kgk.beacon.dispatcher.Dispatcher;
import kgk.beacon.model.Device;
import kgk.beacon.stores.DeviceStore;
import kgk.beacon.view.general.DeviceListScreen;

/** Платформенный адаптер для отображения списка устройств */
public class DeviceListAdapter extends BaseExpandableListAdapter
        implements ExpandableListView.OnChildClickListener {

    private static final String TAG = DeviceListAdapter.class.getSimpleName();

    private Context context;
    private DeviceListScreen screen;
    private List<String> listHeaders;
    private HashMap<String, List<String>> listChildren;

    public DeviceListAdapter(Context context, DeviceListScreen screen, List<String> listHeaders,
                                    HashMap<String, List<String>> listChildren) {
        this.context = context;
        this.screen = screen;
        this.listHeaders = listHeaders;
        this.listChildren = listChildren;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.listChildren.get(this.listHeaders.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild,
                                        View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_devices, parent, false);
        }

        TextView deviceTextView = (TextView) convertView
                .findViewById(R.id.listItemDevices_deviceTextView);
        TextView idTextView = (TextView) convertView.findViewById(R.id.listItemDevices_id);

        String[] deviceInfo = ((String) getChild(groupPosition, childPosition)).split(" ");
        String info = "";
        String id = "";
        for (int i = 0; i < deviceInfo.length; i++) {
            if (i == deviceInfo.length - 1) {
                id = deviceInfo[i];
            } else {
                info += deviceInfo[i] + " ";
            }
        }

        deviceTextView.setText(info);
        idTextView.setText(id);

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return  this.listChildren.get(this.listHeaders.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listHeaders.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                                    ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.group_list_view_devices, null);
        }

        TextView groupLabel = (TextView) convertView.findViewById(R.id.groupListViewDevicesLabel);
        groupLabel.setText(headerTitle);

        return convertView;
    }

    @Override
    public int getGroupCount() {
        return this.listHeaders.size();
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View view,
                                int groupPosition, int childPosition, long id) {

        Device chosenDevice = new Device();

        String[] deviceInfo = ((String) getChild(groupPosition, childPosition)).split(" ");
        String deviceId = deviceInfo[deviceInfo.length - 1];

        List<Device> devices = DeviceStore
                .getInstance(Dispatcher.getInstance(EventBus.getDefault())).getDevices();
        for (Device device : devices) {
            if (device.getId().equals(deviceId)) {
                chosenDevice = device;
            }
        }

        screen.onListItemClick((String) getChild(groupPosition, childPosition), chosenDevice);
        return  true;
    }
}
