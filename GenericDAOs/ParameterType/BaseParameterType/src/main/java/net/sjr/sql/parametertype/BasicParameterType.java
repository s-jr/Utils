package net.sjr.sql.parametertype;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.sql.*;

/**
 * Klasse mit den Standard Java Klassen als ParameterType
 */
@SuppressWarnings("unused")
public final class BasicParameterType implements ParameterType {
	static {
		ParameterTypeRegistry.registerParameterType(new BasicParameterType());
	}

	private BasicParameterType() {
	}

	@Override
	public int set(final @NotNull PreparedStatement pst, final int pos, final @Nullable Object value) throws SQLException {
		if (value instanceof String) {
			pst.setString(pos, (String) value);
		}
		else if (value instanceof Character) {
			pst.setString(pos, String.valueOf(value));
		}
		else if (value instanceof Integer) {
			pst.setInt(pos, (Integer) value);
		}
		else if (value instanceof Long) {
			pst.setLong(pos, (Long) value);
		}
		else if (value instanceof Byte) {
			pst.setByte(pos, (Byte) value);
		}
		else if (value instanceof Short) {
			pst.setShort(pos, (Short) value);
		}
		else if (value instanceof Double) {
			pst.setDouble(pos, (Double) value);
		}
		else if (value instanceof Float) {
			pst.setFloat(pos, (Float) value);
		}
		else if (value instanceof Boolean) {
			pst.setBoolean(pos, (Boolean) value);
		}
		else if (value instanceof Date) {
			pst.setDate(pos, (Date) value);
		}
		else if (value instanceof Time) {
			pst.setTime(pos, (Time) value);
		}
		else if (value instanceof Timestamp) {
			pst.setTimestamp(pos, (Timestamp) value);
		}
		else if (value instanceof java.util.Date) {
			pst.setTimestamp(pos, new Timestamp(((java.util.Date) value).getTime()));
		}
		else if (value instanceof BigDecimal) {
			pst.setBigDecimal(pos, (BigDecimal) value);
		}
		else if (value instanceof Enum) {
			pst.setString(pos, ((Enum<?>) value).name());
		}
		else {
			return -1;
		}
		return pos + 1;
	}
}
