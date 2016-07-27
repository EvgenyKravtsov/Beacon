package kgk.beacon.monitoring.domain.interactor;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import kgk.beacon.monitoring.domain.model.MonitoringEntity;
import kgk.beacon.monitoring.domain.model.MonitoringManager;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetMonitoringEntityByIdTest {

    // Mocks
    private MonitoringManager monitoringManagerMock;
    private GetMonitoringEntityById.Listener listenerMock;

    private GetMonitoringEntityById interactor;

    ////

    @Before
    public void setUp() {
        monitoringManagerMock = mock(MonitoringManager.class);
        listenerMock = mock(GetMonitoringEntityById.Listener.class);
    }

    ////

    @Test
    public void execute_idNotFound_shouldNotCallListener() {
        interactor = new GetMonitoringEntityById(0, monitoringManagerMock);
        when(monitoringManagerMock.getMonitoringEntities())
                .thenReturn(generateMonitoringEntityList());

        interactor.setListener(listenerMock);
        interactor.execute();

        verify(listenerMock, never()).onMonitoringEntityRetreived(any(MonitoringEntity.class));
    }

    @Test
    public void execute_idFound_shouldCallListener() {
        interactor = new GetMonitoringEntityById(1, monitoringManagerMock);
        when(monitoringManagerMock.getMonitoringEntities())
                .thenReturn(generateMonitoringEntityList());

        interactor.setListener(listenerMock);
        interactor.execute();

        verify(listenerMock).onMonitoringEntityRetreived(any(MonitoringEntity.class));
    }

    ////

    private List<MonitoringEntity> generateMonitoringEntityList() {
        List<MonitoringEntity> monitoringEntities = new ArrayList<>();
        monitoringEntities.add(new MonitoringEntity(1, "aaa"));
        monitoringEntities.add(new MonitoringEntity(2, "bbb"));
        return monitoringEntities;
    }
}
