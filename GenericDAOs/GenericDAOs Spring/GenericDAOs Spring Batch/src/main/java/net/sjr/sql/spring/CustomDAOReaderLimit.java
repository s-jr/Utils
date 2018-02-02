package net.sjr.sql.spring;

import net.sjr.sql.DBObject;
import net.sjr.sql.Parameter;
import net.sjr.sql.ParameterList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.database.AbstractPagingItemReader;

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
public class CustomDAOReaderLimit<T extends DBObject<P>, P extends Number, R> extends AbstractPagingItemReader<R> {
	private final Logger log = LoggerFactory.getLogger(getClass());
	protected final PaginationDAO<T, P> dao;
	protected final KlauselFunction join;
	protected final KlauselFunction where;
	protected final BiFunction<Integer, Integer, ParameterList> params;
	protected final KlauselFunction limit;
	protected final KlauselFunction order;
	protected final Function<List<T>, List<R>> mapper;
	protected final DBObjectFunction loadedObjects;
	
	/**
	 * Erstellt einen neuen {@link CustomDAOReaderLimit} an mit einer {@link PaginationDAO}
	 * Die Klauseln müssen hier manuell erstellt werden. Dafür müssen Funktionen in dem Format (page, pageSize) -&gt; Klausel übergeben werden
	 *
	 * @param dao    die {@link PaginationDAO}
	 * @param join   Die JOIN Klausel oder {@code null}
	 * @param where  Die WHERE Klausel oder {@code null}
	 * @param params Die {@link Parameter} oder {@code null}
	 * @param limit  das Limit für die Anzahl der Ergebnisse oder {@code null}
	 * @param order  Die ORDER Klausel oder {@code null}
	 * @param mapper Funktion, die de Rückgabe der DAO nachträglich noch mappt
	 */
	public CustomDAOReaderLimit(final @NotNull PaginationDAO<T, P> dao, final @Nullable KlauselFunction join, final @Nullable KlauselFunction where, final @Nullable BiFunction<Integer, Integer, ParameterList> params, final @Nullable KlauselFunction limit, final @Nullable KlauselFunction order, final @Nullable Function<List<T>, List<R>> mapper) {
		this(dao, join, where, params, limit, order, mapper, null);
	}
	
	/**
	 * Erstellt einen neuen {@link CustomDAOReaderLimit} an mit einer {@link PaginationDAO}
	 * Die Klauseln müssen hier manuell erstellt werden. Dafür müssen Funktionen in dem Format (page, pageSize) -&gt; Klausel übergeben werden
	 *
	 * @param dao      die {@link PaginationDAO}
	 * @param join     Die JOIN Klausel oder {@code null}
	 * @param where    Die WHERE Klausel oder {@code null}
	 * @param params   Die {@link Parameter} oder {@code null}
	 * @param limit    das Limit für die Anzahl der Ergebnisse oder {@code null}
	 * @param order    Die ORDER Klausel oder {@code null}
	 * @param mapper   Funktion, die de Rückgabe der DAO nachträglich noch mappt
	 * @param pageSize die Größe einer Seite. Je größer, desto weniger Datenbankabfragen werden benötigt, aber auch mehr Arbeitsspeicher
	 */
	public CustomDAOReaderLimit(final @NotNull PaginationDAO<T, P> dao, final @Nullable KlauselFunction join, final @Nullable KlauselFunction where, final @Nullable BiFunction<Integer, Integer, ParameterList> params, final @Nullable KlauselFunction limit, final @Nullable KlauselFunction order, final @Nullable Function<List<T>, List<R>> mapper, final int pageSize) {
		this(dao, join, where, params, limit, order, mapper);
		setPageSize(pageSize);
	}
	
	/**
	 * Erstellt einen neuen {@link CustomDAOReaderLimit} an mit einer {@link PaginationDAO}
	 * Die Klauseln müssen hier manuell erstellt werden. Dafür müssen Funktionen in dem Format (page, pageSize) -&gt; Klausel übergeben werden
	 *
	 * @param dao           die {@link PaginationDAO}
	 * @param join          Die JOIN Klausel oder {@code null}
	 * @param where         Die WHERE Klausel oder {@code null}
	 * @param params        Die {@link Parameter} oder {@code null}
	 * @param limit         das Limit für die Anzahl der Ergebnisse oder {@code null}
	 * @param order         Die ORDER Klausel oder {@code null}
	 * @param mapper        Funktion, die de Rückgabe der DAO nachträglich noch mappt
	 * @param loadedObjects Objekte, die schon geladen wurden und somit nicht neu geladen werden müssen
	 */
	public CustomDAOReaderLimit(final @NotNull PaginationDAO<T, P> dao, final @Nullable KlauselFunction join, final @Nullable KlauselFunction where, final @Nullable BiFunction<Integer, Integer, ParameterList> params, final @Nullable KlauselFunction limit, final @Nullable KlauselFunction order, final @Nullable Function<List<T>, List<R>> mapper, final @Nullable DBObjectFunction loadedObjects) {
		this.dao = dao;
		this.join = join;
		this.where = where;
		this.params = params;
		this.limit = limit;
		this.order = order;
		this.mapper = mapper;
		this.loadedObjects = loadedObjects;
		setName("CustomDAOReaderLimit" + hashCode());
	}
	
	/**
	 * Erstellt einen neuen {@link CustomDAOReaderLimit} an mit einer {@link PaginationDAO}
	 * Die Klauseln müssen hier manuell erstellt werden. Dafür müssen Funktionen in dem Format (page, pageSize) -&gt; Klausel übergeben werden
	 *
	 * @param dao           die {@link PaginationDAO}
	 * @param join          Die JOIN Klausel oder {@code null}
	 * @param where         Die WHERE Klausel oder {@code null}
	 * @param params        Die {@link Parameter} oder {@code null}
	 * @param limit         das Limit für die Anzahl der Ergebnisse oder {@code null}
	 * @param order         Die ORDER Klausel oder {@code null}
	 * @param mapper        Funktion, die de Rückgabe der DAO nachträglich noch mappt
	 * @param loadedObjects Objekte, die schon geladen wurden und somit nicht neu geladen werden müssen
	 * @param pageSize      die Größe einer Seite. Je größer, desto weniger Datenbankabfragen werden benötigt, aber auch mehr Arbeitsspeicher
	 */
	public CustomDAOReaderLimit(final @NotNull PaginationDAO<T, P> dao, final @Nullable KlauselFunction join, final @Nullable KlauselFunction where, final @Nullable BiFunction<Integer, Integer, ParameterList> params, final @Nullable KlauselFunction limit, final @Nullable KlauselFunction order, final @Nullable Function<List<T>, List<R>> mapper, final @Nullable DBObjectFunction loadedObjects, final int pageSize) {
		this(dao, join, where, params, limit, order, mapper, loadedObjects);
		setPageSize(pageSize);
	}
	
	@Override
	protected void doReadPage() {
		int page = getPage();
		int size = getPageSize();
		log.debug("Lese Seite {} mit der Größe {}...", page, size);
		String join = this.join == null ? null : this.join.apply(page, size);
		String where = this.where == null ? null : this.where.apply(page, size);
		ParameterList params = this.params == null ? null : this.params.apply(page, size);
		String limit = this.limit == null ? null : this.limit.apply(page, size);
		String order = this.order == null ? null : this.order.apply(page, size);
		DBObject[] loadedObjects = this.loadedObjects == null ? null : this.loadedObjects.apply(page, size);
		results = mapper.apply(dao.loadCustomPage(join, where, params, limit, order, loadedObjects));
	}
	
	@Override
	protected void doJumpToPage(final int itemIndex) {
	}
	
	@Override
	public boolean equals(final @Nullable Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CustomDAOReaderLimit<?, ?, ?> that = (CustomDAOReaderLimit<?, ?, ?>) o;
		return Objects.equals(dao, that.dao) &&
				Objects.equals(join, that.join) &&
				Objects.equals(where, that.where) &&
				Objects.equals(params, that.params) &&
				Objects.equals(limit, that.limit) &&
				Objects.equals(order, that.order) &&
				Objects.equals(mapper, that.mapper) &&
				Objects.equals(loadedObjects, that.loadedObjects);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(dao, join, where, params, limit, order, mapper, loadedObjects);
	}
	
	
	/**
	 * Interface für die Methode (int, int) -&gt; T. Die beiden Parameter sind normalerweise die Seite und die Seitengröße
	 *
	 * @param <T> Result Typ der Funktion
	 */
	@FunctionalInterface
	public interface PageSizeFunction<T> extends BiFunction<Integer, Integer, T> {
	}
	
	/**
	 * Interface für die Methode (int, int) -&gt; String. Die beiden Parameter sind normalerweise die Seite und die Seitengröße und das Ergebnis eine Klausel
	 */
	@FunctionalInterface
	public interface KlauselFunction extends PageSizeFunction<String> {
	}
	
	/**
	 * Interface für die Methode (int, int) -&gt; String. Die beiden Parameter sind normalerweise die Seite und die Seitengröße und das Ergebnis die bereits vorhandenen Objekte
	 */
	@FunctionalInterface
	public interface DBObjectFunction extends PageSizeFunction<DBObject[]> {
	}
}
