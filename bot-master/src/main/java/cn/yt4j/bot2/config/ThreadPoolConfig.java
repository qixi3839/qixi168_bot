package cn.yt4j.bot2.config;

import cn.yt4j.bot2.util.Threads;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池配置
 *
 * @author Lion Li
 **/
@Slf4j
@Configuration
public class ThreadPoolConfig {

	/**
	 * 核心线程数 = cpu 核心数 + 1
	 */
	private final int core = Runtime.getRuntime().availableProcessors() + 1;

	private ScheduledExecutorService scheduledExecutorService;

	/**
	 * 执行周期性或定时任务
	 */
	@Bean(name = "scheduledExecutorService")
	protected ScheduledExecutorService scheduledExecutorService() {
		ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(core,
				new BasicThreadFactory.Builder().namingPattern("schedule-pool-%d").daemon(true).build(),
				new ThreadPoolExecutor.CallerRunsPolicy()) {
			@Override
			protected void afterExecute(Runnable r, Throwable t) {
				super.afterExecute(r, t);
				Threads.printException(r, t);
			}
		};
		this.scheduledExecutorService = scheduledThreadPoolExecutor;
		return scheduledThreadPoolExecutor;
	}

	/**
	 * 销毁事件
	 */
	@PreDestroy
	public void destroy() {
		try {
			log.info("====关闭后台任务任务线程池====");
			Threads.shutdownAndAwaitTermination(scheduledExecutorService);
		}
		catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

}
