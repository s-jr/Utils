package net.sjr.sql.parametertype;

import net.sjr.converterutils.JodaConverterUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Klasse mit den Joda Time Klassen als ParameterType
 */
@SuppressWarnings("unused")
public class JodaParameterType implements ParameterType {
	static {
		ParameterTypeRegistry.registerParameterType(new JodaParameterType());
	}

	private JodaParameterType() {
	}

	@Override
	public int set(final @NotNull PreparedStatement pst, final int pos, final @Nullable Object value) throws SQLException {
		if (value instanceof LocalDate) {
			pst.setDate(pos, JodaConverterUtils.SQLDate.jodaDateToSqlDate((LocalDate) value));
		}
		else if (value instanceof LocalDateTime) {
			pst.setTimestamp(pos, JodaConverterUtils.SQLDate.jodaDateTimeToTimestamp((LocalDateTime) value));
		}
		else if (value instanceof LocalTime) {
			pst.setTime(pos, JodaConverterUtils.SQLDate.jodaTimeToSqlTime((LocalTime) value));
		}
		else {
			return -1;
		}
		return pos + 1;
	}
}
