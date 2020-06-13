package fun.johntaylor.kunkka.component;

import org.springframework.stereotype.Component;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Component
public class ThreadPoolComponent {
	private Scheduler daoPool;

	private ThreadPoolComponent() {
		daoPool = Schedulers.newParallel("thread-db-pool", 10);
	}


	public Scheduler daoInstance() {
		return daoPool;
	}
}
