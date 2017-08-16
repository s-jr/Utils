package net.sjr.converterutils;

import org.apache.commons.lang3.StringUtils;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Diverse Konverter für Datumstypen
 *
 * @author Jan Reichl
 */
@SuppressWarnings("unused")
public final class Java8ConverterUtils {

	/**
	 * util.Date zu Java 8 typen und zurück
	 *
	 * @author Jan Reichl
	 */
	public static final class UtilJava8Date {
		public static LocalDate utilDateToLocalDate(final Date d) {
			if (d == null) {
				return null;
			}
			return Instant.ofEpochMilli(d.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
		}

		public static Date localDateToUtilDate(final LocalDate d) {
			if (d == null) {
				return null;
			}
			return Date.from(d.atStartOfDay(ZoneId.systemDefault()).toInstant());
		}

		public static LocalDateTime utilDateToLocalDateTime(final Date d) {
			if (d == null) {
				return null;
			}
			return LocalDateTime.ofInstant(d.toInstant(), ZoneId.systemDefault());
		}

		public static Date localDateTimeToUtilDate(final LocalDateTime d) {
			if (d == null) {
				return null;
			}
			return Date.from(d.atZone(ZoneId.systemDefault()).toInstant());
		}

		public static LocalTime utilDateToLocalTime(final Date d) {
			if (d == null) {
				return null;
			}
			return Instant.ofEpochMilli(d.getTime()).atZone(ZoneId.systemDefault()).toLocalTime();
		}

		public static Date localTimeToUtilDate(final LocalTime d) {
			if (d == null) {
				return null;
			}
			return Date.from(d.atDate(LocalDate.now()).atZone(ZoneId.systemDefault()).toInstant());
		}
	}

	/**
	 * Schöner lesbarer String zu Java 8 typen und zurück
	 *
	 * @author Jan Reichl
	 */
	public static final class PrettyString {
		private static final DateTimeFormatter MMMyyyy = DateTimeFormatter.ofPattern("MMMM yyyy");
		private static final DateTimeFormatter EEddMMyyyy = DateTimeFormatter.ofPattern("EE dd.MM.yyyy");
		private static final DateTimeFormatter ddMMyyyy = DateTimeFormatter.ofPattern("dd.MM.yyyy");
		private static final DateTimeFormatter ddMMyyyyHHmm = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
		private static final DateTimeFormatter ddMMyyyyHHmmss = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
		private static final DateTimeFormatter HHmm = DateTimeFormatter.ofPattern("HH:mm");

		public static String localDateToMonthYearString(final LocalDate d) {
			if (d == null) {
				return null;
			}
			return d.format(MMMyyyy);
		}

		public static LocalDate monthYearStringToLocalDate(final String s) {
			if (StringUtils.isBlank(s)) {
				return null;
			}
			return LocalDate.parse(s, MMMyyyy);
		}

		public static String localDateToWeekdayString(final LocalDate d) {
			if (d == null) {
				return null;
			}
			return d.format(EEddMMyyyy);
		}

		public static LocalDate weekdayStringToLocalDate(final String s) {
			if (StringUtils.isBlank(s)) {
				return null;
			}
			return LocalDate.parse(s, EEddMMyyyy);
		}

		public static String localDateToString(final LocalDate d) {
			if (d == null) {
				return null;
			}
			return d.format(ddMMyyyy);
		}

		public static LocalDate stringToLocalDate(final String s) {
			if (StringUtils.isBlank(s)) {
				return null;
			}
			return LocalDate.parse(s, ddMMyyyy);
		}

		public static String localDateTimeToString(final LocalDateTime d) {
			if (d == null) {
				return null;
			}
			return d.format(ddMMyyyyHHmm);
		}

		public static LocalDateTime stringToLocalDateTime(final String s) {
			if (StringUtils.isBlank(s)) {
				return null;
			}
			return LocalDateTime.parse(s, ddMMyyyyHHmm);
		}

		public static String localDateTimeToStringWithSeconds(final LocalDateTime d) {
			if (d == null) {
				return null;
			}
			return d.format(ddMMyyyyHHmmss);
		}

		public static LocalDateTime stringWithSecondsToLocalDateTime(final String s) {
			if (StringUtils.isBlank(s)) {
				return null;
			}
			return LocalDateTime.parse(s, ddMMyyyyHHmmss);
		}

		public static String localTimeToString(final LocalTime d) {
			if (d == null) {
				return null;
			}
			return d.format(HHmm);
		}

		public static LocalTime stringToLocalTime(final String s) {
			if (StringUtils.isBlank(s)) {
				return null;
			}
			return LocalTime.parse(s, HHmm);
		}
	}

	/**
	 * String für SQL-Datenbanken zu Java 8 typen und zurück
	 *
	 * @author Jan Reichl
	 */
	public static final class SQLString {
		private static final DateTimeFormatter ddMMyyyy = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		private static final DateTimeFormatter ddMMyyyyHHmm = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		private static final DateTimeFormatter ddMMyyyyHHmmss = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		private static final DateTimeFormatter HHmm = DateTimeFormatter.ofPattern("HH:mm");

		public static String localDateToString(final LocalDate d) {
			if (d == null) {
				return null;
			}
			return d.format(ddMMyyyy);
		}

		public static LocalDate stringToLocalDate(final String s) {
			if (StringUtils.isBlank(s)) {
				return null;
			}
			return LocalDate.parse(s, ddMMyyyy);
		}

		public static String localDateTimeToString(final LocalDateTime d) {
			if (d == null) {
				return null;
			}
			return d.format(ddMMyyyyHHmm);
		}

		public static LocalDateTime stringToLocalDateTime(final String s) {
			if (StringUtils.isBlank(s)) {
				return null;
			}
			return LocalDateTime.parse(s, ddMMyyyyHHmm);
		}

		public static String localDateTimeToStringWithSeconds(final LocalDateTime d) {
			if (d == null) {
				return null;
			}
			return d.format(ddMMyyyyHHmmss);
		}

		public static LocalDateTime stringWithSecondsToLocalDateTime(final String s) {
			if (StringUtils.isBlank(s)) {
				return null;
			}
			return LocalDateTime.parse(s, ddMMyyyyHHmmss);
		}

		public static String localTimeToString(final LocalTime d) {
			if (d == null) {
				return null;
			}
			return d.format(HHmm);
		}

		public static LocalTime stringToLocalTime(final String s) {
			if (StringUtils.isBlank(s)) {
				return null;
			}
			return LocalTime.parse(s, HHmm);
		}
	}

	/**
	 * sql.Date zu Java 8 typen und zurück
	 *
	 * @author Jan Reichl
	 */
	public static final class SQLDate {
		public static java.sql.Date localDateToSqlDate(final LocalDate value) {
			if (value == null) {
				return null;
			}
			return java.sql.Date.valueOf(value);
		}

		public static LocalDate sqlDateToLocalDate(final java.sql.Date value) {
			if (value == null) {
				return null;
			}
			return value.toLocalDate();
		}

		public static Timestamp localDateTimeToTimestamp(final LocalDateTime value) {
			if (value == null) {
				return null;
			}
			return Timestamp.valueOf(value);
		}

		public static LocalDateTime timestampToLocalDateTime(final Timestamp value) {
			if (value == null) {
				return null;
			}
			return value.toLocalDateTime();
		}

		public static Time localTimeToSqlTime(final LocalTime value) {
			if (value == null) {
				return null;
			}
			return Time.valueOf(value);
		}

		public static LocalTime sqlTimeToLocalTime(final Time value) {
			if (value == null) {
				return null;
			}
			return value.toLocalTime();
		}
	}
}