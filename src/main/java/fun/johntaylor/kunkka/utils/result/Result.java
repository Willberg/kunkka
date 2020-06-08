package fun.johntaylor.kunkka.utils.result;

import fun.johntaylor.kunkka.utils.error.ErrorCode;
import fun.johntaylor.kunkka.utils.error.MessageUtil;
import fun.johntaylor.kunkka.utils.json.JsonUtil;

public final class Result<T> {
    private Boolean status;

    private String code;

    private String message;

    private T data;

    public static final Boolean S_SUCCESS = true;
    public static final Boolean S_FAIL = false;

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

    public static <T> Result<T> failWithMessage(String message) {
        return new Result<>(S_FAIL, ErrorCode.SYS_CUSTOMIZE_ERROR, message, null);
    }

    public String toString() {
        return JsonUtil.toJson(this);
    }
}
