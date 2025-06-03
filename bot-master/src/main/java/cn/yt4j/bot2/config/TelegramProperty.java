package cn.yt4j.bot2.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 机器人配置
 *
 * @author gyv12345@163.com
 */
@Component
@Data
@ConfigurationProperties(prefix = "yt4j.telegram")
public class TelegramProperty {

	/**
	 * 机器人Token
	 */
	private String token;

	/**
	 * 机器人名称,也充当coze里userID
	 */
	private String botUsername;

	/**
	 * coze的token
	 */
	private String cozeAiToken;

	/**
	 * coze的智能体ID
	 */
	private String cozeBotId;

	/**
	 * cn 是国内版 com 是国际版
	 */
	private String cozeCn = "cn";

	/**
	 * 群组id
	 */
	private Long groupId;

	/**
	 * 公榜频道ID
	 */
	private Long channelId;

	/**
	 * 报告频道ID
	 */
	private Long reportChannelId;

	/**
	 * 是否使用ai删除
	 */
	private Boolean useAiDel = false;

	/**
	 * AI删除的阈值 1-10 值越大越严格
	 */
	private Integer aiDelThreshold = 6;

}
