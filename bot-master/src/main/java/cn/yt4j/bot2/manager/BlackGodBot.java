package cn.yt4j.bot2.manager;

import cn.yt4j.bot2.config.TelegramProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;

/**
 * 机器人注册
 *
 * @author gyv12345@163.com
 */
@Component
@Slf4j
public class BlackGodBot implements SpringLongPollingBot {

	@Autowired
	private BlackGodBotConsumer consumer;

	@Autowired
	private TelegramProperty telegramProperty;

	@Override
	public String getBotToken() {
		return telegramProperty.getToken();
	}

	@Override
	public LongPollingUpdateConsumer getUpdatesConsumer() {
		return consumer;
	}

}
