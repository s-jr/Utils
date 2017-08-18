package net.sjr.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by Jan Reichl on 18.08.17.
 */
public interface DAOBase<T extends DBObject<P>, P extends Number> extends AutoCloseable {
	T loadFromID(P primary);

	List<T> loadAll();

	Map<String, P> insertIntoDB(T v);

	Map<String, P> updateIntoDB(T v);

	Map<String, P> deleteFromDB(T v);

	Map<String, P> insertOrUpdate(T v);

	P getPrimary(ResultSet rs, int pos) throws SQLException;
}
