package net.sjr.sql.rsloader;

import net.sjr.converterutils.JodaConverterUtils;
import net.sjr.sql.DBObject;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Jan Reichl on 18.08.17.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class JodaRsLoader extends RsLoader {
	public JodaRsLoader(final ResultSet rs, final DBObject... loadedObjects) {
		super(rs, loadedObjects);
	}

	public LocalDate nextLocalDate() throws SQLException {
		return JodaConverterUtils.SQLDate.sqlDateToJodaDate(nextDate());
	}

	public LocalDateTime nextLocalDateTime() throws SQLException {
		return JodaConverterUtils.SQLDate.timestampToJodaDateTime(nextTimestamp());
	}

	public LocalTime nextLocalTime() throws SQLException {
		return JodaConverterUtils.SQLDate.sqlTimeToJodaTime(nextTime());
	}
}
