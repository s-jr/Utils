package net.sjr.sql;

/**
 * Interface für Objekte, welche als eine Spalte in der Datenbank gespeichert werden können
 * @param <T> der Typ der effektiven Datenbankspalte
 */
public interface DBColumn<T> extends DBConvertable {
	T toColumn();

	DBColumn<T> fillFromColumn(T col);
}
