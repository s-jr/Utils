package net.sjr.sql;


import net.sjr.sql.exceptions.NoNullTypeException;
import net.sjr.sql.exceptions.UnsupportedValueException;
import net.sjr.sql.parametertype.ParameterType;
import net.sjr.sql.parametertype.ParameterTypeRegistry;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Parameter {
	private final Object value;
	private final Integer type;

	static {
		loadClass("net.sjr.sql.parametertype.BasicParameterType");
		loadClass("net.sjr.sql.parametertype.Java8ParameterType");
		loadClass("net.sjr.sql.parametertype.JodaParameterType");
	}

	private static void loadClass(String s) {
		try {
			Class.forName(s);
		}
		catch (ClassNotFoundException ignored) {
		}
	}

	public Parameter(final Object value) {
		this(value, null);
	}

	public Parameter(final Object value, final Integer type) {
		this.value = value;
		this.type = type;
	}

	/**
	 * Setzt einen Parameter in das Statement ein
	 *
	 * @param pst      Statement in welches eingesetzt werden soll
	 * @param position Position an die eingesetzt werden soll
	 * @return neue Position für den nächsten Parameter
	 * @throws SQLException
	 */
	public int setParameter(final PreparedStatement pst, final int position) throws SQLException {
		final Object actualValue;
		if (value instanceof DBEnum) actualValue = ((DBEnum) value).getDBIdentifier();
		else if (value instanceof DBObject) actualValue = ((DBObject) value).getPrimary();
		else actualValue = value;

		if (actualValue == null) {
			if (type != null) {
				pst.setNull(position, type);
				return position + 1;
			}
			else {
				throw new NoNullTypeException(pst, position);
			}
		}
		else {
			for (ParameterType type : ParameterTypeRegistry.PARAMETER_TYPES) {
				int newPos = type.set(pst, position, actualValue);
				if (newPos > 0) return newPos;
			}
		}
		throw new UnsupportedValueException(actualValue.getClass());
	}

	@Override
	public String toString() {
		return "Parameter [value=" + value + ", type=" + type + "]";
	}
}
