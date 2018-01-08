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
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Klasse, die eine {@link PaginationDAO} nutzt um Items für Spring Batch aus einer Datenbank zu lesen.
 * Dabei sind die Klauseln frei wählbar
 *
 * @param <T> Typ des gespeicherten Java Objektes
 * @param <P> Typ des Primary Keys
 * @param <R> Typ nach dem Mappen des Ergebnisses der DAO
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class CustomDAOReaderPrimary<T extends DBObject<P>, P extends Number, R> extends AbstractItemStreamItemReader<R> {
	private final Logger log = LoggerFactory.getLogger(getClass());
	protected final PaginationDAO<T, P> dao;
	protected final KlauselFunction<P> join;
	protected final KlauselFunction<P> where;
	protected final BiFunction<P, Integer, ParameterList> params;
	protected final KlauselFunction<P> limit;
	protected final KlauselFunction<P> order;
	protected final Function<List<T>, List<R>> mapper;
	protected final DBObjectFunction<P> loadedObjects;
	protected final Function<R, P> primaryExtractor;
	
	private P lastPrimary = null;
	
	private static final String LAST_PRIMARY = "lastprimary";
	private final int pageSize;
	private volatile int indexInList = 0;
	
	protected volatile List<R> results;
	
	private final Object lock = new Object();
	
	/**
	 * Erstellt einen neuen {@link CustomDAOReaderPrimary} an mit einer {@link PaginationDAO}
	 * Die Klauseln müssen hier manuell erstellt werden. Dafür müssen Funktionen in dem Format (page, pageSize) -&gt; Klausel übergeben werden
	 *
	 * @param dao              die {@link PaginationDAO}
	 * @param join             Die JOIN Klausel oder {@code null}
	 * @param where            Die WHERE Klausel oder {@code null}
	 * @param params           Die {@link Parameter} oder {@code null}
	 * @param limit            das Limit für die Anzahl der Ergebnisse oder {@code null}
	 * @param order            Die ORDER Klausel oder {@code null}
	 * @param mapper           Funktion, die de Rückgabe der DAO nachträglich noch mappt
	 * @param primaryExtractor Funktion, welche aus dem Result Objekt die letzte Primary ID extrahiert
	 */
	public CustomDAOReaderPrimary(PaginationDAO<T, P> dao, KlauselFunction<P> join, KlauselFunction<P> where, BiFunction<P, Integer, ParameterList> params, KlauselFunction<P> limit, KlauselFunction<P> order, final Function<List<T>, List<R>> mapper, Function<R, P> primaryExtractor) {
		this(dao, join, where, params, limit, order, mapper, primaryExtractor, null);
	}
	
	/**
	 * Erstellt einen neuen {@link CustomDAOReaderPrimary} an mit einer {@link PaginationDAO}
	 * Die Klauseln müssen hier manuell erstellt werden. Dafür müssen Funktionen in dem Format (page, pageSize) -&gt; Klausel übergeben werden
	 *
	 * @param dao              die {@link PaginationDAO}
	 * @param join             Die JOIN Klausel oder {@code null}
	 * @param where            Die WHERE Klausel oder {@code null}
	 * @param params           Die {@link Parameter} oder {@code null}
	 * @param limit            das Limit für die Anzahl der Ergebnisse oder {@code null}
	 * @param order            Die ORDER Klausel oder {@code null}
	 * @param mapper           Funktion, die de Rückgabe der DAO nachträglich noch mappt
	 * @param primaryExtractor Funktion, welche aus dem Result Objekt die letzte Primary ID extrahiert
	 * @param pageSize         die Größe einer Seite. Je größer, desto weniger Datenbankabfragen werden benötigt, aber auch mehr Arbeitsspeicher
	 */
	public CustomDAOReaderPrimary(PaginationDAO<T, P> dao, KlauselFunction<P> join, KlauselFunction<P> where, BiFunction<P, Integer, ParameterList> params, KlauselFunction<P> limit, KlauselFunction<P> order, final Function<List<T>, List<R>> mapper, Function<R, P> primaryExtractor, int pageSize) {
		this(dao, join, where, params, limit, order, mapper, primaryExtractor, null, pageSize);
	}
	
	/**
	 * Erstellt einen neuen {@link CustomDAOReaderPrimary} an mit einer {@link PaginationDAO}
	 * Die Klauseln müssen hier manuell erstellt werden. Dafür müssen Funktionen in dem Format (page, pageSize) -&gt; Klausel übergeben werden
	 *
	 * @param dao              die {@link PaginationDAO}
	 * @param join             Die JOIN Klausel oder {@code null}
	 * @param where            Die WHERE Klausel oder {@code null}
	 * @param params           Die {@link Parameter} oder {@code null}
	 * @param limit            das Limit für die Anzahl der Ergebnisse oder {@code null}
	 * @param order            Die ORDER Klausel oder {@code null}
	 * @param mapper           Funktion, die de Rückgabe der DAO nachträglich noch mappt
	 * @param primaryExtractor Funktion, welche aus dem Result Objekt die letzte Primary ID extrahiert
	 * @param loadedObjects    Objekte, die schon geladen wurden und somit nicht neu geladen werden müssen
	 */
	public CustomDAOReaderPrimary(PaginationDAO<T, P> dao, KlauselFunction<P> join, KlauselFunction<P> where, BiFunction<P, Integer, ParameterList> params, KlauselFunction<P> limit, KlauselFunction<P> order, final Function<List<T>, List<R>> mapper, Function<R, P> primaryExtractor, DBObjectFunction<P> loadedObjects) {
		this(dao, join, where, params, limit, order, mapper, primaryExtractor, loadedObjects, 10);
	}
	
	/**
	 * Erstellt einen neuen {@link CustomDAOReaderPrimary} an mit einer {@link PaginationDAO}
	 * Die Klauseln müssen hier manuell erstellt werden. Dafür müssen Funktionen in dem Format (page, pageSize) -&gt; Klausel übergeben werden
	 *
	 * @param dao              die {@link PaginationDAO}
	 * @param join             Die JOIN Klausel oder {@code null}
	 * @param where            Die WHERE Klausel oder {@code null}
	 * @param params           Die {@link Parameter} oder {@code null}
	 * @param limit            das Limit für die Anzahl der Ergebnisse oder {@code null}
	 * @param order            Die ORDER Klausel oder {@code null}
	 * @param mapper           Funktion, die de Rückgabe der DAO nachträglich noch mappt
	 * @param primaryExtractor Funktion, welche aus dem Result Objekt die letzte Primary ID extrahiert
	 * @param loadedObjects    Objekte, die schon geladen wurden und somit nicht neu geladen werden müssen
	 * @param pageSize         die Größe einer Seite. Je größer, desto weniger Datenbankabfragen werden benötigt, aber auch mehr Arbeitsspeicher
	 */
	public CustomDAOReaderPrimary(PaginationDAO<T, P> dao, KlauselFunction<P> join, KlauselFunction<P> where, BiFunction<P, Integer, ParameterList> params, KlauselFunction<P> limit, KlauselFunction<P> order, final Function<List<T>, List<R>> mapper, Function<R, P> primaryExtractor, DBObjectFunction<P> loadedObjects, int pageSize) {
		this.dao = dao;
		this.join = join;
		this.where = where;
		this.params = params;
		this.limit = limit;
		this.order = order;
		this.mapper = mapper;
		this.primaryExtractor = primaryExtractor;
		this.loadedObjects = loadedObjects;
		this.pageSize = pageSize;
		setName("CustomDAOReaderPrimary" + hashCode());
	}
	
	@Override
	public R read() {
		synchronized (lock) {
			if (results == null || indexInList >= pageSize) {
				log.debug("Lese Seite nach Primary {}", lastPrimary);
				
				String join = this.join == null ? null : this.join.apply(lastPrimary, pageSize);
				String where = this.where == null ? null : this.where.apply(lastPrimary, pageSize);
				ParameterList params = this.params == null ? null : this.params.apply(lastPrimary, pageSize);
				String limit = this.limit == null ? null : this.limit.apply(lastPrimary, pageSize);
				String order = this.order == null ? null : this.order.apply(lastPrimary, pageSize);
				DBObject[] loadedObjects = this.loadedObjects == null ? null : this.loadedObjects.apply(lastPrimary, pageSize);
				
				results = mapper.apply(dao.loadCustomPage(join, where, params, limit, order, loadedObjects));
				indexInList = 0;
			}
			
			int next = indexInList++;
			if (next < results.size()) {
				R result = results.get(next);
				lastPrimary = primaryExtractor.apply(result);
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
		CustomDAOReaderPrimary<?, ?, ?> that = (CustomDAOReaderPrimary<?, ?, ?>) o;
		return pageSize == that.pageSize &&
				indexInList == that.indexInList &&
				Objects.equals(dao, that.dao) &&
				Objects.equals(join, that.join) &&
				Objects.equals(where, that.where) &&
				Objects.equals(params, that.params) &&
				Objects.equals(limit, that.limit) &&
				Objects.equals(order, that.order) &&
				Objects.equals(mapper, that.mapper) &&
				Objects.equals(loadedObjects, that.loadedObjects) &&
				Objects.equals(primaryExtractor, that.primaryExtractor) &&
				Objects.equals(lastPrimary, that.lastPrimary) &&
				Objects.equals(results, that.results) &&
				Objects.equals(lock, that.lock);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(dao, join, where, params, limit, order, mapper, loadedObjects, primaryExtractor, lastPrimary, pageSize, indexInList, results, lock);
	}
	
	/**
	 * Interface für die Methode (P, int) -&gt; T. Die beiden Parameter sind normalerweise die Seite und die Seitengröße
	 *
	 * @param <T> Result Typ der Funktion
	 */
	@FunctionalInterface
	public interface PrimarySizeFunction<P extends Number, T> extends BiFunction<P, Integer, T> {
	}
	
	/**
	 * Interface für die Methode (P, int) -&gt; String. Die beiden Parameter sind normalerweise die Seite und die Seitengröße und das Ergebnis eine Klausel
	 */
	@FunctionalInterface
	public interface KlauselFunction<P extends Number> extends PrimarySizeFunction<P, String> {
	}
	
	/**
	 * Interface für die Methode (P, int) -&gt; String. Die beiden Parameter sind normalerweise die Seite und die Seitengröße und das Ergebnis die bereits vorhandenen Objekte
	 */
	@FunctionalInterface
	public interface DBObjectFunction<P extends Number> extends PrimarySizeFunction<P, DBObject[]> {
	}
}
