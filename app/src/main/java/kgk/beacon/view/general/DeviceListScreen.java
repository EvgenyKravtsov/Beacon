package kgk.beacon.view.general;

import kgk.beacon.model.Device;

/**
 * Интерфейс взаимодействия с экраном списка усторйств
 */
public interface DeviceListScreen {

    void onListItemClick(String deviceInfo, Device chosenDevice);
}
