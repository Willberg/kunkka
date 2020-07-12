package fun.johntaylor.kunkka.utils.general;

/**
 * @Author John
 * @Description 对象工具类
 * @Date 2020/7/12 10:38 PM
 **/
public final class ObjectUtil {
	private ObjectUtil() {

	}


	/**
	 /**
	 * @Author John
	 * @Description 多个对象，是否同时为null或存在
	 * @Date 2020/7/12 10:51 PM
	 * @Param objects
	 * @return true/false
	 **/
	public static boolean consistence(Object... objects) {
		if (objects.length == 0) {
			return false;
		}

		boolean existNull = false;
		for (Object o : objects) {
			if (o != null) {
				existNull = true;
				break;
			}
		}

		// 如果存在对象是null, 其余对象都必须是null
		if (existNull) {
			for (Object o : objects) {
				if (o != null) {
					return false;
				}
			}
			return true;
		}

		// 不存在为null的对象，则所有对象都存在
		return true;
	}
}
