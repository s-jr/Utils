package net.sjr.sql.parametertype;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Interface für Klassen, die Objekte in {@link PreparedStatement} einsetzen
 */
public interface ParameterType {
	/**
	 * setzt ein Wert an eine bestimmte Stelle in das {@link PreparedStatement}
	 *
	 * @param pst   das {@link PreparedStatement} in das eingesetzt werden soll
	 * @param pos   die Position an die gesetzt werden soll
	 * @param value der Wert, der gesetzt werden soll
	 *
	 * @return die nächste Position oder -1, wenn die Klasse des Objektes nicht unterstützt wird
	 *
	 * @throws SQLException Wenn eine {@link SQLException} aufgetreten ist
	 */
	int set(@NotNull PreparedStatement pst, int pos, @Nullable Object value) throws SQLException;
}
