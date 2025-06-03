package cn.yt4j.bot2.entity.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * coze 对话列表
 *
 * @author gyv12345@163.com
 */
@NoArgsConstructor
@Data
public class CozeListVo {

	@JsonProperty("bot_id")
	private String botId;

	@JsonProperty("chat_id")
	private String chatId;

	@JsonProperty("content")
	private String content;

	@JsonProperty("content_type")
	private String contentType;

	@JsonProperty("conversation_id")
	private String conversationId;

	@JsonProperty("created_at")
	private Integer createdAt;

	@JsonProperty("id")
	private String id;

	@JsonProperty("role")
	private String role;

	@JsonProperty("type")
	private String type;

	@JsonProperty("updated_at")
	private Integer updatedAt;

}
