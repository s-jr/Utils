package net.sjr.sql.rsloader;

import net.sjr.sql.DAOBase;
import net.sjr.sql.DBEnum;
import net.sjr.sql.DBObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Klasse mit diversen Methoden, die bei dem Arbeiten mit {@link ResultSet}s hilfreich sind
 */
@SuppressWarnings("WeakerAccess")
public class RsUtils {
	
	/**
	 * lädt ein Objekt aus den bereits geladenen Objekten oder zur not aus der DAO
	 *
	 * @param rsPos         Position der ID im ResultSet
	 * @param rs            das ResultSet mit der ID
	 * @param dao           das DAO mit dem zur not gesucht werden soll
	 * @param loadedObjects die geladenen Objekte, die durchsucht werden sollen
	 * @param <T>           der Typ der DAO
	 * @param <P>           der Primary Typ des Types der DAO
	 *
	 * @return das (evtl. geladene) Objekt oder null, wenn nicht vorhanden
	 *
	 * @throws SQLException wenn ein SQL Fehler auftrat
	 */
	@SuppressWarnings({"unchecked", "WeakerAccess"})
	public static @Nullable <T extends DBObject<P>, P extends Number> T loadedObjectsOrNull(final int rsPos, final @NotNull ResultSet rs, final @NotNull DAOBase<T, P> dao, final DBObject... loadedObjects)
			throws SQLException {
		P id = dao.getPrimary(rs, rsPos);
		if (rs.wasNull() || id == null) {
			return null;
		}
		
		if (loadedObjects != null) {
			for (final DBObject o : loadedObjects) {
				if (o != null && o.getPrimary() != null && o.getPrimary().equals(id)) {
					Type type = dao.getClass();
					while (type instanceof Class) {
						type = ((Class) type).getGenericSuperclass();
					}
					Class<T> genericClass = (Class<T>) ((ParameterizedType) type).getActualTypeArguments()[0];
					if (genericClass.isInstance(o)) {
						return (T) o;
					}
				}
			}
		}
		return dao.loadFromID(id);
	}
	
	/**
	 * holt das Enum von seinem Identifier
	 *
	 * @param identifier der Identifier
	 * @param enumClass  die Enum Klasse
	 * @param <E>        Der Typ der Enum Klasse
	 * @param <T>        der Typ des Identifiers
	 *
	 * @return die Enum oder null, wenn nicht vorhanden
	 */
	public static @Nullable <E extends DBEnum<T>, T> E getFromDBIdentifier(final @Nullable T identifier, final @NotNull Class<E> enumClass) {
		E[] values = enumClass.getEnumConstants();
		for (final E value : values) {
			if (value.getDBIdentifier().equals(identifier)) {
				return value;
			}
		}
		return null;
	}
	
	/**
	 * Holt ein nullbares Boolean aus dem ResultSet
	 *
	 * @param rs  das ResultSet aus welchem geholt werden soll
	 * @param pos die Position in dem ResultSet
	 *
	 * @return der Wert oder null
	 *
	 * @throws SQLException wenn ein SQL Fehler auftrat
	 */
	public static @Nullable Boolean getNullableBoolean(final @NotNull ResultSet rs, final int pos) throws SQLException {
		boolean result = rs.getBoolean(pos);
		if (rs.wasNull()) return null;
		return result;
	}
	
	/**
	 * Holt ein nullbares Byte aus dem ResultSet
	 *
	 * @param rs  das ResultSet aus welchem geholt werden soll
	 * @param pos die Position in dem ResultSet
	 *
	 * @return der Wert oder null
	 *
	 * @throws SQLException wenn ein SQL Fehler auftrat
	 */
	public static @Nullable Byte getNullableByte(final @NotNull ResultSet rs, final int pos) throws SQLException {
		byte result = rs.getByte(pos);
		if (rs.wasNull()) return null;
		return result;
	}
	
	/**
	 * Holt ein nullbares Short aus dem ResultSet
	 *
	 * @param rs  das ResultSet aus welchem geholt werden soll
	 * @param pos die Position in dem ResultSet
	 *
	 * @return der Wert oder null
	 *
	 * @throws SQLException wenn ein SQL Fehler auftrat
	 */
	public static @Nullable Short getNullableShort(final @NotNull ResultSet rs, final int pos) throws SQLException {
		short result = rs.getShort(pos);
		if (rs.wasNull()) return null;
		return result;
	}
	
	/**
	 * Holt ein nullbares Int aus dem ResultSet
	 *
	 * @param rs  das ResultSet aus welchem geholt werden soll
	 * @param pos die Position in dem ResultSet
	 *
	 * @return der Wert oder null
	 *
	 * @throws SQLException wenn ein SQL Fehler auftrat
	 */
	public static @Nullable Integer getNullableInt(final @NotNull ResultSet rs, final int pos) throws SQLException {
		int result = rs.getInt(pos);
		if (rs.wasNull()) return null;
		return result;
	}
	
	/**
	 * Holt ein nullbares Long aus dem ResultSet
	 *
	 * @param rs  das ResultSet aus welchem geholt werden soll
	 * @param pos die Position in dem ResultSet
	 *
	 * @return der Wert oder null
	 *
	 * @throws SQLException wenn ein SQL Fehler auftrat
	 */
	public static @Nullable Long getNullableLong(final @NotNull ResultSet rs, final int pos) throws SQLException {
		long result = rs.getLong(pos);
		if (rs.wasNull()) return null;
		return result;
	}
	
	/**
	 * Holt ein nullbares Float aus dem ResultSet
	 *
	 * @param rs  das ResultSet aus welchem geholt werden soll
	 * @param pos die Position in dem ResultSet
	 *
	 * @return der Wert oder null
	 *
	 * @throws SQLException wenn ein SQL Fehler auftrat
	 */
	public static @Nullable Float getNullableFloat(final @NotNull ResultSet rs, final int pos) throws SQLException {
		float result = rs.getFloat(pos);
		if (rs.wasNull()) return null;
		return result;
	}
	
	/**
	 * Holt ein nullbares Double aus dem ResultSet
	 *
	 * @param rs  das ResultSet aus welchem geholt werden soll
	 * @param pos die Position in dem ResultSet
	 *
	 * @return der Wert oder null
	 *
	 * @throws SQLException wenn ein SQL Fehler auftrat
	 */
	public static @Nullable Double getNullableDouble(final @NotNull ResultSet rs, final int pos) throws SQLException {
		double result = rs.getDouble(pos);
		if (rs.wasNull()) return null;
		return result;
	}
}
