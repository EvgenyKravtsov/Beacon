package kgk.beacon.monitoring.presentation.presenter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import kgk.beacon.monitoring.DependencyInjection;
import kgk.beacon.monitoring.domain.interactor.GetActiveMonitoringEntity;
import kgk.beacon.monitoring.domain.interactor.GetMonitoringEntities;
import kgk.beacon.monitoring.domain.interactor.GetMonitoringEntityById;
import kgk.beacon.monitoring.domain.interactor.GetMonitroingEntityGroups;
import kgk.beacon.monitoring.domain.interactor.GetRouteReport;
import kgk.beacon.monitoring.domain.interactor.InteractorThreadPool;
import kgk.beacon.monitoring.domain.interactor.SetDefaultMapTypeSetting;
import kgk.beacon.monitoring.domain.interactor.UpdateMonitoringEntities;
import kgk.beacon.monitoring.domain.model.MonitoringEntity;
import kgk.beacon.monitoring.domain.model.MonitoringEntityGroup;
import kgk.beacon.monitoring.domain.model.MonitoringManager;
import kgk.beacon.monitoring.domain.model.routereport.RouteReport;
import kgk.beacon.monitoring.domain.model.routereport.RouteReportParametersPeriodSeparated;
import kgk.beacon.monitoring.presentation.model.MapType;
import kgk.beacon.monitoring.presentation.view.MapView;
import kgk.beacon.util.AppController;

public class MapViewPresenter implements
        GetMonitoringEntities.Listener,
        GetActiveMonitoringEntity.Listener,
        GetMonitoringEntityById.Listener,
        GetMonitroingEntityGroups.Listener,
        UpdateMonitoringEntities.Listener,
        GetRouteReport.Listener {

    private static final int MONITORING_UPDATE_FREQUENCY = 10; // Seconds

    private MapView view;
    private boolean updateThreadStatus;

    ////

    public MapViewPresenter(MapView mapView) {
        this.view = mapView;
    }

    ////

    public void unbindView() {
        this.view = null;
    }

    public void requestMonitoringEntities() {
        GetMonitoringEntities interactor =
                new GetMonitoringEntities(DependencyInjection.provideMonitoringManager());
        interactor.setListener(this);
        InteractorThreadPool.getInstance().execute(interactor);
    }

    public void requestActiveMonitoringEntity() {
        GetActiveMonitoringEntity interactor = new GetActiveMonitoringEntity();
        interactor.setListener(this);
        InteractorThreadPool.getInstance().execute(interactor);
    }

    public void requestMonitoringEntityById(long id) {
        GetMonitoringEntityById interactor = new GetMonitoringEntityById(
                id,
                DependencyInjection.provideMonitoringManager()
        );

        interactor.setListener(this);
        InteractorThreadPool.getInstance().execute(interactor);
    }

    public void requestMonitoringEntityGroupsCount() {
        GetMonitroingEntityGroups interactor = new GetMonitroingEntityGroups();
        interactor.setListener(this);
        InteractorThreadPool.getInstance().execute(interactor);
    }

    public void requestMonitoringEntitiesUpdate() {
        updateThreadStatus = true;

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (updateThreadStatus) {
                    UpdateMonitoringEntities interactor = new UpdateMonitoringEntities();
                    interactor.setListener(MapViewPresenter.this);
                    InteractorThreadPool.getInstance().execute(interactor);

                    try {
                        TimeUnit.SECONDS.sleep(MONITORING_UPDATE_FREQUENCY);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void requestQuickReport() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        long fromDateTimestamp = calendar.getTimeInMillis() / 1000;

        calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 0);

        long toDateTimestamp = calendar.getTimeInMillis() / 1000;

        RouteReportParametersPeriodSeparated parameters = new RouteReportParametersPeriodSeparated(
                fromDateTimestamp,
                toDateTimestamp,
                30,
                DependencyInjection.provideConfiguration().calculateOffsetUtc(),
                MonitoringManager.getInstance().getActiveMonitoringEntity().getId());

        GetRouteReport interactor = new GetRouteReport(parameters);
        interactor.setListener(this);
        InteractorThreadPool.getInstance().execute(interactor);
    }

    public void stopMonitoringEntitiesUpdate() {
        updateThreadStatus = false;
    }

    public void saveDefaultMapType(MapType mapType) {
        SetDefaultMapTypeSetting interactor = new SetDefaultMapTypeSetting(mapType);
        InteractorThreadPool.getInstance().execute(interactor);
    }

    public boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                AppController.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    ////

    @Override
    public void onMonitoringEntitiesRetreived(final List<MonitoringEntity> monitoringEntities) {
        requestActiveMonitoringEntity();

        // TODO Delete test code
//        WebSocket webSocket = WebSocket.getInstance();
//        webSocket.connect();
    }

    @Override
    public void onActiveMonitoringEntityRetreived(final MonitoringEntity activeMonitoringEntity) {
        AppController.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (view != null) view.setActiveMonitoringEntity(activeMonitoringEntity);
            }
        });
    }

    @Override
    public void onMonitoringEntityRetreived(final MonitoringEntity monitoringEntity) {
        DependencyInjection.provideMonitoringManager().setActiveMonitoringEntity(monitoringEntity);
        DependencyInjection.provideConfiguration().saveActiveMonitoringEntity(monitoringEntity.getId());
        AppController.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.setActiveMonitoringEntity(monitoringEntity);
            }
        });
    }

    @Override
    public void onMonitoringEntityGroupsRetreived(List<MonitoringEntityGroup> groups) {
        final int count = groups.size();

        AppController.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.toggleChooseGroupMenuButton(count > 1);
            }
        });
    }

    @Override
    public void onMonitoringEntitiesUpdated(final List<MonitoringEntity> monitoringEntities) {
        AppController.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (view != null) view.showMonitoringEntities(monitoringEntities);
            }
        });
    }

    @Override
    public void onRouteReportRetreived(final RouteReport routeReport) {
        AppController.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RouteReport.RouteReportInstance = routeReport;
                view.navigateToRouteReportView();
            }
        });
    }
}
