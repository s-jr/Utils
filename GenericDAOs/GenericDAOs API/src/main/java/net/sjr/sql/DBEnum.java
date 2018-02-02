package net.sjr.sql;

import org.jetbrains.annotations.NotNull;

/**
 * Interface um ein {@code Enum} in der Datenbank speicherbar zu machen
 * @param <T> der Typ des Identifiers, der effektiv gespeichert wird
 */
public interface DBEnum<T> extends DBConvertable {
	/**
	 * gibt den in der Datenbank zu speichernden Wert. Muss für korrekte Funktionsweise eindeutig sein
	 *
	 * @return der zu speichernde Wert
	 */
	@NotNull T getDBIdentifier();
}