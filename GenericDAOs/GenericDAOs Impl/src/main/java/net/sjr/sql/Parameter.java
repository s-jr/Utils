package net.sjr.sql;


import net.sjr.sql.exceptions.NoNullTypeException;
import net.sjr.sql.exceptions.UnsupportedValueException;
import net.sjr.sql.parametertype.ParameterType;
import net.sjr.sql.parametertype.ParameterTypeRegistry;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

/**
 * Parameter welcher in ein {@link PreparedStatement} eingesetzt werden kann
 */
@SuppressWarnings("WeakerAccess")
public class Parameter {
	final Object value;
	private final Integer type;
	
	static {
		loadClass("net.sjr.sql.parametertype.BasicParameterType");
		loadClass("net.sjr.sql.parametertype.Java8ParameterType");
		loadClass("net.sjr.sql.parametertype.JodaParameterType");
	}
	
	/**
	 * Lädt einen ParameterType, welcher sich dann selber registrieren muss
	 *
	 * @param name der full qualified Klassenname
	 */
	private static void loadClass(String name) {
		try {
			Class.forName(name);
		}
		catch (ClassNotFoundException ignored) {
		}
	}
	
	/**
	 * Erstellt einen neuen Parameter ohne Typ. Dadurch darf er nicht {@code null} sein
	 *
	 * @param value der Wert des Parameters
	 */
	public Parameter(final Object value) {
		this(value, null);
	}
	
	/**
	 * Erstellt einen neuen Parameter mit Typ. Dadurch darf er {@code null} sein
	 *
	 * @param value der Wert des Parameters
	 * @param type  der Typ aus der {@link java.sql.Types} Klasse, welcher im {@code null} Fall gebraucht wird
	 */
	public Parameter(final Object value, final Integer type) {
		this.value = value;
		this.type = type;
	}
	
	/**
	 * Setzt einen Parameter in das Statement ein
	 *
	 * @param pst      Statement in welches eingesetzt werden soll
	 * @param position Position an die eingesetzt werden soll
	 *
	 * @return neue Position für den nächsten Parameter
	 *
	 * @throws SQLException wenn eine SQLException aufgetreten ist
	 */
	public int setParameter(final PreparedStatement pst, final int position) throws SQLException {
		Object actualValue = value;
		while (actualValue instanceof DBConvertable) {
			if (actualValue instanceof DBEnum) actualValue = ((DBEnum) value).getDBIdentifier();
			else if (actualValue instanceof DBObject) actualValue = ((DBObject) value).getPrimary();
			else if (actualValue instanceof DBColumn) actualValue = ((DBColumn) value).toColumn();
			else throw new UnsupportedValueException(actualValue.getClass());
		}
		
		
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
		return "Parameter [value=" + value + ", type=" + type + ']';
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Parameter parameter = (Parameter) o;
		return Objects.equals(value, parameter.value) &&
				Objects.equals(type, parameter.type);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(value, type);
	}
}
