package kgk.beacon.monitoring.presentation.view;

import java.util.List;

import kgk.beacon.monitoring.domain.model.MonitoringEntityGroup;

public interface MonitoringGroupListView {

    void showMonitoringEntityGroups(
            List<MonitoringEntityGroup> groups,
            MonitoringEntityGroup activeGroup);

    void toggleDownloadDataProgressDialog(boolean status);
}
