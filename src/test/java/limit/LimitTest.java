package limit;

import fun.johntaylor.kunkka.component.filter.impl.LimitFilter;
import fun.johntaylor.kunkka.utils.cache.impl.LimitCache;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.concurrent.*;
import java.util.stream.IntStream;

public class LimitTest {


	/**
	 * @Author John
	 * @Description 测试限流，需要将LimitFilter的limit.max设置为特定值，比如5
	 * @Date 2020/7/6 8:00 PM
	 * @Param
	 * @return
	 **/
	@Test
	public void testFixedWindow() throws Exception {
		LimitFilter filter = new LimitFilter();
		// 创建一个10线程的线程池
		ExecutorService service = Executors.newFixedThreadPool(10);
		// 创建一个100个任务的信号量
		CountDownLatch countDownLatch = new CountDownLatch(100);
		long start = System.currentTimeMillis();
		//向线程池提交100次任务
		IntStream.range(0, 100).forEach(i -> {
			service.execute(() -> {
				boolean isLimit = filter.isTokenBucketLimit();
//					boolean isLimit = isLeakBucketLimit();
				if (isLimit) {
					System.out.println("超过服务");
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// 每完成一次任务，信号量减1
				countDownLatch.countDown();
			});
		});
		service.shutdown();
		// 线程等待，当信号量为0时，被唤醒
		countDownLatch.await();
		System.out.println("use time: " + (System.currentTimeMillis() - start));
	}

	@Test
	public void testExecutor() throws Exception {
		ExecutorService service = Executors.newFixedThreadPool(10);
		CountDownLatch countDownLatch = new CountDownLatch(100);
		long start = System.currentTimeMillis();
		IntStream.range(0, 100).forEach(i -> {
			service.submit(() -> {
				synchronized (this) {
					System.out.println("i=" + i);
					System.out.println("thread: " + Thread.currentThread().getName());
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (i % 10 == 0) {
					System.out.println();
				}
				countDownLatch.countDown();
			});
		});
		service.shutdown();
		countDownLatch.await();
		System.out.println(countDownLatch.getCount());
		System.out.println("use time: " + (System.currentTimeMillis() - start));
	}

	/**
	 * @Author John
	 * @Description 漏桶, 需要将LimitCache过期时间设置为5s, 进行对比测试
	 * @Date 2020/7/5 10:29 AM
	 * @Param
	 * @return
	 **/
	private static BlockingQueue<Long> queue = new ArrayBlockingQueue<>(5);

	private synchronized boolean isLeakBucketLimit() {
		Integer i = Optional.ofNullable(LimitCache.get("lack", Integer.class)).orElse(0);
		if (0 == i) {
			queue.clear();
			LimitCache.set("lack", 1);
		}

		if (queue.remainingCapacity() > 0) {
			queue.add(1L);
			return false;
		} else {
			return true;
		}

	}
}
