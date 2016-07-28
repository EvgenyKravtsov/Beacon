package kgk.beacon.monitoring.domain.interactor;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import kgk.beacon.monitoring.domain.model.MonitoringEntity;
import kgk.beacon.monitoring.domain.model.MonitoringEntityGroup;
import kgk.beacon.monitoring.domain.model.MonitoringManager;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetMonitoringEntitiesTest {

    // Mocks
    private MonitoringManager monitoringManagerMock;
    private GetMonitoringEntities.Listener listenerMock;

    private GetMonitoringEntities getMonitoringEntities;

    ////

    @Before
    public void setUp() {
        monitoringManagerMock = mock(MonitoringManager.class);
        listenerMock = mock(GetMonitoringEntities.Listener.class);

        getMonitoringEntities = new GetMonitoringEntities(monitoringManagerMock);
    }

    ////

    @Test
    public void execute_activeGroupIsNull_shouldReturnAllEntites() {
        when(monitoringManagerMock.getMonitoringEntities())
                .thenReturn(initMonitoringManagerMock());
        when(monitoringManagerMock.getActiveMonitoringEntityGroup()).thenReturn(null);

        getMonitoringEntities.setListener(listenerMock);
        getMonitoringEntities.execute();

        verify(listenerMock)
                .onMonitoringEntitiesRetreived(monitoringManagerMock.getMonitoringEntities());
    }

    @Test
    public void execute_activeGroupIsNotNull_shouldReturnEntitiesFromActiveGroup() {
        when(monitoringManagerMock.getMonitoringEntities())
                .thenReturn(initMonitoringManagerMock());

        MonitoringEntityGroup group = new MonitoringEntityGroup("group1");
        group.add(new MonitoringEntity(1, null, null, null, null));
        group.add(new MonitoringEntity(2, null, null, null, null));

        when(monitoringManagerMock.getActiveMonitoringEntityGroup())
                .thenReturn(group);

        getMonitoringEntities.setListener(new GetMonitoringEntities.Listener() {
            @Override
            public void onMonitoringEntitiesRetreived(List<MonitoringEntity> monitoringEntities) {
                assertTrue(monitoringEntities.size() == 2);
            }
        });

        getMonitoringEntities.execute();
    }

    ////

    private List<MonitoringEntity> initMonitoringManagerMock() {
        List<String> group1 = new ArrayList<>();
        group1.add("group1");
        List<String> group2 = new ArrayList<>();
        group2.add("group2");

        List<MonitoringEntity> monitoringEntities = new ArrayList<>();
        monitoringEntities.add(new MonitoringEntity(1, "aaa", "111", "111", group1));
        monitoringEntities.add(new MonitoringEntity(2, "bbb", "222", "222", group1));
        monitoringEntities.add(new MonitoringEntity(3, "ccc", "333", "333", group2));

        MonitoringEntityGroup groupEntity1 = new MonitoringEntityGroup("group1");
        groupEntity1.add(monitoringEntities.get(0));
        groupEntity1.add(monitoringEntities.get(1));
        MonitoringEntityGroup groupEntity2 = new MonitoringEntityGroup("group2");
        groupEntity2.add(monitoringEntities.get(2));

        return monitoringEntities;
    }
}
