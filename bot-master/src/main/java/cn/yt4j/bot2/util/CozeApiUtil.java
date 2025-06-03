package cn.yt4j.bot2.util;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.Method;
import cn.yt4j.bot2.config.TelegramProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * coze请求工具
 *
 * @author gyv12345@163.com
 */
@Component
@Slf4j
public class CozeApiUtil {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private TelegramProperty telegramProperty;

	/**
	 * 问问题
	 * @param body
	 * @return
	 */
	@SneakyThrows
	public String question(Object body) {
		HttpRequest request = new HttpRequest("https://api.coze." + telegramProperty.getCozeCn() + "/v3/chat");
		request.setMethod(Method.POST);

		String contentType = "application/json";

		request.header("Content-Type", contentType);
		request.header("Authorization", "Bearer " + telegramProperty.getCozeAiToken());

		if (ObjectUtil.isNotEmpty(body)) {
			request.body(objectMapper.writeValueAsString(body));
			log.info("发送请求:{}", objectMapper.writeValueAsString(body));
		}

		String result = request.execute().body();
		log.info("调用COZE返回：{}", result);
		return result;
	}

	/**
	 * 获取答案
	 */
	public String getAnswer(String chatId, String conversationId) {
		HttpRequest request = new HttpRequest(
				"https://api.coze." + telegramProperty.getCozeCn() + "/v3/chat/message/list");
		request.setMethod(Method.GET);

		String contentType = "application/json";

		request.header("Content-Type", contentType);
		request.header("Authorization", "Bearer " + telegramProperty.getCozeAiToken());

		Map<String, Object> map = new HashMap<>();
		map.put("chat_id", chatId);
		map.put("conversation_id", conversationId);

		request.form(map);
		String result = request.execute().body();
		log.info("获取COZE调用结果：{}", result);
		return result;
	}

}
