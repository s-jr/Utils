package net.sjr.sql;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class SQLUtils {
	/**
	 * Wandelt den felder String vom Format "colA, colB" in das Format "?, ?" um
	 *
	 * @param felder der umzuwandelnde String
	 * @return der umgewandelte String
	 */
	public static String getFragezeichenInsert(final String felder) {
		return felder.replaceAll("[a-zA-Z0-9_]+", "?");
	}

	/**
	 * Wandelt den felder String vom Format "colA, colB" in das Format "colA=?, colB=?" um
	 *
	 * @param felder der umzuwandelnde String
	 * @return der umgewandelte String
	 */
	public static String getFragezeichenUpdate(final String felder) {
		return getFragezeichenSelect(felder, ", ", "=");
	}

	/**
	 * Wandelt den felder String vom Format "colA, colB" in das Format "colA=?, colB=?" um
	 *
	 * @param felder   der umzuwandelnde String
	 * @param multOp   das Trennzeichen zwischen den Klauseln
	 * @param operator der Operator
	 * @return der umgewandelte String
	 */
	public static String getFragezeichenSelect(final String felder, String multOp, String operator) {
		return felder.replaceAll(", ", operator + "?" + multOp) + operator + "?";
	}

	/**
	 * Wandelt den felder String vom Format "colA, colB" in das Format "tableName.colA, tableName.colB" um
	 *
	 * @param felder    der umzuwandelnde String
	 * @param tableName der Tabellenname
	 *
	 * @return der umgewandelte String
	 */
	public static String fullQualifyTableName(final String felder, String tableName) {
		return tableName + "." + felder.replaceAll(", ", ", " + tableName + ".");
	}

	/**
	 * lädt ein Objekt aus den bereits geladenen Objekten oder zur not aus der DAO
	 *
	 * @param rsPos         Position der ID im ResultSet
	 * @param rs            das ResultSet mit der ID
	 * @param dao           das DAO mit dem zur not gesucht werden soll
	 * @param loadedObjects die geladenen Objekte, die durchsucht werden sollen
	 * @param <T>           der Typ der DAO
	 * @param <P>           der Primary Typ des Types der DAO
	 * @return das (evtl. geladene) Objekt oder null, wenn nicht vorhanden
	 * @throws SQLException wenn ein SQL Fehler auftrat
	 */
	@SuppressWarnings("unchecked")
	public static <T extends DBObject<P>, P extends Number> T loadedObjectsOrNull(final int rsPos, final ResultSet rs, final DAO<T, P> dao, final DBObject... loadedObjects)
			throws SQLException {
		P id = dao.getPrimary(rs, rsPos);
		if (rs.wasNull()) {
			return null;
		}

		if (loadedObjects != null) {
			for (DBObject o : loadedObjects) {
				if (o != null && o.getPrimary().equals(id)) {
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
	 * Extrahiert das SQL Statement aus einem prepared Statement
	 *
	 * @param pst das Prepared Statement aus welchem extrahiert werden soll
	 * @return das SQL Statement
	 */
	public static String pstToSQL(PreparedStatement pst) {
		return pst.toString().replaceAll("com\\.mysql\\.jdbc\\.JDBC42PreparedStatement@[0-9a-z]+: ", "");
	}

	/**
	 * Fügt der WHERE Klausel (wenn nötig) ein IS NULL hinzu
	 *
	 * @param where  die WHERE Klausel
	 * @param params die Parameter, die eingefügt werden
	 *
	 * @return die WHERE Klausel mit IS NULLs
	 */
	public static String nullableWhere(String where, ParameterList params) {
		if (where == null || params == null) return where;
		String[] terme = where.split(" ");
		StringBuilder result = new StringBuilder();
		int pos = 0;
		for (String s : terme) {
			if (result.length() > 0) result.append(" ");
			if (s.contains("=?")) {
				if (params.get(pos).value == null)
					result.append("(").append(s).append(" OR ").append(s.replace("=?", "")).append(" IS NULL)");
				else result.append(s);
				pos++;
			}
			else {
				result.append(s);
			}
		}
		return result.toString();
	}
}
