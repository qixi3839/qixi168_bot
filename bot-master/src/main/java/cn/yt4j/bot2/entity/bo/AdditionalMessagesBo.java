package cn.yt4j.bot2.entity.bo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * coze查询内容
 *
 * @author gyv12345@163.com
 */
@Data
public class AdditionalMessagesBo {

	public AdditionalMessagesBo(String content) {
		this.content = content;
	}

	@JsonProperty("role")
	private String role = "user";

	@JsonProperty("content")
	private String content;

	@JsonProperty("content_type")
	private String contentType = "text";

}
