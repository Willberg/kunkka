package util;

import fun.johntaylor.kunkka.utils.time.TimeUtil;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

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
}
