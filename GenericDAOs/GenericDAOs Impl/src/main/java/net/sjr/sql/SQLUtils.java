package net.sjr.sql;

import net.sjr.sql.rsloader.RsUtils;
import org.slf4j.Logger;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Klasse mit diversen Methoden, die bei dem Arbeiten mit SQL hilfreich sind
 */
@SuppressWarnings("WeakerAccess")
public class SQLUtils extends RsUtils {
	/**
	 * Wandelt den Felder {@link String} vom Format "colA, colB" in das Format "?, ?" um
	 *
	 * @param felder der umzuwandelnde {@link String}
	 *
	 * @return der umgewandelte {@link String}
	 */
	public static String getFragezeichenInsert(final String felder) {
		return felder.replaceAll("[a-zA-Z0-9_]+", "?");
	}
	
	/**
	 * Wandelt den Felder {@link String} vom Format "colA, colB" in das Format "colA=?, colB=?" um
	 *
	 * @param felder der umzuwandelnde {@link String}
	 *
	 * @return der umgewandelte {@link String}
	 */
	public static String getFragezeichenUpdate(final String felder) {
		return getFragezeichenSelect(felder, ", ", "=");
	}
	
	/**
	 * Wandelt den Felder {@link String} vom Format "colA, colB" in das Format "colA=?, colB=?" um
	 *
	 * @param felder   der umzuwandelnde {@link String}
	 * @param multOp   das Trennzeichen zwischen den Klauseln
	 * @param operator der Operator
	 *
	 * @return der umgewandelte {@link String}
	 */
	public static String getFragezeichenSelect(final String felder, String multOp, String operator) {
		return felder.replaceAll(", ", operator + '?' + multOp) + operator + '?';
	}
	
	/**
	 * Wandelt den Felder {@link String} vom Format "colA, colB" in das Format "tableName.colA, tableName.colB" um
	 *
	 * @param felder    der umzuwandelnde {@link String}
	 * @param tableName der Tabellenname
	 *
	 * @return der umgewandelte {@link String}
	 */
	public static String fullQualifyTableName(final String felder, String tableName) {
		return tableName + '.' + felder.replaceAll(", ", ", " + tableName + '.');
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
			if (result.length() > 0) result.append(' ');
			if (s.contains("=?")) {
				if (params.get(pos).value == null)
					result.append('(').append(s).append(" OR ").append(s.replace("=?", "")).append(" IS NULL)");
				else result.append(s);
				pos++;
			}
			else {
				result.append(s);
			}
		}
		return result.toString();
	}
	
	/**
	 * Extrahiert das SQL Statement aus einem {@link PreparedStatement}
	 *
	 * @param pst das {@link PreparedStatement} aus welchem extrahiert werden soll
	 *
	 * @return das SQL Statement
	 */
	public static String pstToSQL(PreparedStatement pst) {
		return pst.toString()
				  .replaceAll("org.apache.tomcat.jdbc.pool.StatementFacade\\$StatementProxy\\[Proxy=[0-9]+; Query=.+ Delegate=", "")
				  .replaceAll("com\\.mysql\\.jdbc\\.JDBC42PreparedStatement@[0-9a-z]+: ", "")
				  .replaceAll("org\\.hsqldb\\.jdbc\\.JDBCPreparedStatement@[0-9a-z]+\\[", "");
	}
	
	/**
	 * Schließt {@link AutoCloseable} mit {@code null} Check und Fehlerabfangung. Besondere Fehlerbeschreibung bei SQL Fehlern
	 *
	 * @param closeable das {@link AutoCloseable}
	 * @param log       der {@link Logger} im Fehlerfall
	 */
	public static void closeSqlAutocloseable(final AutoCloseable closeable, Logger log) {
		if (closeable != null) {
			try {
				closeable.close();
			}
			catch (SQLException e) {
				if (log != null) log.error("SQL Fehler", e);
			}
			catch (Exception e) {
				if (log != null) log.error("Allgemeiner Fehler", e);
			}
		}
	}
	
	/**
	 * Findet die Klasse des Primary Keys des Objektes des DAO heraus
	 *
	 * @param dao die DAO des Objektes
	 * @param <T> der Typ des DAO
	 * @param <P> der Typ des Primary Keys der DAO
	 * @return die Klasse des Primary Keys
	 */
	public static <T extends DBObject<P>, P extends Number> Class<P> getPrimaryClass(DAO<T, P> dao) {
		return dao.getPrimaryClass();
	}
}
