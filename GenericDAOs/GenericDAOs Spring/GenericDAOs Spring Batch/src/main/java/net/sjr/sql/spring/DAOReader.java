package net.sjr.sql.spring;

import net.sjr.sql.DBObject;
import net.sjr.sql.Parameter;
import net.sjr.sql.ParameterList;
import org.springframework.batch.item.database.AbstractPagingItemReader;

/**
 * Klasse, die eine {@link PaginationDAO} nutzt um Items für Spring Batch aus einer Datenbank zu lesen
 * @param <T> Typ des gespeicherten Java Objektes
 * @param <P> Typ des Primary Keys
 */
public class DAOReader<T extends DBObject<P>, P extends Number> extends AbstractPagingItemReader<T> {
	private final PaginationDAO<T, P> dao;
	private final String join;
	private final String where;
	private final ParameterList params;
	
	/**
	 * Erstellt einen neuen {@link DAOReader} an mit einer {@link PaginationDAO}
	 *
	 * @param dao die {@link PaginationDAO}
	 */
	public DAOReader(PaginationDAO<T, P> dao) {
		this(dao, null, null, null);
	}
	
	/**
	 * Erstellt einen neuen {@link DAOReader} an mit einer {@link PaginationDAO} und erweiterten Abfragebedingungen
	 * @param dao die {@link PaginationDAO}
	 * @param join Die JOIN Klausel oder {@code null}
	 * @param where Die WHERE Klausel oder {@code null}
	 * @param params Die {@link Parameter} oder {@code null}
	 */
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
