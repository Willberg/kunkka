package fun.johntaylor.kunkka.utils.error;

/**
 * @Author John
 * @Description 错误码
 * @Date 2020/6/24 3:06 PM
 **/
public final class ErrorCode {
	public static String SYS_ERROR = "sys.0001";
	public static String SYS_DB_ERROR = "sys.0002";
	public static String SYS_TIMEOUT_ERROR = "sys.0003";
	public static String SYS_CUSTOMIZE_ERROR = "sys.0004";
	public static String SYS_PARAMETER_ERROR = "sys.0005";
	public static String SYS_LIMIT_ERROR = "sys.0006";

	public static String USER_EXISTS = "user.0001";
	public static String USER_AUTHENTICATION_ERROR = "user.0002";
}
