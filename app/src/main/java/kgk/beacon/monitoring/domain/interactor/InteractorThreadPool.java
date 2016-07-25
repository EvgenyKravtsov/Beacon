package kgk.beacon.monitoring.domain.interactor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class InteractorThreadPool {

    private static final int POOL_SIZE = 2;
    private static final int MAX_POOL_SIZE = 4;
    private static final int TIMEOUT = 30;

    private static InteractorThreadPool instance;

    private ThreadPoolExecutor threadPoolExecutor;

    ////

    private InteractorThreadPool() {
        threadPoolExecutor = new ThreadPoolExecutor(POOL_SIZE, MAX_POOL_SIZE, TIMEOUT,
                TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(POOL_SIZE));
    }

    public static synchronized InteractorThreadPool getInstance() {
        if (instance == null) {
            instance = new InteractorThreadPool();
        }
        return instance;
    }

    ////

    public void execute(final Interactor interactor) {
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                interactor.execute();
            }
        });
    }
}
