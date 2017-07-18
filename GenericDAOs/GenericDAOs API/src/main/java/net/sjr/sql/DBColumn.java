package net.sjr.sql;

/**
 * Created by Jan on 12.07.2017.
 */
public interface DBColumn<T> extends DBConvertable {
	T toColumn();

	void fillFromColumn(T col);
}
