package fun.johntaylor.kunkka.component;

import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

public class ThreadPoolComponent {
    private static Scheduler daoPool = Schedulers.newParallel("thread-db-pool", 10);

    private ThreadPoolComponent() {

    }

    public static Scheduler daoThreadPool() {
        return daoPool;
    }
}
