package net.sjr.sql;

import net.sjr.sql.rsloader.RsUtils;

import java.sql.PreparedStatement;

@SuppressWarnings("WeakerAccess")
public final class SQLUtils extends RsUtils {
	/**
	 * Wandelt den felder String vom Format "colA, colB" in das Format "?, ?" um
	 *
	 * @param felder der umzuwandelnde String
	 *
	 * @return der umgewandelte String
	 */
	public static String getFragezeichenInsert(final String felder) {
		return felder.replaceAll("[a-zA-Z0-9_]+", "?");
	}
	
	/**
	 * Wandelt den felder String vom Format "colA, colB" in das Format "colA=?, colB=?" um
	 *
	 * @param felder der umzuwandelnde String
	 *
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
	 *
	 * @return der umgewandelte String
	 */
	public static String getFragezeichenSelect(final String felder, String multOp, String operator) {
		return felder.replaceAll(", ", operator + '?' + multOp) + operator + '?';
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
	 * Extrahiert das SQL Statement aus einem prepared Statement
	 *
	 * @param pst das Prepared Statement aus welchem extrahiert werden soll
	 *
	 * @return das SQL Statement
	 */
	public static String pstToSQL(PreparedStatement pst) {
		return pst.toString()
				  .replaceAll("org.apache.tomcat.jdbc.pool.StatementFacade\\$StatementProxy\\[Proxy=[0-9]+; Query=.+ Delegate=", "")
				  .replaceAll("com\\.mysql\\.jdbc\\.JDBC42PreparedStatement@[0-9a-z]+: ", "")
				  .replaceAll("org\\.hsqldb\\.jdbc\\.JDBCPreparedStatement@[0-9a-z]+\\[", "");
	}
}
