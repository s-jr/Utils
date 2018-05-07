package net.sjr.converterutils;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Diverse Konverter für Datumstypen
 *
 * @author Jan Reichl
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public final class JodaConverterUtils {

	/**
	 * util.Date zu JodaTime typen und zurück
	 *
	 * @author Jan Reichl
	 */
	public static final class UtilJodaDate {
		@Contract("null -> null; !null -> !null")
		public static @Nullable LocalDate utilDateToJodaDate(final @Nullable Date d) {
			if (d == null) {
				return null;
			}
			return LocalDate.fromDateFields(d);
		}
		
		@Contract("null -> null; !null -> !null")
		public static @Nullable Date jodaDateToUtilDate(final @Nullable LocalDate d) {
			if (d == null) {
				return null;
			}
			return d.toDate();
		}
		
		@Contract("null -> null; !null -> !null")
		public static @Nullable LocalDateTime utilDateToJodaDateTime(final @Nullable Date d) {
			if (d == null) {
				return null;
			}
			return LocalDateTime.fromDateFields(d);
		}
		
		@Contract("null -> null; !null -> !null")
		public static @Nullable Date jodaDateTimeToUtilDate(final @Nullable LocalDateTime d) {
			if (d == null) {
				return null;
			}
			return d.toDate();
		}
		
		@Contract("null -> null; !null -> !null")
		public static @Nullable LocalTime utilDateToJodaTime(final @Nullable Date d) {
			if (d == null) {
				return null;
			}
			return LocalTime.fromDateFields(d);
		}
		
		@Contract("null -> null; !null -> !null")
		public static @Nullable Date jodaTimeToUtilDate(final @Nullable LocalTime d) {
			if (d == null) {
				return null;
			}
			return d.toDateTimeToday().toDate();
		}
	}

	/**
	 * Schöner lesbarer String zu JodaTime typen und zurück
	 *
	 * @author Jan Reichl
	 */
	public static final class PrettyString {
		private static final DateTimeFormatter MMMyyyy = DateTimeFormat.forPattern("MMMM yyyy");
		private static final DateTimeFormatter EEddMMyyyy = DateTimeFormat.forPattern("EE dd.MM.yyyy");
		private static final DateTimeFormatter ddMMyyyy = DateTimeFormat.forPattern("dd.MM.yyyy");
		private static final DateTimeFormatter ddMMyyyyHHmm = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm");
		private static final DateTimeFormatter ddMMyyyyHHmmss = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm:sss");
		private static final DateTimeFormatter HHmm = DateTimeFormat.forPattern("HH:mm");
		
		@Contract("null -> null; !null -> !null")
		public static @Nullable String jodaDateToMonthYearString(final @Nullable LocalDate d) {
			if (d == null) {
				return null;
			}
			return MMMyyyy.print(d);
		}
		
		@Contract("null -> null")
		public static @Nullable LocalDate monthYearStringToJodaDate(final @Nullable String s) {
			if (StringUtils.isBlank(s)) {
				return null;
			}
			return MMMyyyy.parseLocalDate(s);
		}
		
		@Contract("null -> null; !null -> !null")
		public static @Nullable String jodaDateToWeekdayString(final @Nullable LocalDate d) {
			if (d == null) {
				return null;
			}
			return EEddMMyyyy.print(d);
		}
		
		@Contract("null -> null")
		public static @Nullable LocalDate weekdayStringToJodaDate(final @Nullable String s) {
			if (StringUtils.isBlank(s)) {
				return null;
			}
			return EEddMMyyyy.parseLocalDate(s);
		}
		
		@Contract("null -> null; !null -> !null")
		public static @Nullable String jodaDateToString(final @Nullable LocalDate d) {
			if (d == null) {
				return null;
			}
			return ddMMyyyy.print(d);
		}
		
		@Contract("null -> null")
		public static @Nullable LocalDate stringToJodaDate(final @Nullable String s) {
			if (StringUtils.isBlank(s)) {
				return null;
			}
			return ddMMyyyy.parseLocalDate(s);
		}
		
		@Contract("null -> null; !null -> !null")
		public static @Nullable String jodaDateTimeToString(final @Nullable LocalDateTime d) {
			if (d == null) {
				return null;
			}
			return ddMMyyyyHHmm.print(d);
		}
		
		@Contract("null -> null")
		public static @Nullable LocalDateTime stringToJodaDateTime(final @Nullable String s) {
			if (StringUtils.isBlank(s)) {
				return null;
			}
			return ddMMyyyyHHmm.parseLocalDateTime(s);
		}
		
		@Contract("null -> null; !null -> !null")
		public static @Nullable String jodaDateTimeToStringWithSeconds(final @Nullable LocalDateTime d) {
			if (d == null) {
				return null;
			}
			return ddMMyyyyHHmmss.print(d);
		}
		
		@Contract("null -> null")
		public static @Nullable LocalDateTime stringWithSecondsToJodaDateTime(final @Nullable String s) {
			if (StringUtils.isBlank(s)) {
				return null;
			}
			return ddMMyyyyHHmmss.parseLocalDateTime(s);
		}
		
		@Contract("null -> null; !null -> !null")
		public static @Nullable String jodaTimeToString(final @Nullable LocalTime d) {
			if (d == null) {
				return null;
			}
			return HHmm.print(d);
		}
		
		@Contract("null -> null")
		public static @Nullable LocalTime stringToJodaTime(final @Nullable String s) {
			if (StringUtils.isBlank(s)) {
				return null;
			}
			return HHmm.parseLocalTime(s);
		}
	}

	/**
	 * String für SQL-Datenbanken zu JodaTime typen und zurück
	 *
	 * @author Jan Reichl
	 */
	public static final class SQLString {
		private static final DateTimeFormatter ddMMyyyy = DateTimeFormat.forPattern("yyyy-MM-dd");
		private static final DateTimeFormatter ddMMyyyyHHmm = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");
		private static final DateTimeFormatter ddMMyyyyHHmmss = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
		private static final DateTimeFormatter HHmm = DateTimeFormat.forPattern("HH:mm");
		
		@Contract("null -> null; !null -> !null")
		public static @Nullable String jodaDateToString(final @Nullable LocalDate d) {
			if (d == null) {
				return null;
			}
			return ddMMyyyy.print(d);
		}
		
		@Contract("null -> null")
		public static @Nullable LocalDate stringToJodaDate(final @Nullable String s) {
			if (StringUtils.isBlank(s)) {
				return null;
			}
			return ddMMyyyy.parseLocalDate(s);
		}
		
		@Contract("null -> null; !null -> !null")
		public static @Nullable String jodaDateTimeToString(final @Nullable LocalDateTime d) {
			if (d == null) {
				return null;
			}
			return ddMMyyyyHHmm.print(d);
		}
		
		@Contract("null -> null")
		public static @Nullable LocalDateTime stringToJodaDateTime(final @Nullable String s) {
			if (StringUtils.isBlank(s)) {
				return null;
			}
			return ddMMyyyyHHmm.parseLocalDateTime(s);
		}
		
		@Contract("null -> null; !null -> !null")
		public static @Nullable String jodaDateTimeToStringWithSeconds(final @Nullable LocalDateTime d) {
			if (d == null) {
				return null;
			}
			return ddMMyyyyHHmmss.print(d);
		}
		
		@Contract("null -> null")
		public static @Nullable LocalDateTime stringWithSecondsToJodaDateTime(final @Nullable String s) {
			if (StringUtils.isBlank(s)) {
				return null;
			}
			return ddMMyyyyHHmmss.parseLocalDateTime(s);
		}
		
		@Contract("null -> null; !null -> !null")
		public static @Nullable String jodaTimeToString(final @Nullable LocalTime d) {
			if (d == null) {
				return null;
			}
			return HHmm.print(d);
		}
		
		@Contract("null -> null")
		public static @Nullable LocalTime stringToJodaTime(final @Nullable String s) {
			if (StringUtils.isBlank(s)) {
				return null;
			}
			return HHmm.parseLocalTime(s);
		}
	}

	/**
	 * sql.Date zu JodaTime typen und zurück
	 *
	 * @author Jan Reichl
	 */
	public static final class SQLDate {
		@Contract("null -> null; !null -> !null")
		public static @Nullable java.sql.Date jodaDateToSqlDate(final @Nullable LocalDate value) {
			if (value == null) {
				return null;
			}
			return new java.sql.Date(value.toDate().getTime());
		}
		
		@Contract("null -> null; !null -> !null")
		public static @Nullable LocalDate sqlDateToJodaDate(final @Nullable java.sql.Date value) {
			if (value == null) {
				return null;
			}
			return new LocalDate(value.getTime());
		}
		
		@Contract("null -> null; !null -> !null")
		public static @Nullable Timestamp jodaDateTimeToTimestamp(final @Nullable LocalDateTime value) {
			if (value == null) {
				return null;
			}
			return new Timestamp(value.toDateTime().getMillis());
		}
		
		@Contract("null -> null; !null -> !null")
		public static @Nullable LocalDateTime timestampToJodaDateTime(final @Nullable Timestamp value) {
			if (value == null) {
				return null;
			}
			return new LocalDateTime(value.getTime());
		}
		
		@Contract("null -> null; !null -> !null")
		public static @Nullable Time jodaTimeToSqlTime(final @Nullable LocalTime value) {
			if (value == null) {
				return null;
			}
			return new Time(value.getMillisOfDay());
		}
		
		@Contract("null -> null; !null -> !null")
		public static @Nullable LocalTime sqlTimeToJodaTime(final @Nullable Time value) {
			if (value == null) {
				return null;
			}
			return new LocalTime(value.getTime());
		}
	}

	/**
	 * Duration zu String und zurück
	 *
	 * @author Jan Reichl
	 */
	public static class Durations {
		private static final PeriodFormatter DURATIONFORMAT = new PeriodFormatterBuilder()
				.appendHours()
				.appendSuffix("h ")
				.appendMinutes()
				.appendSuffix("min").toFormatter();
		
		@Contract("null -> null; !null -> !null")
		public static @Nullable String jodaDurationToString(final Duration d) {
			if (d == null) {
				return null;
			}
			if (d.isShorterThan(new Duration(60000))) {
				return "0min";
			}
			return DURATIONFORMAT.print(d.toPeriod());
		}
		
		@Contract("null -> null; !null -> !null")
		public static @Nullable Duration stringToJodaDuration(final @Nullable String s) {
			if (s == null) {
				return null;
			}
			return DURATIONFORMAT.parsePeriod(s).toStandardDuration();
		}
	}
}