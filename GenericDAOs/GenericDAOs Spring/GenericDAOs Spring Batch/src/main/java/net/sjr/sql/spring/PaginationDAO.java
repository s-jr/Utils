package net.sjr.sql.spring;

import net.sjr.sql.DAO;
import net.sjr.sql.DBObject;
import net.sjr.sql.ParameterList;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamWriter;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;

/**
 * Created by Jan on 10.07.2017.
 */
public abstract class PaginationDAO<T extends DBObject<P>, P extends Number> extends DAO<T, P> implements ItemStreamWriter<T> {
	private int done = 0;

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
		int itemsSize = items.size();
		for (; done < itemsSize; done++) {
			T item = items.get(done);
			insertOrUpdate(item);
		}
		done = 0;
	}

	@Override
	public void open(ExecutionContext executionContext) {
		if (executionContext.containsKey("paginationdao.done")) {
			done = executionContext.getInt("paginationdao.done");
		}
	}

	@Override
	public void update(ExecutionContext executionContext) {
		executionContext.putLong("paginationdao.done", done);
	}
}
