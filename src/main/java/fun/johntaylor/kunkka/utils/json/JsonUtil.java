package fun.johntaylor.kunkka.utils.json;

import com.google.gson.Gson;

import java.lang.reflect.Type;

public final class JsonUtil {
    private static final Gson gson = new Gson();

    private JsonUtil() {

    }

    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    public static <T> T fromJson(String str, Class<T> clz) {
        return gson.fromJson(str, clz);
    }

    public static <T> T fromJson(String str, Type type) {
        return gson.fromJson(str, type);
    }
}
