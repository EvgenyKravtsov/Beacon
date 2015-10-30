package kgk.beacon.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import kgk.beacon.R;
import kgk.beacon.actions.ActionCreator;
import kgk.beacon.dispatcher.Dispatcher;
import kgk.beacon.model.Signal;
import kgk.beacon.stores.SignalStore;
import kgk.beacon.util.AppController;
import kgk.beacon.util.DateFormatter;


public class HistoryFragment extends Fragment {

    public static final String TAG = HistoryFragment.class.getSimpleName();

    public static final int REQUEST_DATE_PICKER = 5;
    public static final int DEFAULT_NUMBER_OF_SIGNALS = 10;

    private HistoryExpandableListViewAdapter adapter;

    private Dispatcher dispatcher;
    private ActionCreator actionCreator;
    private SignalStore signalStore;

    @Bind(R.id.fragmentHistory_historyListView) ExpandableListView historyListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        ButterKnife.bind(this, view);
        initFluxDependencies();
        updateExpandableListViewAdapter();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        dispatcher.register(this);
        actionCreator.refreshSignalsDisplayed();
    }

    @Override
    public void onPause() {
        super.onPause();
        dispatcher.unregister(this);
    }

    public void onEventMainThread(SignalStore.SignalStoreChangeEvent event) {
        updateExpandableListViewAdapter();
    }

    private void updateExpandableListViewAdapter() {
        Map<String, Object> dataForAdapter = sortForExpandableList((ArrayList<Signal>) signalStore.getSignalsDisplayed());

        ArrayList<Map<String, String>> groupDataList = (ArrayList<Map<String, String>>) dataForAdapter.get("groupDataList");
        String[] groupFrom = (String[]) dataForAdapter.get("groupFrom");
        int[] groupTo = (int[]) dataForAdapter.get("groupTo");
        ArrayList<ArrayList<Map<String, Signal>>> childDataList = (ArrayList<ArrayList<Map<String, Signal>>>) dataForAdapter.get("childDataList");
        String[] childFrom = (String[]) dataForAdapter.get("childFrom");
        int[] childTo = (int[]) dataForAdapter.get("childTo");

        adapter = new HistoryExpandableListViewAdapter(getActivity(),
                groupDataList, android.R.layout.simple_expandable_list_item_1,
                groupFrom, groupTo,
                childDataList, R.layout.list_item_history,
                childFrom, childTo);

        historyListView.setAdapter(adapter);
        historyListView.setOnChildClickListener(adapter);
    }

    private void initFluxDependencies() {
        dispatcher = Dispatcher.getInstance(EventBus.getDefault());
        actionCreator = ActionCreator.getInstance(dispatcher);
        signalStore = SignalStore.getInstance(dispatcher);
    }

    private Map<String, Object> sortForExpandableList(ArrayList<Signal> signals) {
        ArrayList<Date> datesFromSignals = new ArrayList<>();
        for (Signal signal : signals) {
            datesFromSignals.add(new Date(signal.getDate() * 1000));
        }

        Date[] groupsArray = DateFormatter.filterForUniqueDays(datesFromSignals);

        Map<String, String> map;
        Map<String, Signal> signalMap;
        ArrayList<Map<String, String>> groupDataList = new ArrayList<>();
        for (Date date : groupsArray) {
            map = new HashMap<>();
            map.put("groupName", DateFormatter.formatDate(date));
            groupDataList.add(map);
        }

        String[] groupFrom = new String[] {"groupName"};
        int[] groupTo = new int[] {android.R.id.text1};

        ArrayList<ArrayList<Map<String, Signal>>> childDataList = new ArrayList<>();

        for (Date date : groupsArray) {
            ArrayList<Map<String, Signal>> childDataItemList = new ArrayList<>();

            Signal[] signalArray = filterSignalsForDate(date, signals);

            for (Signal signal : signalArray) {
                signalMap = new HashMap<>();
                signalMap.put("signal", signal);
                childDataItemList.add(signalMap);
            }

            childDataList.add(childDataItemList);
        }

        String[] childFrom = new String[] {"signal"};
        int childTo[] = new int[] {android.R.id.text1};

        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("groupDataList", groupDataList);
        returnMap.put("groupFrom", groupFrom);
        returnMap.put("groupTo", groupTo);
        returnMap.put("childDataList", childDataList);
        returnMap.put("childFrom", childFrom);
        returnMap.put("childTo", childTo);

        return  returnMap;
    }

    private Signal[] filterSignalsForDate(Date date, ArrayList<Signal> signals) {
        ArrayList<Signal> result = new ArrayList<>();

        Calendar targetDate = Calendar.getInstance();
        targetDate.setTime(date);

        Calendar signalDate = Calendar.getInstance();

        int targetYear = targetDate.get(Calendar.YEAR);
        int targetMonth = targetDate.get(Calendar.MONTH);
        int targetDay = targetDate.get(Calendar.DAY_OF_MONTH);

        for (Signal signal : signals) {
            signalDate.setTime(new Date(signal.getDate() * 1000));

            int signalYear = signalDate.get(Calendar.YEAR);
            int signalMonth = signalDate.get(Calendar.MONTH);
            int signalDay = signalDate.get(Calendar.DAY_OF_MONTH);

            if (targetYear == signalYear  &&
                    targetMonth == signalMonth &&
                    targetDay == signalDay) {
                result.add(signal);
            }
        }

        return result.toArray(new Signal[result.size()]);
    }

    @OnClick(R.id.fragmentHistory_selectPeriodButton)
    public void onPressSelectPeriodButton(View view) {
        Intent datePickerIntent = new Intent(getActivity(), DatePickerActivity.class);
        getActivity().startActivityForResult(datePickerIntent, REQUEST_DATE_PICKER);
    }

    @OnClick(R.id.fragmentHistory_trackButton)
    public void onPressTrackButton(View view) {
        if (signalStore.getSignalsDisplayed().size() > 0) {
            Intent intent = new Intent(getActivity(), PathActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(getActivity(), R.string.no_signals_toast, Toast.LENGTH_SHORT).show();
        }
    }

    class HistoryExpandableListViewAdapter extends SimpleExpandableListAdapter
            implements ExpandableListView.OnChildClickListener {

        private ArrayList<Map<String, String>> groupData;
        private ArrayList<ArrayList<Map<String, Signal>>> childData;

        public HistoryExpandableListViewAdapter(Context context,
                                                List<? extends Map<String, ?>> groupData,
                                                int groupLayout, String[] groupFrom,
                                                int[] groupTo,
                                                List<? extends List<? extends Map<String, ?>>> childData,
                                                int childLayout,
                                                String[] childFrom,
                                                int[] childTo) {
            super(context, groupData, groupLayout, groupFrom,
                    groupTo, childData, childLayout, childFrom, childTo);

            this.groupData = (ArrayList<Map<String, String>>) groupData;
            this.childData = (ArrayList<ArrayList<Map<String, Signal>>>) childData;
        }

        @Override
        public Signal getChild(int groupPosition, int childPosition) {
            return childData.get(groupPosition).get(childPosition).get("signal");
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ViewHolderHistoryFragment viewHolder;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) HistoryFragment.this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_item_history, parent, false);
                viewHolder = new ViewHolderHistoryFragment(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolderHistoryFragment) convertView.getTag();
            }

            Signal signal = getChild(groupPosition, childPosition);
            viewHolder.time.setText(DateFormatter.formatTime(new Date(signal.getDate() * 1000)));
            viewHolder.type.setText(String.valueOf(AppController.getInstance().getActiveDeviceId()));
            viewHolder.satellites.setText(String.valueOf(signal.getSatellites())
                    + getString(R.string.list_item_satellites_sign));
            viewHolder.voltage.setText(String.valueOf(signal.getVoltage())
                    + getString(R.string.list_item_voltage_sign));
            // viewHolder.charge.setText(String.valueOf(signal.getCharge()) + "%");
            viewHolder.speed.setText(String.valueOf(signal.getSpeed())
                    + getString(R.string.list_item_speed_sign));
            viewHolder.temperature.setText(String.valueOf(signal.getTemperature())
                    + getString(R.string.list_item_temperature_sign));
            viewHolder.balance.setText(String.valueOf(signal.getBalance())
                    + getString(R.string.list_item_balance_sign));

            return convertView;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                                 ViewGroup parent) {
            View groupView = super.getGroupView(groupPosition, isExpanded,
                    convertView, parent);
            groupView.setBackgroundColor(Color.parseColor("#aafdffff"));
            return groupView;
        }

        @Override
        public boolean onChildClick(ExpandableListView parent, View view,
                                    int groupPosition, int childPosition, long id) {

            Signal signal = getChild(groupPosition, childPosition);
            Intent mapIntent = new Intent(getActivity(), MapCustomActivity.class);
            mapIntent.putExtra(MapCustomFragment.EXTRA_SIGNAL, signal);
            startActivity(mapIntent);

            return true;
        }
    }

    static class ViewHolderHistoryFragment {

        @Bind(R.id.listItemHistory_timeTextView) TextView time;
        @Bind(R.id.listItemHistory_typeTextView) TextView type;
        @Bind(R.id.listItemHistory_satellitesCountTextView) TextView satellites;
        @Bind(R.id.listItemHistory_voltageCountTextView) TextView voltage;
        // @Bind(R.id.listItemHistory_chargeCountTextView) TextView charge;
        @Bind(R.id.listItemHistory_speedCountTextView) TextView speed;
        @Bind(R.id.listItemHistory_temperatureCountTextView) TextView temperature;
        @Bind(R.id.listItemHistory_balanceTextView) TextView balance;

        public ViewHolderHistoryFragment(View view) {
            ButterKnife.bind(this, view);
        }
    }
}







































