package fun.johntaylor.kunkka.utils.json;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * @Author John
 * @Description json处理
 * @Date 2020/6/24 3:17 PM
 **/
public final class JsonUtil {
	private static final Gson GSON = new Gson();

	private JsonUtil() {

	}

	public static String toJson(Object obj) {
		return GSON.toJson(obj);
	}

	public static <T> T fromJson(String str, Class<T> clz) {
		return GSON.fromJson(str, clz);
	}

	public static <T> T fromJson(String str, Type type) {
		return GSON.fromJson(str, type);
	}
}
