package net.sjr.converterutils;

import org.apache.commons.lang3.StringUtils;
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
		public static LocalDate utilDateToJodaDate(final Date d) {
			if (d == null) {
				return null;
			}
			return LocalDate.fromDateFields(d);
		}

		public static Date jodaDateToUtilDate(final LocalDate d) {
			if (d == null) {
				return null;
			}
			return d.toDate();
		}

		public static LocalDateTime utilDateToJodaDateTime(final Date d) {
			if (d == null) {
				return null;
			}
			return LocalDateTime.fromDateFields(d);
		}

		public static Date jodaDateTimeToUtilDate(final LocalDateTime d) {
			if (d == null) {
				return null;
			}
			return d.toDate();
		}

		public static LocalTime utilDateToJodaTime(final Date d) {
			if (d == null) {
				return null;
			}
			return LocalTime.fromDateFields(d);
		}

		public static Date jodaTimeToUtilDate(final LocalTime d) {
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

		public static String jodaDateToMonthYearString(final LocalDate d) {
			if (d == null) {
				return null;
			}
			return MMMyyyy.print(d);
		}

		public static LocalDate monthYearStringToJodaDate(final String s) {
			if (StringUtils.isBlank(s)) {
				return null;
			}
			return MMMyyyy.parseLocalDate(s);
		}

		public static String jodaDateToWeekdayString(final LocalDate d) {
			if (d == null) {
				return null;
			}
			return EEddMMyyyy.print(d);
		}

		public static LocalDate weekdayStringToJodaDate(final String s) {
			if (StringUtils.isBlank(s)) {
				return null;
			}
			return EEddMMyyyy.parseLocalDate(s);
		}

		public static String jodaDateToString(final LocalDate d) {
			if (d == null) {
				return null;
			}
			return ddMMyyyy.print(d);
		}

		public static LocalDate stringToJodaDate(final String s) {
			if (StringUtils.isBlank(s)) {
				return null;
			}
			return ddMMyyyy.parseLocalDate(s);
		}

		public static String jodaDateTimeToString(final LocalDateTime d) {
			if (d == null) {
				return null;
			}
			return ddMMyyyyHHmm.print(d);
		}

		public static LocalDateTime stringToJodaDateTime(final String s) {
			if (StringUtils.isBlank(s)) {
				return null;
			}
			return ddMMyyyyHHmm.parseLocalDateTime(s);
		}

		public static String jodaDateTimeToStringWithSeconds(final LocalDateTime d) {
			if (d == null) {
				return null;
			}
			return ddMMyyyyHHmmss.print(d);
		}

		public static LocalDateTime stringWithSecondsToJodaDateTime(final String s) {
			if (StringUtils.isBlank(s)) {
				return null;
			}
			return ddMMyyyyHHmmss.parseLocalDateTime(s);
		}

		public static String jodaTimeToString(final LocalTime d) {
			if (d == null) {
				return null;
			}
			return HHmm.print(d);
		}

		public static LocalTime stringToJodaTime(final String s) {
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

		public static String jodaDateToString(final LocalDate d) {
			if (d == null) {
				return null;
			}
			return ddMMyyyy.print(d);
		}

		public static LocalDate stringToJodaDate(final String s) {
			if (StringUtils.isBlank(s)) {
				return null;
			}
			return ddMMyyyy.parseLocalDate(s);
		}

		public static String jodaDateTimeToString(final LocalDateTime d) {
			if (d == null) {
				return null;
			}
			return ddMMyyyyHHmm.print(d);
		}

		public static LocalDateTime stringToJodaDateTime(final String s) {
			if (StringUtils.isBlank(s)) {
				return null;
			}
			return ddMMyyyyHHmm.parseLocalDateTime(s);
		}

		public static String jodaDateTimeToStringWithSeconds(final LocalDateTime d) {
			if (d == null) {
				return null;
			}
			return ddMMyyyyHHmmss.print(d);
		}

		public static LocalDateTime stringWithSecondsToJodaDateTime(final String s) {
			if (StringUtils.isBlank(s)) {
				return null;
			}
			return ddMMyyyyHHmmss.parseLocalDateTime(s);
		}

		public static String jodaTimeToString(final LocalTime d) {
			if (d == null) {
				return null;
			}
			return HHmm.print(d);
		}

		public static LocalTime stringToJodaTime(final String s) {
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
		public static java.sql.Date jodaDateToSqlDate(final LocalDate value) {
			if (value == null) {
				return null;
			}
			return new java.sql.Date(value.toDate().getTime());
		}

		public static LocalDate sqlDateToJodaDate(final java.sql.Date value) {
			if (value == null) {
				return null;
			}
			return new LocalDate(value.getTime());
		}

		public static Timestamp jodaDateTimeToTimestamp(final LocalDateTime value) {
			if (value == null) {
				return null;
			}
			return new Timestamp(value.toDateTime().getMillis());
		}

		public static LocalDateTime timestampToJodaDateTime(final Timestamp value) {
			if (value == null) {
				return null;
			}
			return new LocalDateTime(value.getTime());
		}

		public static Time jodaTimeToSqlTime(final LocalTime value) {
			if (value == null) {
				return null;
			}
			return new Time(value.getMillisOfDay());
		}

		public static LocalTime sqlTimeToJodaTime(final Time value) {
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
		private static final PeriodFormatter ARBEITSZEITFORMAT = new PeriodFormatterBuilder()
				.appendHours()
				.appendSuffix("h ")
				.appendMinutes()
				.appendSuffix("min").toFormatter();

		public static String jodaDurationToString(final Duration d) {
			if (d.isShorterThan(new Duration(60000))) {
				return "0min";
			}
			return ARBEITSZEITFORMAT.print(d.toPeriod());
		}

		public static Duration stringToJodaDuration(final String s) {
			return ARBEITSZEITFORMAT.parsePeriod(s).toStandardDuration();
		}
	}
}