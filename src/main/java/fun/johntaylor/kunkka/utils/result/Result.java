package fun.johntaylor.kunkka.utils.result;

import fun.johntaylor.kunkka.utils.error.MessageUtil;
import fun.johntaylor.kunkka.utils.json.JsonUtil;

/**
 * @Author John
 * @Description 返回消息
 * @Date 2020/6/24 3:18 PM
 **/
public final class Result<T> {
	private Boolean status;

	private String code;

	private String message;

	private T data;

	private static final Boolean S_SUCCESS = true;
	private static final Boolean S_FAIL = false;

	private Result() {

	}

	private Result(boolean status, String code, String message, T data) {
		this.status = status;
		this.code = code;
		this.message = message;
		this.data = data;
	}

	public static <T> Result<T> success() {
		return new Result<>(S_SUCCESS, null, null, null);
	}

	public static <T> Result<T> success(T data) {
		return new Result<>(S_SUCCESS, null, null, data);
	}

	public static <T> Result<T> fail(String code) {
		return new Result<>(S_FAIL, code, MessageUtil.getMessage(code), null);
	}

	public static <T> Result<T> failWithMessage(String code, String message) {
		return new Result<>(S_FAIL, code, message, null);
	}

	public static <T> Result<T> failWithMessage(String code, T data) {
		return new Result<>(S_FAIL, code, JsonUtil.toJson(data), null);
	}

	public boolean isSuccess() {
		return this.status;
	}

	public String getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	public T getData() {
		return this.data;
	}

	@Override
	public String toString() {
		return JsonUtil.toJson(this);
	}
}
