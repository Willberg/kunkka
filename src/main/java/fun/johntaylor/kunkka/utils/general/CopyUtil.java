package fun.johntaylor.kunkka.utils.general;

import fun.johntaylor.kunkka.utils.json.JsonUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @Author John
 * @Description 拷贝工具
 * @Date 2020/7/3 1:51 PM
 **/
@Slf4j
public final class CopyUtil {


	/**
	 * @Author John
	 * @Description 深拷贝
	 * @Date 2020/7/3 1:51 PM
	 * @Param
	 * @return
	 **/
	public static <T> T deepCopy(T t, Type type) {
		String json = JsonUtil.toJson(t);
		return JsonUtil.fromJson(json, type);
	}


	/**
	 * @Author John
	 * @Description 获取变量表
	 * @Date 2020/7/4 10:07 AM
	 * @Param
	 * @return
	 **/
	private static <S> Map<String, Field> getFieldMap(boolean isLowcase, S s) throws IllegalAccessException {
		Field[] sourceFields = s.getClass().getDeclaredFields();
		Map<String, Field> fieldMap = new HashMap<>(sourceFields.length);
		// 存放sourceFields
		for (Field f : sourceFields) {
			if (Modifier.isStatic(f.getModifiers())) {
				continue;
			}

			f.setAccessible(true);
			if (Objects.nonNull(f.get(s))) {
				String fieldName = f.getName();
				if (isLowcase) {
					fieldName = fieldName.toLowerCase();
				}
				fieldMap.put(fieldName, f);
			}
		}
		return fieldMap;
	}


	/**
	 * @Author John
	 * @Description 根据source，给target复制, 对象类型可以不同
	 * @Date 2020/7/3 10:16 PM
	 * @Param
	 * @return
	 **/
	public static <S, T> T copy(S s, T t) {
		Field[] targetFields = t.getClass().getDeclaredFields();

		try {
			Map<String, Field> fieldMap = getFieldMap(false, s);

			for (Field f : targetFields) {
				if (Modifier.isStatic(f.getModifiers())) {
					continue;
				}

				f.setAccessible(true);
				if (Objects.nonNull(fieldMap.get(f.getName()))) {
					f.set(t, fieldMap.get(f.getName()).get(s));
				}
			}
		} catch (Exception e) {
			log.error("copy error, {} to {}", s.getClass().getName(), t.getClass().getName(), e);
		}
		return t;
	}

	/**
	 * @Author John
	 * @Description 根据source，给target复制, 对象类型可以不同,通过方法调用
	 * @Date 2020/7/3 10:16 PM
	 * @Param
	 * @return
	 **/
	private static <S, T> T copyWithMethodName(S s, T t, String methodName) throws IllegalAccessException, InvocationTargetException {
		Map<String, Field> fieldMap = getFieldMap(true, s);
		Method[] methods = t.getClass().getDeclaredMethods();
		for (Method m : methods) {
			if (m.getName().contains(methodName)) {
				String fieldName = m.getName().substring(methodName.length()).toLowerCase();
				if (fieldMap.containsKey(fieldName)) {
					m.invoke(t, fieldMap.get(fieldName).get(s));
				}
			}
		}
		return t;
	}

	/**
	 * @Author John
	 * @Description 根据source，给target复制, 对象类型可以不同,通过set方法调用
	 * @Date 2020/7/3 10:16 PM
	 * @Param
	 * @return
	 **/
	public static <S, T> T copyWithSet(S s, T t) {
		try {
			return copyWithMethodName(s, t, "set");
		} catch (Exception e) {
			log.error("copy error, {} to {}", s.getClass().getName(), t.getClass().getName(), e);
		}
		return t;
	}
}
