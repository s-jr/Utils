package net.sjr.sql.parametertype;

import net.sjr.converterutils.Java8ConverterUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Klasse mit den Java 8 Date Klassen als ParameterType
 */
@SuppressWarnings("unused")
public class Java8ParameterType implements ParameterType {
	static {
		ParameterTypeRegistry.registerParameterType(new Java8ParameterType());
	}

	private Java8ParameterType() {
	}

	@Override
	public int set(PreparedStatement pst, int pos, Object value) throws SQLException {
		if (value instanceof LocalDate) {
			pst.setDate(pos, Java8ConverterUtils.SQLDate.localDateToSqlDate((LocalDate) value));
		}
		else if (value instanceof LocalDateTime) {
			pst.setTimestamp(pos, Java8ConverterUtils.SQLDate.localDateTimeToTimestamp((LocalDateTime) value));
		}
		else if (value instanceof LocalTime) {
			pst.setTime(pos, Java8ConverterUtils.SQLDate.localTimeToSqlTime((LocalTime) value));
		}
		else {
			return -1;
		}
		return pos + 1;
	}
}
