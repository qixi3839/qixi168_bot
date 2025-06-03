package cn.yt4j.bot2.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.generics.TelegramClient;

/**
 * 机器人客户端配置
 *
 * @author gyv12345@163.com
 */
@Configuration
public class BlackGodTelegramClientConfig {

	@Autowired
	private TelegramProperty telegramProperty;

	@Bean
	public TelegramClient getClient() {
		return new OkHttpTelegramClient(telegramProperty.getToken());
	}

}
