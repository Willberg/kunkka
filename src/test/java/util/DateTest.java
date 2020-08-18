package util;

import com.google.gson.reflect.TypeToken;
import fun.johntaylor.kunkka.utils.general.CopyUtil;
import fun.johntaylor.kunkka.utils.time.TimeUtil;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

public class DateTest {
	@Test
	public void test1() {
		LocalDate startLocalDate = TimeUtil.getLocalDate("2020-01-01", "yyyy-MM-dd");
		LocalDate now = LocalDate.now();
		System.out.println(now.getYear());
		System.out.println(now.getMonthValue());

		System.out.println(startLocalDate.plus(1, ChronoUnit.MONTHS).getDayOfYear() - startLocalDate.getDayOfYear());
		System.out.println(LocalDate.now().getDayOfMonth());
	}

	@Test
	public void test2() {
		Map<String, Double> retMap = new LinkedHashMap<>(31);
		IntStream.range(1, 31).forEach(value -> {
			Double v = new BigDecimal(Math.random() * 100).setScale(2, RoundingMode.HALF_DOWN).doubleValue();
			if (value % 3 == 0) {
				v = 0.0;
			}
			retMap.put(String.format("2020-08-%s", value), v);
		});

		for (String date : retMap.keySet()) {
			System.out.println(String.format("%s:%s", date, retMap.get(date)));
		}
		System.out.println();

		Set<String> set = CopyUtil.deepCopy(retMap.keySet(), new TypeToken<Set<String>>() {
		}.getType());
		for (String date : set) {
			if (retMap.get(date) == 0D) {
				retMap.remove(date);
			}
		}

		for (String date : retMap.keySet()) {
			System.out.println(String.format("%s:%s", date, retMap.get(date)));
		}
	}
}
