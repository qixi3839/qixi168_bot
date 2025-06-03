package cn.yt4j.bot2.controller;

import cn.yt4j.bot2.config.TelegramProperty;
import cn.yt4j.bot2.manager.BlackGodBotManager;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 机器人
 *
 * @author gyv12345@163.com
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("bot")
public class BotController {

	private final TelegramProperty telegramProperty;

	private final BlackGodBotManager blackGodBotManager;

	@PostMapping("send")
	public void sendMessage(@RequestBody String message) {
		blackGodBotManager.sendMessage(message, telegramProperty.getChannelId(), true);
	}

	@GetMapping("del")
	public void del(Integer messageId) {
		blackGodBotManager.del(messageId, telegramProperty.getGroupId());
	}

	@PostMapping("sendQun")
	public void sendQunMessage(@RequestBody String message) {
		blackGodBotManager.sendMessage(message, telegramProperty.getGroupId(), true);
	}

}
