package net.sjr.sql;

import org.jetbrains.annotations.NotNull;

/**
 * Interface für Objekte, welche als eine Spalte in der Datenbank gespeichert werden können
 * @param <T> der Typ der effektiven Datenbankspalte
 */
public interface DBColumn<T> extends DBConvertable {
	@NotNull T toColumn();
	
	@NotNull DBColumn<T> fillFromColumn(@NotNull T col);
}
