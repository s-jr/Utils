package net.sjr.sql.rsloader;

import net.sjr.converterutils.JodaConverterUtils;
import net.sjr.sql.DBObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import java.sql.ResultSet;

/**
 * Erweitert den {@link RsLoader} um die Joda Time Klassen
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class JodaRsLoader extends RsLoader {
	public JodaRsLoader(final @NotNull ResultSet rs, final DBObject... loadedObjects) {
		super(rs, loadedObjects);
	}
	
	/**
	 * Gibt das {@code LocalDate} an der aktuellen Position zurück und geht eine Position weiter
	 *
	 * @return das aktuelle {@code LocalDate}
	 */
	public @Nullable LocalDate nextLocalDate() {
		return JodaConverterUtils.SQLDate.sqlDateToJodaDate(nextDate());
	}
	
	/**
	 * Gibt die {@code LocalDateTime} an der aktuellen Position zurück und geht eine Position weiter
	 * @return die aktuelle {@code LocalDateTime}
	 */
	public @Nullable LocalDateTime nextLocalDateTime() {
		return JodaConverterUtils.SQLDate.timestampToJodaDateTime(nextTimestamp());
	}
	
	/**
	 * Gibt die {@code LocalTime} an der aktuellen Position zurück und geht eine Position weiter
	 * @return die aktuelle {@code LocalTime}
	 */
	public @Nullable LocalTime nextLocalTime() {
		return JodaConverterUtils.SQLDate.sqlTimeToJodaTime(nextTime());
	}
}
