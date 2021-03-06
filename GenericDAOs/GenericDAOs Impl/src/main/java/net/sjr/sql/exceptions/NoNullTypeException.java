package net.sjr.sql.exceptions;

import net.sjr.sql.SQLUtils;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.sql.PreparedStatement;

public class NoNullTypeException extends RuntimeException implements Serializable {
	private static final long serialVersionUID = 879471158051022442L;
	
	public NoNullTypeException(final @NotNull PreparedStatement pst, final int position) {
		super("Es wurde ein null-Wert an Position " + position + " [" + pstToPositionName(pst, position) + " ] übergeben, aber kein Typ spezifiziert\n"
				+ "(Problem SQL) " + SQLUtils.pstToSQL(pst));
	}
	
	public NoNullTypeException(final @NotNull String s) {
		super(s);
	}
	
	private static @NotNull String pstToPositionName(final @NotNull PreparedStatement pst, final int position) {
		String sql = SQLUtils.pstToSQL(pst);
		String[] names;
		try {
			if (sql.contains("UPDATE")) {
				String namesStr = sql.split("SET ")[1].split(" WHERE")[0];
				names = namesStr.split("=\\?, ");
			}
			else if (sql.contains("DELETE") || sql.contains("SELECT")) {
				String namesStr = sql.split(" WHERE ")[1];
				names = namesStr.split("=\\?( (AND|OR) )?");
			}
			else if (sql.contains("INSERT")) {
				String namesStr = sql.split("\\(")[1].split("\\)")[0];
				names = namesStr.split(",");
			}
			else return "Unbekannt";
			return names[position - 1];
		}
		catch (final ArrayIndexOutOfBoundsException ignored) {
			return "Unbekannt";
		}
	}
}
