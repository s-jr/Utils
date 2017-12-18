package net.sjr.sql.spring;

import net.sjr.sql.DBObject;
import net.sjr.sql.Parameter;
import net.sjr.sql.ParameterList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.support.AbstractItemStreamItemReader;

import java.util.List;
import java.util.Objects;

/**
 * Klasse, die eine {@link PaginationDAO} nutzt um Items für Spring Batch aus einer Datenbank zu lesen
 *
 * @param <T> Typ des gespeicherten Java Objektes
 * @param <P> Typ des Primary Keys
 */
@SuppressWarnings({"unused", "WeakerAccess", "unchecked"})
public class DAOReaderPrimary<T extends DBObject<P>, P extends Number> extends AbstractItemStreamItemReader<T> {
	private final Logger log = LoggerFactory.getLogger(getClass());
	protected final PaginationDAO<T, P> dao;
	protected final String join;
	protected final String where;
	protected final ParameterList params;
	private P lastPrimary = null;
	Class<P> primaryClass = null;
	
	private static final String LAST_PRIMARY = "lastprimary";
	private final int pageSize;
	private volatile int indexInList = 0;
	
	protected volatile List<T> results;
	
	private final Object lock = new Object();
	
	/**
	 * Erstellt einen neuen {@link DAOReaderPrimary} an mit einer {@link PaginationDAO}
	 *
	 * @param dao die {@link PaginationDAO}
	 */
	public DAOReaderPrimary(PaginationDAO<T, P> dao) {
		this(dao, null, null, null);
	}
	
	/**
	 * Erstellt einen neuen {@link DAOReaderPrimary} an mit einer {@link PaginationDAO}
	 *
	 * @param dao      die {@link PaginationDAO}
	 * @param pageSize die Größe einer Seite. Je größer, desto weniger Datenbankabfragen werden benötigt, aber auch mehr Arbeitsspeicher
	 */
	public DAOReaderPrimary(PaginationDAO<T, P> dao, int pageSize) {
		this(dao, null, null, null, pageSize);
	}
	
	/**
	 * Erstellt einen neuen {@link DAOReaderPrimary} an mit einer {@link PaginationDAO} und erweiterten Abfragebedingungen
	 *
	 * @param dao    die {@link PaginationDAO}
	 * @param join   Die JOIN Klausel oder {@code null}
	 * @param where  Die WHERE Klausel oder {@code null}
	 * @param params Die {@link Parameter} oder {@code null}
	 */
	public DAOReaderPrimary(PaginationDAO<T, P> dao, String join, String where, ParameterList params) {
		this(dao, join, where, params, 10);
		
	}
	
	/**
	 * Erstellt einen neuen {@link DAOReaderPrimary} an mit einer {@link PaginationDAO} und erweiterten Abfragebedingungen
	 *
	 * @param dao      die {@link PaginationDAO}
	 * @param join     Die JOIN Klausel oder {@code null}
	 * @param where    Die WHERE Klausel oder {@code null}
	 * @param params   Die {@link Parameter} oder {@code null}
	 * @param pageSize die Größe einer Seite. Je größer, desto weniger Datenbankabfragen werden benötigt, aber auch mehr Arbeitsspeicher
	 */
	public DAOReaderPrimary(PaginationDAO<T, P> dao, String join, String where, ParameterList params, int pageSize) {
		this.dao = dao;
		this.join = join;
		this.where = where;
		this.params = params;
		this.pageSize = pageSize;
		setName("DAOReaderPrimary" + hashCode());
	}
	
	@Override
	public T read() {
		synchronized (lock) {
			if (results == null || indexInList >= pageSize) {
				log.debug("Lese Seite nach Primary {}", lastPrimary);
				
				results = dao.loadPageFromPrimary(lastPrimary, pageSize, join, where, params);
				indexInList = 0;
			}
			
			int next = indexInList++;
			if (next < results.size()) {
				T result = results.get(next);
				lastPrimary = result.getPrimary();
				return result;
			}
			else {
				return null;
			}
		}
	}
	
	@Override
	public void close() throws ItemStreamException {
		super.close();
		lastPrimary = null;
		indexInList = 0;
		results = null;
	}
	
	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException {
		super.open(executionContext);
		
		if (executionContext.containsKey(getExecutionContextKey(LAST_PRIMARY))) {
			lastPrimary = SQLUtilsSpring.loadLastPrimaryFromContext(dao, executionContext, getExecutionContextKey(LAST_PRIMARY));
		}
	}
	
	@Override
	public void update(ExecutionContext executionContext) throws ItemStreamException {
		super.update(executionContext);
		SQLUtilsSpring.saveLastPrimaryToContext(dao, lastPrimary, executionContext, getExecutionContextKey(LAST_PRIMARY));
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		DAOReaderPrimary<?, ?> that = (DAOReaderPrimary<?, ?>) o;
		return pageSize == that.pageSize &&
				Objects.equals(dao, that.dao) &&
				Objects.equals(join, that.join) &&
				Objects.equals(where, that.where) &&
				Objects.equals(params, that.params);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(dao, join, where, params, pageSize);
	}
}
