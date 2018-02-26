package net.sjr.sql.spring;

import net.sjr.sql.*;
import net.sjr.sql.exceptions.UncheckedSQLException;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Erweitert die {@link DAO}s um die Möglichkeit als Spring Batch Writer zu dienen und fügt die vom {@link DAOReaderLimit} benötigten Methoden hinzu
 *
 * @param <T> Typ des zu gespeichernden Java Objektes
 * @param <P> Typ des Primary Keys
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class PaginationDAO<T extends DBObject<P>, P extends Number> extends DAO<T, P> implements ItemStreamWriter<T> {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private int done = 0;
	
	/**
	 * Erstellt die {@link PaginationDAO} mit einer {@link DataSource}
	 *
	 * @param ds die {@link DataSource}
	 */
	public PaginationDAO(final @NotNull DataSource ds) {
		super(ds);
	}
	
	/**
	 * Erstellt die {@link PaginationDAO} mit einer bereits vorhandenen Datenbankverbindung
	 *
	 * @param con die bereits vorhandene Datenbankverbindung
	 */
	public PaginationDAO(final @NotNull Connection con) {
		super(con);
	}
	
	/**
	 * Erstellt die {@link PaginationDAO} mit einem bereits vorhandenen {@link DAO}
	 *
	 * @param dao die bereits vorhandene {@link DAO}
	 */
	public PaginationDAO(final @NotNull DAO<? extends DBObject, ? extends Number> dao) {
		super(dao);
	}
	
	@Override
	protected @NotNull Connection getConnectionFromDataSource() throws SQLException {
		return DataSourceUtils.doGetConnection(dataSource);
	}
	
	@Override
	public void close() {
		if (log != null) log.debug("Closing DAO...");
		if (pstCache != null) {
			for (final PreparedStatement pst : pstCache.values()) {
				SQLUtils.closeSqlAutocloseable(log, pst);
			}
			pstCache.clear();
		}
		if (dataSource != null) {
			try {
				DataSourceUtils.doReleaseConnection(connection, dataSource);
			}
			catch (final SQLException e) {
				throw new UncheckedSQLException(e);
			}
		}
	}
	
	/**
	 * Lädt eine Seite aus der Datenbank mit erweiterten Bedingungen
	 *
	 * @param pageNumber die Seitennummer
	 * @param pageSize   die Größe einer Seite
	 * @param join       Die JOIN Klausel oder {@code null}
	 * @param where      Die WHERE Klausel oder {@code null}
	 * @param params     Die {@link Parameter} oder {@code null}
	 *
	 * @return die Seite. Niemals {@code null}
	 */
	public @NotNull List<T> loadPage(final int pageNumber, final int pageSize, final @Nullable String join, final @Nullable String where, final @Nullable ParameterList params) {
		return loadAllFromWhere(join, where, params, (pageNumber * pageSize) + ", " + ((pageNumber + 1) * pageSize), getPrimaryCol());
	}
	
	/**
	 * Lädt eine Seite aus der Datenbank mit erweiterten Bedingungen
	 *
	 * @param lastPrimary die kleinste Primary ID, nach der geladen werden soll
	 * @param pageSize    die Größe einer Seite
	 * @param join        Die JOIN Klausel oder {@code null}
	 * @param where       Die WHERE Klausel oder {@code null}
	 * @param params      Die {@link Parameter} oder {@code null}
	 * @return die Seite. Niemals {@code null}
	 */
	public @NotNull List<T> loadPageFromPrimary(final @Nullable P lastPrimary, final int pageSize, final @Nullable String join, final @Nullable String where, final @Nullable ParameterList params) {
		String fullWhere;
		if (StringUtils.isBlank(where)) fullWhere = "";
		else fullWhere = '(' + where + ") AND ";
		fullWhere += getPrimaryCol() + " > ?";
		
		ParameterList fullParams;
		if (params == null) fullParams = new ParameterList();
		else fullParams = new ParameterList(params);
		fullParams.addParameter(lastPrimary == null ? 0 : lastPrimary);
		
		return loadAllFromWhere(join, fullWhere, fullParams, String.valueOf(pageSize), getPrimaryCol(), "loadPageFromPrimary" + getPrimaryCol());
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
	public @NotNull List<T> loadCustomPage(final @Nullable String join, final @Nullable String where, final @Nullable ParameterList params, final @Nullable String limit, final @Nullable String order, final DBObject... loadedObjects) {
		return loadAllFromWhere(join, where, params, limit, order, loadedObjects);
	}
	
	@Override
	public void write(final @NotNull List<? extends T> items) {
		for (int itemsSize = items.size(); done < itemsSize; done++) {
			T item = items.get(done);
			insertOrUpdate(item);
		}
		done = 0;
	}
	
	@Override
	public void open(final @NotNull ExecutionContext executionContext) {
		if (executionContext.containsKey("paginationdao.done")) {
			done = executionContext.getInt("paginationdao.done");
		}
	}
	
	@Override
	public void update(final @NotNull ExecutionContext executionContext) {
		executionContext.putInt("paginationdao.done", done);
	}
}
