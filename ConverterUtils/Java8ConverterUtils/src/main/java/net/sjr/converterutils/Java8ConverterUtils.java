package net.sjr.converterutils;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

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
@SuppressWarnings({"WeakerAccess", "unused"})
public final class Java8ConverterUtils {

	/**
	 * util.Date zu Java 8 typen und zurück
	 *
	 * @author Jan Reichl
	 */
	public static final class UtilJava8Date {
		public static @Nullable LocalDate utilDateToLocalDate(final @Nullable Date d) {
			if (d == null) {
				return null;
			}
			return Instant.ofEpochMilli(d.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
		}
		
		public static @Nullable Date localDateToUtilDate(final @Nullable LocalDate d) {
			if (d == null) {
				return null;
			}
			return Date.from(d.atStartOfDay(ZoneId.systemDefault()).toInstant());
		}
		
		public static @Nullable LocalDateTime utilDateToLocalDateTime(final @Nullable Date d) {
			if (d == null) {
				return null;
			}
			return LocalDateTime.ofInstant(d.toInstant(), ZoneId.systemDefault());
		}
		
		public static @Nullable Date localDateTimeToUtilDate(final @Nullable LocalDateTime d) {
			if (d == null) {
				return null;
			}
			return Date.from(d.atZone(ZoneId.systemDefault()).toInstant());
		}
		
		public static @Nullable LocalTime utilDateToLocalTime(final @Nullable Date d) {
			if (d == null) {
				return null;
			}
			return Instant.ofEpochMilli(d.getTime()).atZone(ZoneId.systemDefault()).toLocalTime();
		}
		
		public static @Nullable Date localTimeToUtilDate(final @Nullable LocalTime d) {
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
		
		public static @Nullable String localDateToMonthYearString(final @Nullable LocalDate d) {
			if (d == null) {
				return null;
			}
			return d.format(MMMyyyy);
		}
		
		public static LocalDate monthYearStringToLocalDate(final @Nullable String s) {
			if (StringUtils.isBlank(s)) {
				return null;
			}
			return LocalDate.parse(s, MMMyyyy);
		}
		
		public static @Nullable String localDateToWeekdayString(final @Nullable LocalDate d) {
			if (d == null) {
				return null;
			}
			return d.format(EEddMMyyyy);
		}
		
		public static LocalDate weekdayStringToLocalDate(final @Nullable String s) {
			if (StringUtils.isBlank(s)) {
				return null;
			}
			return LocalDate.parse(s, EEddMMyyyy);
		}
		
		public static @Nullable String localDateToString(final @Nullable LocalDate d) {
			if (d == null) {
				return null;
			}
			return d.format(ddMMyyyy);
		}
		
		public static LocalDate stringToLocalDate(final @Nullable String s) {
			if (StringUtils.isBlank(s)) {
				return null;
			}
			return LocalDate.parse(s, ddMMyyyy);
		}
		
		public static @Nullable String localDateTimeToString(final @Nullable LocalDateTime d) {
			if (d == null) {
				return null;
			}
			return d.format(ddMMyyyyHHmm);
		}
		
		public static LocalDateTime stringToLocalDateTime(final @Nullable String s) {
			if (StringUtils.isBlank(s)) {
				return null;
			}
			return LocalDateTime.parse(s, ddMMyyyyHHmm);
		}
		
		public static @Nullable String localDateTimeToStringWithSeconds(final @Nullable LocalDateTime d) {
			if (d == null) {
				return null;
			}
			return d.format(ddMMyyyyHHmmss);
		}
		
		public static LocalDateTime stringWithSecondsToLocalDateTime(final @Nullable String s) {
			if (StringUtils.isBlank(s)) {
				return null;
			}
			return LocalDateTime.parse(s, ddMMyyyyHHmmss);
		}
		
		public static @Nullable String localTimeToString(final @Nullable LocalTime d) {
			if (d == null) {
				return null;
			}
			return d.format(HHmm);
		}
		
		public static LocalTime stringToLocalTime(final @Nullable String s) {
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
		
		public static @Nullable String localDateToString(final @Nullable LocalDate d) {
			if (d == null) {
				return null;
			}
			return d.format(ddMMyyyy);
		}
		
		public static LocalDate stringToLocalDate(final @Nullable String s) {
			if (StringUtils.isBlank(s)) {
				return null;
			}
			return LocalDate.parse(s, ddMMyyyy);
		}
		
		public static @Nullable String localDateTimeToString(final @Nullable LocalDateTime d) {
			if (d == null) {
				return null;
			}
			return d.format(ddMMyyyyHHmm);
		}
		
		public static LocalDateTime stringToLocalDateTime(final @Nullable String s) {
			if (StringUtils.isBlank(s)) {
				return null;
			}
			return LocalDateTime.parse(s, ddMMyyyyHHmm);
		}
		
		public static @Nullable String localDateTimeToStringWithSeconds(final @Nullable LocalDateTime d) {
			if (d == null) {
				return null;
			}
			return d.format(ddMMyyyyHHmmss);
		}
		
		public static LocalDateTime stringWithSecondsToLocalDateTime(final @Nullable String s) {
			if (StringUtils.isBlank(s)) {
				return null;
			}
			return LocalDateTime.parse(s, ddMMyyyyHHmmss);
		}
		
		public static @Nullable String localTimeToString(final @Nullable LocalTime d) {
			if (d == null) {
				return null;
			}
			return d.format(HHmm);
		}
		
		public static LocalTime stringToLocalTime(final @Nullable String s) {
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
		public static @Nullable java.sql.Date localDateToSqlDate(final @Nullable LocalDate value) {
			if (value == null) {
				return null;
			}
			return java.sql.Date.valueOf(value);
		}
		
		public static @Nullable LocalDate sqlDateToLocalDate(final @Nullable java.sql.Date value) {
			if (value == null) {
				return null;
			}
			return value.toLocalDate();
		}
		
		public static @Nullable Timestamp localDateTimeToTimestamp(final @Nullable LocalDateTime value) {
			if (value == null) {
				return null;
			}
			return Timestamp.valueOf(value);
		}
		
		public static @Nullable LocalDateTime timestampToLocalDateTime(final @Nullable Timestamp value) {
			if (value == null) {
				return null;
			}
			return value.toLocalDateTime();
		}
		
		public static @Nullable Time localTimeToSqlTime(final @Nullable LocalTime value) {
			if (value == null) {
				return null;
			}
			return Time.valueOf(value);
		}
		
		public static @Nullable LocalTime sqlTimeToLocalTime(final @Nullable Time value) {
			if (value == null) {
				return null;
			}
			return value.toLocalTime();
		}
	}
}