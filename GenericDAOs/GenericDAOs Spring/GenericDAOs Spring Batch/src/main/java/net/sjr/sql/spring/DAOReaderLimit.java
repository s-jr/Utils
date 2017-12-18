package net.sjr.sql.spring;

import net.sjr.sql.DBObject;
import net.sjr.sql.Parameter;
import net.sjr.sql.ParameterList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.database.AbstractPagingItemReader;

import java.util.Objects;

/**
 * Klasse, die eine {@link PaginationDAO} nutzt um Items für Spring Batch aus einer Datenbank zu lesen
 *
 * @param <T> Typ des gespeicherten Java Objektes
 * @param <P> Typ des Primary Keys
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class DAOReaderLimit<T extends DBObject<P>, P extends Number> extends AbstractPagingItemReader<T> {
	private final Logger log = LoggerFactory.getLogger(getClass());
	protected final PaginationDAO<T, P> dao;
	protected final String join;
	protected final String where;
	protected final ParameterList params;
	
	/**
	 * Erstellt einen neuen {@link DAOReaderLimit} an mit einer {@link PaginationDAO}
	 *
	 * @param dao die {@link PaginationDAO}
	 */
	public DAOReaderLimit(PaginationDAO<T, P> dao) {
		this(dao, null, null, null);
	}
	
	/**
	 * Erstellt einen neuen {@link DAOReaderLimit} an mit einer {@link PaginationDAO}
	 *
	 * @param dao      die {@link PaginationDAO}
	 * @param pageSize die Größe einer Seite. Je größer, desto weniger Datenbankabfragen werden benötigt, aber auch mehr Arbeitsspeicher
	 */
	public DAOReaderLimit(PaginationDAO<T, P> dao, int pageSize) {
		this(dao, null, null, null);
		setPageSize(pageSize);
	}
	
	/**
	 * Erstellt einen neuen {@link DAOReaderLimit} an mit einer {@link PaginationDAO} und erweiterten Abfragebedingungen
	 *
	 * @param dao    die {@link PaginationDAO}
	 * @param join   Die JOIN Klausel oder {@code null}
	 * @param where  Die WHERE Klausel oder {@code null}
	 * @param params Die {@link Parameter} oder {@code null}
	 */
	public DAOReaderLimit(PaginationDAO<T, P> dao, String join, String where, ParameterList params) {
		this.dao = dao;
		this.join = join;
		this.where = where;
		this.params = params;
		setName("DAOReaderLimit" + hashCode());
	}
	
	/**
	 * Erstellt einen neuen {@link DAOReaderLimit} an mit einer {@link PaginationDAO} und erweiterten Abfragebedingungen
	 *
	 * @param dao      die {@link PaginationDAO}
	 * @param join     Die JOIN Klausel oder {@code null}
	 * @param where    Die WHERE Klausel oder {@code null}
	 * @param params   Die {@link Parameter} oder {@code null}
	 * @param pageSize die Größe einer Seite. Je größer, desto weniger Datenbankabfragen werden benötigt, aber auch mehr Arbeitsspeicher
	 */
	public DAOReaderLimit(PaginationDAO<T, P> dao, String join, String where, ParameterList params, int pageSize) {
		this(dao, join, where, params);
		setPageSize(pageSize);
	}
	
	@Override
	protected void doReadPage() {
		int page = getPage();
		int pageSize = getPageSize();
		log.debug("Lese Seite {} mit der Größe {}...", page, pageSize);
		results = dao.loadPage(page, pageSize, join, where, params);
	}
	
	@Override
	protected void doJumpToPage(int itemIndex) {
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		DAOReaderLimit<?, ?> that = (DAOReaderLimit<?, ?>) o;
		return Objects.equals(dao, that.dao) &&
				Objects.equals(join, that.join) &&
				Objects.equals(where, that.where) &&
				Objects.equals(params, that.params);
	}
	
	@Override
	public int hashCode() {
		
		return Objects.hash(dao, join, where, params);
	}
}
