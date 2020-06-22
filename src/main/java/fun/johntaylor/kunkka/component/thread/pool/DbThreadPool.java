package fun.johntaylor.kunkka.component.thread.pool;

import org.springframework.stereotype.Component;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

/**
 * @Author John
 * @Description mybatis采用阻塞式,在reactor模式中防止阻塞，切换到其他线程池中进行处理
 * @Date 2020/6/22 6:18 PM
 **/
@Component
public class DbThreadPool {
	private Scheduler daoPool;

	private DbThreadPool() {
		daoPool = Schedulers.newParallel("thread-db-pool", 10);
	}


	public Scheduler daoInstance() {
		return daoPool;
	}
}
