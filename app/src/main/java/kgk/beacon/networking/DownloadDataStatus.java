package kgk.beacon.networking;

/**
 * Перечень состояний обработки http-запроса
 */
public enum DownloadDataStatus {

    Started, Success, Error, noInternetConnection, DeviceNotFound
}
