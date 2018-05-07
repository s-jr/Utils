package net.sjr.converterutils;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Contract;
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
		@Contract("null -> null; !null -> !null")
		public static @Nullable LocalDate utilDateToLocalDate(final @Nullable Date d) {
			if (d == null) {
				return null;
			}
			return Instant.ofEpochMilli(d.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
		}
		
		@Contract("null -> null; !null -> !null")
		public static @Nullable Date localDateToUtilDate(final @Nullable LocalDate d) {
			if (d == null) {
				return null;
			}
			return Date.from(d.atStartOfDay(ZoneId.systemDefault()).toInstant());
		}
		
		@Contract("null -> null; !null -> !null")
		public static @Nullable LocalDateTime utilDateToLocalDateTime(final @Nullable Date d) {
			if (d == null) {
				return null;
			}
			return LocalDateTime.ofInstant(d.toInstant(), ZoneId.systemDefault());
		}
		
		@Contract("null -> null; !null -> !null")
		public static @Nullable Date localDateTimeToUtilDate(final @Nullable LocalDateTime d) {
			if (d == null) {
				return null;
			}
			return Date.from(d.atZone(ZoneId.systemDefault()).toInstant());
		}
		
		@Contract("null -> null; !null -> !null")
		public static @Nullable LocalTime utilDateToLocalTime(final @Nullable Date d) {
			if (d == null) {
				return null;
			}
			return Instant.ofEpochMilli(d.getTime()).atZone(ZoneId.systemDefault()).toLocalTime();
		}
		
		@Contract("null -> null; !null -> !null")
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
		
		@Contract("null -> null; !null -> !null")
		public static @Nullable String localDateToMonthYearString(final @Nullable LocalDate d) {
			if (d == null) {
				return null;
			}
			return d.format(MMMyyyy);
		}
		
		@Contract("null -> null")
		public static @Nullable LocalDate monthYearStringToLocalDate(final @Nullable String s) {
			if (StringUtils.isBlank(s)) {
				return null;
			}
			return LocalDate.parse(s, MMMyyyy);
		}
		
		@Contract("null -> null; !null -> !null")
		public static @Nullable String localDateToWeekdayString(final @Nullable LocalDate d) {
			if (d == null) {
				return null;
			}
			return d.format(EEddMMyyyy);
		}
		
		@Contract("null -> null")
		public static @Nullable LocalDate weekdayStringToLocalDate(final @Nullable String s) {
			if (StringUtils.isBlank(s)) {
				return null;
			}
			return LocalDate.parse(s, EEddMMyyyy);
		}
		
		@Contract("null -> null; !null -> !null")
		public static @Nullable String localDateToString(final @Nullable LocalDate d) {
			if (d == null) {
				return null;
			}
			return d.format(ddMMyyyy);
		}
		
		@Contract("null -> null")
		public static @Nullable LocalDate stringToLocalDate(final @Nullable String s) {
			if (StringUtils.isBlank(s)) {
				return null;
			}
			return LocalDate.parse(s, ddMMyyyy);
		}
		
		@Contract("null -> null; !null -> !null")
		public static @Nullable String localDateTimeToString(final @Nullable LocalDateTime d) {
			if (d == null) {
				return null;
			}
			return d.format(ddMMyyyyHHmm);
		}
		
		@Contract("null -> null")
		public static @Nullable LocalDateTime stringToLocalDateTime(final @Nullable String s) {
			if (StringUtils.isBlank(s)) {
				return null;
			}
			return LocalDateTime.parse(s, ddMMyyyyHHmm);
		}
		
		@Contract("null -> null; !null -> !null")
		public static @Nullable String localDateTimeToStringWithSeconds(final @Nullable LocalDateTime d) {
			if (d == null) {
				return null;
			}
			return d.format(ddMMyyyyHHmmss);
		}
		
		@Contract("null -> null")
		public static @Nullable LocalDateTime stringWithSecondsToLocalDateTime(final @Nullable String s) {
			if (StringUtils.isBlank(s)) {
				return null;
			}
			return LocalDateTime.parse(s, ddMMyyyyHHmmss);
		}
		
		@Contract("null -> null; !null -> !null")
		public static @Nullable String localTimeToString(final @Nullable LocalTime d) {
			if (d == null) {
				return null;
			}
			return d.format(HHmm);
		}
		
		@Contract("null -> null")
		public static @Nullable LocalTime stringToLocalTime(final @Nullable String s) {
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
		
		@Contract("null -> null; !null -> !null")
		public static @Nullable String localDateToString(final @Nullable LocalDate d) {
			if (d == null) {
				return null;
			}
			return d.format(ddMMyyyy);
		}
		
		@Contract("null -> null")
		public static @Nullable LocalDate stringToLocalDate(final @Nullable String s) {
			if (StringUtils.isBlank(s)) {
				return null;
			}
			return LocalDate.parse(s, ddMMyyyy);
		}
		
		@Contract("null -> null; !null -> !null")
		public static @Nullable String localDateTimeToString(final @Nullable LocalDateTime d) {
			if (d == null) {
				return null;
			}
			return d.format(ddMMyyyyHHmm);
		}
		
		@Contract("null -> null")
		public static @Nullable LocalDateTime stringToLocalDateTime(final @Nullable String s) {
			if (StringUtils.isBlank(s)) {
				return null;
			}
			return LocalDateTime.parse(s, ddMMyyyyHHmm);
		}
		
		@Contract("null -> null; !null -> !null")
		public static @Nullable String localDateTimeToStringWithSeconds(final @Nullable LocalDateTime d) {
			if (d == null) {
				return null;
			}
			return d.format(ddMMyyyyHHmmss);
		}
		
		@Contract("null -> null")
		public static @Nullable LocalDateTime stringWithSecondsToLocalDateTime(final @Nullable String s) {
			if (StringUtils.isBlank(s)) {
				return null;
			}
			return LocalDateTime.parse(s, ddMMyyyyHHmmss);
		}
		
		@Contract("null -> null; !null -> !null")
		public static @Nullable String localTimeToString(final @Nullable LocalTime d) {
			if (d == null) {
				return null;
			}
			return d.format(HHmm);
		}
		
		@Contract("null -> null")
		public static @Nullable LocalTime stringToLocalTime(final @Nullable String s) {
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
		@Contract("null -> null; !null -> !null")
		public static @Nullable java.sql.Date localDateToSqlDate(final @Nullable LocalDate value) {
			if (value == null) {
				return null;
			}
			return java.sql.Date.valueOf(value);
		}
		
		@Contract("null -> null; !null -> !null")
		public static @Nullable LocalDate sqlDateToLocalDate(final @Nullable java.sql.Date value) {
			if (value == null) {
				return null;
			}
			return value.toLocalDate();
		}
		
		@Contract("null -> null; !null -> !null")
		public static @Nullable Timestamp localDateTimeToTimestamp(final @Nullable LocalDateTime value) {
			if (value == null) {
				return null;
			}
			return Timestamp.valueOf(value);
		}
		
		@Contract("null -> null; !null -> !null")
		public static @Nullable LocalDateTime timestampToLocalDateTime(final @Nullable Timestamp value) {
			if (value == null) {
				return null;
			}
			return value.toLocalDateTime();
		}
		
		@Contract("null -> null; !null -> !null")
		public static @Nullable Time localTimeToSqlTime(final @Nullable LocalTime value) {
			if (value == null) {
				return null;
			}
			return Time.valueOf(value);
		}
		
		@Contract("null -> null; !null -> !null")
		public static @Nullable LocalTime sqlTimeToLocalTime(final @Nullable Time value) {
			if (value == null) {
				return null;
			}
			return value.toLocalTime();
		}
	}
}