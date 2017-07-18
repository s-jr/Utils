package net.sjr.sql.spring;

import net.sjr.sql.DBObject;
import net.sjr.sql.ParameterList;
import org.springframework.batch.item.database.AbstractPagingItemReader;

/**
 * Created by Jan on 10.07.2017.
 */
public class DAOReader<T extends DBObject<P>, P extends Number> extends AbstractPagingItemReader<T> {
	private final PaginationDAO<T, P> dao;
	private final String join;
	private final String where;
	private final ParameterList params;

	public DAOReader(PaginationDAO<T, P> dao) {
		this(dao, null, null, null);
	}

	public DAOReader(PaginationDAO<T, P> dao, String join, String where, ParameterList params) {
		this.dao = dao;
		this.join = join;
		this.where = where;
		this.params = params;
	}

	@Override
	protected void doReadPage() {
		results = dao.loadPage(getPage(), getPageSize(), join, where, params);
	}

	@Override
	protected void doJumpToPage(int itemIndex) {
	}
}
