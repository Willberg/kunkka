package fun.johntaylor.kunkka.utils.time;

import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

/**
 * @Author John
 * @Description 时间工具类
 * @Date 2020/8/12 10:19 PM
 **/
@Slf4j
public final class TimeUtil {
	private TimeUtil() {

	}

	/**
	 * 将日期字符串转为特定格式的日期
	 * @param dateStr
	 * @param format
	 * @return Date
	 */
	public static Date getDate(String dateStr, String format) {
		Date date = null;
		try {
			date = new SimpleDateFormat(format).parse(dateStr);
		} catch (ParseException e) {
			log.error("convert date error", e);
		}
		return date;
	}

	/**
	 * 将日期字符串转为特定格式的LocalDate
	 * @param dateStr
	 * @param format
	 * @return LocalDate
	 */
	public static LocalDate getLocalDate(String dateStr, String format) {
		Date date = getDate(dateStr, format);
		if (Objects.isNull(date)) {
			return null;
		}
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}

	/**
	 * 根据localDate获取时间戳
	 * @param localDate
	 * @return timestamp
	 */
	public static long getTimestampByLocalDate(LocalDate localDate) {
		return Timestamp.valueOf(localDate.atStartOfDay()).getTime();
	}

	/**
	 * 根据时间戳获取本地日期
	 * @param timestamp
	 * @param format
	 * @return date
	 */
	public static String getDateStrByTimestamp(Long timestamp, String format) {
		LocalDate localDate = getLocalDateByTimestamp(timestamp);
		return localDate.format(DateTimeFormatter.ofPattern(format));
	}

	/**
	 * 根据时间戳获取本地日期
	 * @param timestamp
	 * @return LocalDate
	 */
	public static LocalDate getLocalDateByTimestamp(Long timestamp) {
		LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), TimeZone.getDefault().toZoneId());
		return localDateTime.toLocalDate();
	}

	/**
	 * 根据LocalDate获取本地日期
	 * @param localDate
	 * @param format
	 * @return date
	 */
	public static String getDateStrByLocalDate(LocalDate localDate, String format) {
		return localDate.format(DateTimeFormatter.ofPattern(format));
	}

	/**
	 * 根据开始日期, 间隔月份和格式，返回月初和月末的时间戳
	 * @param startDate
	 * @param format
	 * @return Long[]
	 */
	public static Long[] getStartEndTimestamp(long diff, String startDate, String format) {
		LocalDate startLocalDate = TimeUtil.getLocalDate(startDate, format);
		if (Objects.isNull(startLocalDate)) {
			return null;
		}
		startLocalDate = startLocalDate.atStartOfDay().toLocalDate();
		long startTime = TimeUtil.getTimestampByLocalDate(startLocalDate);
		long endTime = TimeUtil.getTimestampByLocalDate(startLocalDate.plus(diff, ChronoUnit.MONTHS));
		Long[] timestamps = new Long[2];
		timestamps[0] = startTime;
		timestamps[1] = endTime;
		return timestamps;
	}

	/**
	 * 返回格式化的datetime
	 * @param timestamp
	 * @param format
	 * @return datetime
	 */
	public static String getLocalDateTimeStrFromTimestamp(long timestamp, String format) {
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), TimeZone.getDefault().toZoneId()).format(DateTimeFormatter.ofPattern(format));
	}
}
