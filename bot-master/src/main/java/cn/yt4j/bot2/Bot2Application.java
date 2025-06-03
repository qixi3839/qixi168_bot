package cn.yt4j.bot2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 项目启动
 *
 * @author gyv12345@163.com
 */
@EnableScheduling
@SpringBootApplication
public class Bot2Application {

	public static void main(String[] args) {
		// 使用代理
		System.setProperty("http.proxyHost", "127.0.0.1");
		System.setProperty("http.proxyPort", "7890");
		// 使用socket 代理
		System.setProperty("https.proxyHost", "127.0.0.1");
		System.setProperty("https.proxyPort", "7890");

		SpringApplication.run(Bot2Application.class, args);
	}

}
