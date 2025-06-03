package cn.yt4j.bot2.manager;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.groupadministration.RestrictChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.ChatPermissions;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

/**
 * 机器人manager层，本来想着操作数据库的，但是感觉我们的是单体的服务，直接改就行了
 */
@Slf4j
@Component
public class BlackGodBotManager {

	@Autowired
	private TelegramClient telegramClient;

	@Async
	public void sendMessage(String messageText, Long chatId, Boolean isMd) {
		SendMessage message = SendMessage // Create a message object
			.builder()
			.chatId(chatId)
			.text(messageText)
			.build();

		try {
			telegramClient.execute(message);
		}
		catch (TelegramApiException e) {
			throw new RuntimeException(e);
		}
	}

	@Async
	public void forwardMessage(String messageText, Long chatId, Long fromChatId, Boolean isMd) {

	}

	@Async
	@SneakyThrows
	public void muteUser(Long chatId, Long userId) {
		// 禁止发送消息
		ChatPermissions permissions = new ChatPermissions();
		permissions.setCanSendMessages(false);

		// 限制原消息发送者的权限
		RestrictChatMember restrictChatMember = new RestrictChatMember(chatId.toString(), userId, permissions);
		restrictChatMember.setChatId(chatId.toString());
		restrictChatMember.setUserId(userId);
		restrictChatMember.setUntilDate((int) (System.currentTimeMillis() / 1000) + 3600); // 设置限制时长（1小时）

		telegramClient.execute(restrictChatMember);
	}

	@Async
	public void processSpamInfo(Update update) {

	}

	@Async
	public void del(Integer messageId, Long chatId) {
		try {
			telegramClient.execute(new DeleteMessage(chatId.toString(), messageId));
		}
		catch (TelegramApiException e) {
			log.error("Error in deleteMessage: ", e);
		}
	}

}
