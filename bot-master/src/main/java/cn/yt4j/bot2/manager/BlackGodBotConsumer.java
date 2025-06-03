package cn.yt4j.bot2.manager;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.yt4j.bot2.config.TelegramProperty;
import cn.yt4j.bot2.entity.bo.AdditionalMessagesBo;
import cn.yt4j.bot2.entity.bo.CozeApiBo;
import cn.yt4j.bot2.entity.vo.CozeApiVo;
import cn.yt4j.bot2.entity.vo.CozeListVo;
import cn.yt4j.bot2.entity.vo.DataVo;
import cn.yt4j.bot2.util.CozeApiUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.groupadministration.RestrictChatMember;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.ChatPermissions;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 机器人消息处理器，也是最核心的消息处理器
 *
 * @author gyv12345@163.com
 */
@Component
@Slf4j
public class BlackGodBotConsumer implements LongPollingSingleThreadUpdateConsumer {

	@Autowired
	private TelegramClient telegramClient;

	@Autowired
	private TelegramProperty telegramProperty;

	@Autowired
	private ScheduledExecutorService scheduledExecutorService;

	@Autowired
	private CozeApiUtil cozeApiUtil;

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public void consume(Update update) {
		if (update == null)
			return;

		// 处理频道消息
		if (update.hasChannelPost() && update.getChannelPost().hasText()) {
			// 频道消息转发
			handleChannelPost(update);
		}

		// 处理群组消息
		if (update.hasMessage()) {
			handleGroupMessage(update);
		}
	}

	private void handleChannelPost(Update update) {
		String messageText = update.getChannelPost().getText();
		Long chatId = update.getChannelPost().getChatId();
		log.info("Received from " + chatId + ": " + messageText);

		if (ObjectUtil.equals(chatId.toString(), telegramProperty.getReportChannelId().toString())) {
			forwardMessage(telegramProperty.getReportChannelId().toString(), telegramProperty.getGroupId().toString(),
					update.getChannelPost().getMessageId());
		}
	}

	@SneakyThrows
	private void handleGroupMessage(Update update) {
		Message message = update.getMessage();
		GetChatMember getChatMember = new GetChatMember(message.getChatId().toString(), message.getFrom().getId());
		ChatMember chatMember = telegramClient.execute(getChatMember);

		log.info("chatId:{},messageId:{},username:{},text:{}", message.getChatId(), message.getMessageId(), chatMember
			.getUser()
			.getFirstName()
				+ (ObjectUtil.isNotEmpty(chatMember.getUser().getLastName()) ? chatMember.getUser().getLastName() : ""),
				message.getText());

		// 检查并限制回复消息
		checkAndRestrict(message);

		// 处理垃圾信息
		if (telegramProperty.getUseAiDel()) {
			String userContent = chatMember.getUser().getFirstName()
					+ (ObjectUtil.isNotEmpty(chatMember.getUser().getLastName()) ? chatMember.getUser().getLastName()
							: "")
					+ ":" + message.getText();
			processSpamInfo(message, chatMember, userContent, message.getChatId());
		}

	}

	@Async
	@SneakyThrows
	public void processSpamInfo(Message message, ChatMember chatMember, String userContent, Long chatId) {
		String status = chatMember.getStatus();

		if (isAdminOrBot(status, chatMember)) {
			return;
		}
		if (!message.hasText()) {
			return;
		}

		CozeApiVo<DataVo> cozeApiVo = sendCozeApiRequest(userContent);

		scheduledExecutorService.schedule(() -> {
			handleCozeApiResponse(chatId, message, cozeApiVo);
		}, 3, TimeUnit.SECONDS);
	}

	private CozeApiVo<DataVo> sendCozeApiRequest(String content) throws Exception {
		CozeApiBo cozeApiBo = new CozeApiBo();
		cozeApiBo.setBotId(telegramProperty.getCozeBotId());
		cozeApiBo.setAdditionalMessages(ListUtil.of(new AdditionalMessagesBo(content)));
		cozeApiBo.setUserId(telegramProperty.getBotUsername());
		String result = cozeApiUtil.question(cozeApiBo);
		return objectMapper.readValue(result, new TypeReference<CozeApiVo<DataVo>>() {
		});
	}

	@SneakyThrows
	private void handleCozeApiResponse(Long chatId, Message message, CozeApiVo<DataVo> cozeApiVo) {
		log.info("chatId {}: ", cozeApiVo.getData().getId());
		CozeApiVo<List<CozeListVo>> cozeApiList;
		do {
			String answer = cozeApiUtil.getAnswer(cozeApiVo.getData().getId(), cozeApiVo.getData().getConversationId());
			cozeApiList = objectMapper.readValue(answer, new TypeReference<CozeApiVo<List<CozeListVo>>>() {
			});
			Thread.sleep(1000);
		}
		while (!ObjectUtil.isNotEmpty(cozeApiList.getData()));

		cozeApiList.getData()
			.stream()
			.filter(datum -> "answer".equals(datum.getType())
					&& Integer.parseInt(datum.getContent()) > telegramProperty.getAiDelThreshold())
			.forEach(datum -> deleteMessage(chatId, message.getMessageId()));
	}

	private boolean isAdminOrBot(String status, ChatMember chatMember) {
		return "administrator".equals(status) || "creator".equals(status) || chatMember.getUser().getIsBot();
	}

	@Async
	public void checkAndRestrict(Message message) {
		if (message.isReply() && message.getReplyToMessage() != null) {
			restrictUserIfNeeded(message);
		}
	}

	private void restrictUserIfNeeded(Message message) {
		Long chatId = message.getChatId();
		String text = message.getText();

		try {
			GetChatMember getChatMember = new GetChatMember(chatId.toString(), message.getFrom().getId());
			ChatMember chatMember = telegramClient.execute(getChatMember);

			if (("GroupAnonymousBot".equals(chatMember.getUser().getUserName()) && "D".equals(text))
					|| ("creator".equals(chatMember.getStatus()) && "D".equals(text))) {
				deleteMessage(chatId, message.getReplyToMessage().getMessageId());
				muteUser(chatId, message.getReplyToMessage().getFrom().getId());
			}
		}
		catch (TelegramApiException e) {
			log.error("Error in restrictUserIfNeeded: ", e);
		}
	}

	private void muteUser(Long chatId, Long userId) throws TelegramApiException {
		ChatPermissions permissions = new ChatPermissions();
		permissions.setCanSendMessages(false);
		RestrictChatMember restrictChatMember = new RestrictChatMember(chatId.toString(), userId, permissions);
		restrictChatMember.setUntilDate((int) (System.currentTimeMillis() / 1000) + 3600);
		telegramClient.execute(restrictChatMember);
	}

	private void forwardMessage(String fromChatId, String toChatId, Integer messageId) {
		try {
			telegramClient
				.execute(ForwardMessage.builder().chatId(toChatId).fromChatId(fromChatId).messageId(messageId).build());
		}
		catch (TelegramApiException e) {
			log.error("Error in forwardMessage: ", e);
		}
	}

	private void deleteMessage(Long chatId, Integer messageId) {
		try {
			telegramClient.execute(new DeleteMessage(chatId.toString(), messageId));
		}
		catch (TelegramApiException e) {
			log.error("Error in deleteMessage: ", e);
		}
	}

}
