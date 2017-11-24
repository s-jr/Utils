package net.sjr.sql.spring;

import net.sjr.sql.DAO;
import net.sjr.sql.DBObject;
import net.sjr.sql.Parameter;
import net.sjr.sql.ParameterList;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamWriter;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;

/**
 * Erweitert die {@link DAO}s um die Möglichkeit als Spring Batch Writer zu dienen und fügt die vom {@link DAOReader} benötigten Methoden hinzu
 *
 * @param <T> Typ des zu gespeichernden Java Objektes
 * @param <P> Typ des Primary Keys
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class PaginationDAO<T extends DBObject<P>, P extends Number> extends DAO<T, P> implements ItemStreamWriter<T> {
	private int done = 0;
	
	/**
	 * Erstellt die {@link PaginationDAO} mit einer {@link DataSource}
	 *
	 * @param ds die {@link DataSource}
	 */
	public PaginationDAO(DataSource ds) {
		super(ds);
	}
	
	/**
	 * Erstellt die {@link PaginationDAO} mit einer bereits vorhandenen Datenbankverbindung
	 *
	 * @param con die bereits vorhandene Datenbankverbindung
	 */
	public PaginationDAO(Connection con) {
		super(con);
	}
	
	/**
	 * Erstellt die {@link PaginationDAO} mit einem bereits vorhandenen {@link DAO}
	 *
	 * @param dao die bereits vorhandene {@link DAO}
	 */
	public PaginationDAO(DAO<? extends DBObject, ? extends Number> dao) {
		super(dao);
	}
	
	/**
	 * Lädt eine Seite aus der Datenbank
	 *
	 * @param pageNumber die Seitennummer
	 * @param pageSize   die Größe einer Seite
	 *
	 * @return die Seite. Niemals {@code null}
	 */
	public List<T> loadPage(int pageNumber, int pageSize) {
		return loadPage(pageNumber, pageSize, null, null, null);
	}
	
	/**
	 * Lädt eine Seite aus der Datenbank mit erweiterten Bedingungen. Die Pagination muss hier außerhalb der LIMIT Klausel manuell erfolgen
	 *
	 * @param pageNumber die Seitennummer
	 * @param pageSize   die Größe einer Seite
	 * @param join       Die JOIN Klausel oder {@code null}
	 * @param where      Die WHERE Klausel oder {@code null}
	 * @param params     Die {@link Parameter} oder {@code null}
	 *
	 * @return die Seite. Niemals {@code null}
	 */
	public List<T> loadPage(int pageNumber, int pageSize, String join, String where, ParameterList params) {
		return loadAllFromWhere(join, where, params, (pageNumber * pageSize) + ", " + ((pageNumber + 1) * pageSize), getPrimaryCol());
	}
	
	/**
	 * Lädt eine Seite aus der Datenbank mit eigenen Bedingungen. Die Pagination muss hier manuell erfolgen
	 *
	 * @param join          Die JOIN Klausel oder {@code null}
	 * @param where         Die WHERE Klausel oder {@code null}
	 * @param params        Die {@link Parameter} oder {@code null}
	 * @param limit         das Limit für die Anzahl der Ergebnisse oder {@code null}
	 * @param order         Die ORDER Klausel oder {@code null}
	 * @param loadedObjects Objekte, die schon geladen wurden und somit nicht neu geladen werden müssen
	 *
	 * @return die Seite. Niemals {@code null}
	 */
	public List<T> loadCustomPage(final String join, final String where, final ParameterList params, final String limit, final String order, final DBObject... loadedObjects) {
		return loadAllFromWhere(join, where, params, limit, order, loadedObjects);
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
		executionContext.putInt("paginationdao.done", done);
	}
}
