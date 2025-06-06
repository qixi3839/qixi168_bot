
package cn.yt4j.bot2.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.io.Serial;
import java.io.Serializable;

/**
 * 返回信息
 *
 * @author gyv12345@163.com
 */
@ToString(callSuper = true)
@Getter
@Setter
public class Result<T> implements Serializable {

	@Serial
	private static final long serialVersionUID = -6101337183914807339L;

	/**
	 * 编码
	 */
	private int code;

	/**
	 * 消息
	 */
	private String message;

	/**
	 * 数据
	 */
	private T data;

	/**
	 * 正常与否
	 */
	private Boolean success;

	public static <T> Result<T> ok() {
		return result(null, HttpStatus.OK.value(), null);
	}

	public static <T> Result<T> ok(T data) {
		if (data instanceof Boolean) {
			if (Boolean.FALSE.equals(data)) {
				return result(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), "出现错误，可能是演示环境引起的");
			}
		}
		return result(data, HttpStatus.OK.value(), null);
	}

	public static <T> Result<T> ok(String message) {
		return result(null, HttpStatus.OK.value(), message);
	}

	public static Result<String> success(String message) {
		return result(message, HttpStatus.OK.value(), null);
	}

	public static <T> Result<T> ok(T data, String message) {
		return result(data, HttpStatus.OK.value(), message);
	}

	public static <T> Result<T> failed() {
		return result(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
	}

	public static <T> Result<T> failed(String message) {
		return result(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
	}

	public static <T> Result<T> failed(T data) {
		return result(data, HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
	}

	public static <T> Result<T> failed(T data, String message) {
		return result(data, HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
	}

	private static <T> Result<T> result(T data, int code, String message) {
		Result<T> apiResult = new Result<>();
		apiResult.setCode(code);
		apiResult.setData(data);
		apiResult.setMessage(message);
		apiResult.setSuccess(true);
		return apiResult;
	}

}
