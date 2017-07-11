package net.sjr.sql.spring;

import net.sjr.sql.DAO;
import net.sjr.sql.DBObject;
import net.sjr.sql.ParameterList;
import org.springframework.batch.item.ItemWriter;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;

/**
 * Created by Jan on 10.07.2017.
 */
public abstract class PaginationDAO<T extends DBObject<P>, P extends Number> extends DAO<T, P> implements ItemWriter<T> {
	public PaginationDAO(DataSource ds) {
		super(ds);
	}

	public PaginationDAO(Connection con) {
		super(con);
	}

	public PaginationDAO(DAO<? extends DBObject, ? extends Number> dao) {
		super(dao);
	}

	public List<T> loadPage(int pageNumber, int pageSize) {
		return loadPage(pageNumber, pageSize, null, null, null);
	}

	public List<T> loadPage(int pageNumber, int pageSize, String join, String where, ParameterList params) {
		return loadAllFromWhere(join, where, params, (pageNumber * pageSize) + ", " + ((pageNumber + 1) * pageSize), getPrimaryCol());
	}

	@Override
	public void write(List<? extends T> items) throws Exception {
		for (T item : items) {
			insertOrUpdate(item);
		}
	}
}
