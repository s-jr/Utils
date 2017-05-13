package net.sjr.sql.parametertpye;

import net.sjr.converterutils.JodaConverterUtils;
import net.sjr.sql.parametertype.ParameterType;
import net.sjr.sql.parametertype.ParameterTypeRegistry;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Jan Reichl on 11.05.17.
 */
public class JodaParameterType implements ParameterType {
	static {
		ParameterTypeRegistry.registerParameterType(new JodaParameterType());
	}

	private JodaParameterType() {
	}

	@Override
	public int set(PreparedStatement pst, int pos, Object value) throws SQLException {
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
