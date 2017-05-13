package net.sjr.sql.exceptions;

import net.sjr.sql.SQLUtils;

import java.io.Serializable;
import java.sql.PreparedStatement;

public class NoNullTypeException extends RuntimeException implements Serializable {
	private static final long serialVersionUID = 1L;

	public NoNullTypeException(PreparedStatement pst, int position) {
		super("Es wurde ein null-Wert an Position " + position + " [" + pstToPositionName(pst, position) + " ] übergeben, aber kein Typ spezifiziert\n"
				+ "(Poblem SQL) " + SQLUtils.pstToSQL(pst));
	}

	private static String pstToPositionName(PreparedStatement pst, int position) {
		String sql = SQLUtils.pstToSQL(pst);
		String[] names;
		if (sql.contains("UPDATE")) {
			String namesStr = sql.split("SET ")[1].split(" WHERE")[0];
			names = namesStr.split("=?, ");
		}
		else {
			String namesStr = sql.split("[(]")[1].split("[)]")[0];
			names = namesStr.split(",");
		}
		return names[position - 1];
	}
}
