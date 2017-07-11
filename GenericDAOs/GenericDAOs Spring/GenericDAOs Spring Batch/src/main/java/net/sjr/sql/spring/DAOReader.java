package net.sjr.sql.spring;

import net.sjr.sql.DBObject;
import org.springframework.batch.item.database.AbstractPagingItemReader;

/**
 * Created by Jan on 10.07.2017.
 */
public class DAOReader<T extends DBObject<P>, P extends Number> extends AbstractPagingItemReader<T> {
	private final PaginationDAO<T, P> dao;

	public DAOReader(PaginationDAO<T, P> dao) {
		this.dao = dao;
	}

	@Override
	protected void doReadPage() {
		results = dao.loadPage(getPage(), getPageSize());
	}

	@Override
	protected void doJumpToPage(int itemIndex) {
	}
}
