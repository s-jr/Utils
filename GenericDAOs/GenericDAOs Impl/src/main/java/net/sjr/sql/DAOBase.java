package net.sjr.sql;

import net.sjr.sql.exceptions.UncheckedSQLException;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@SuppressWarnings("WeakerAccess")
public abstract class DAOBase<CP extends DAOConnectionPoolBase<C>, C extends DAOConnectionBase<? extends DAOBase>> implements AutoCloseable {
	protected final DataSource dataSource;
	protected final Connection staticConnection;
	private final Logger log = LoggerFactory.getLogger(getClass());
	protected CP connectionPool;
	
	/**
	 * Erstellt die {@link DAOBase} mit einer {@link DataSource}
	 *
	 * @param ds die {@link DataSource}
	 */
	public DAOBase(final @NotNull DataSource ds) {
		dataSource = ds;
		staticConnection = null;
		connectionPool = createConnectionPool();
	}
	
	/**
	 * Erstellt die {@link DAOBase} mit einer bereits vorhandenen Datenbankverbindung
	 *
	 * @param con die bereits vorhandene Datenbankverbindung
	 */
	public DAOBase(final @NotNull Connection con) {
		dataSource = null;
		staticConnection = con;
		connectionPool = createConnectionPool();
	}
	
	/**
	 * Erstellt die {@link DAOBase} mit einem bereits vorhandenen {@link DAOBase}
	 *
	 * @param dao die bereits vorhandene {@link DAOBase}
	 */
	public DAOBase(final @NotNull DAOBase<?, ?> dao) {
		dataSource = dao.dataSource;
		staticConnection = dao.staticConnection;
		connectionPool = createConnectionPool();
	}
	
	/**
	 * Gibt den Namen der Kreuztabelle zurück
	 *
	 * @return der Name der Kreuztabelle
	 */
	protected abstract @NotNull String getTable();
	
	/**
	 * Gibt den Dtype zurück, welcher bei vererbten Objekten den effektiven Typ angibt
	 *
	 * @return der DType
	 */
	protected @Nullable String getDtype() {
		return null;
	}
	
	/**
	 * Erstellt einen {@link DAOConnectionPool}
	 *
	 * @return der {@link DAOConnectionPool}
	 */
	protected abstract CP createConnectionPool();
	
	/**
	 * Optionale Konfiguration für den {@link DAOConnectionPoolBase}
	 *
	 * @return das Konfigurationsobjekt
	 */
	protected GenericObjectPoolConfig getPoolConfig() {
		return null;
	}
	
	/**
	 * Holt aus der {@link DataSource} die {@link Connection}. Nützlich, wenn eine andere Methode als {@link DataSource#getConnection()} genutzt werden soll
	 *
	 * @return die Connection
	 * @throws SQLException Wenn eine {@link SQLException} aufgetreten ist
	 */
	protected @NotNull Connection getConnectionFromDataSource() throws SQLException {
		if (dataSource == null) throw new IllegalStateException("Es gibt keine DataSource");
		return dataSource.getConnection();
	}
	
	/**
	 * Schließt eine aus der {@link DataSource} erstellte {@link Connection}. Nützlich, wenn eine andere Methode als {@link Connection#close()} genutzt werden soll
	 *
	 * @throws SQLException Wenn eine {@link SQLException} aufgetreten ist
	 */
	protected void closeConnectionFromDataSource(Connection connection) throws SQLException {
		SQLUtils.closeSqlAutocloseable(log, connection);
	}
	
	/**
	 * Öffnet die Datenbankverbindung. Wenn shouldCloseAlways true zurück gibt, wird diese direkt wieder geschlossen. Sonst nicht
	 */
	public void tryConnection() {
		C con = null;
		try {
			con = connectionPool.borrowObject();
		}
		catch (final RuntimeException e) {
			throw e;
		}
		catch (final SQLException e) {
			throw new UncheckedSQLException(e);
		}
		catch (final Exception e) {
			throw new RuntimeException(e);
		}
		finally {
			doCloseAlways(con, null);
		}
	}
	
	/**
	 * An Hand der Rückgabe wird entschieden, ob nach jeder Funktion die Datenbankverbindung inkl. {@link PreparedStatement}s geschlossen werden soll.
	 *
	 * @return {@code true} wenn immer geschlossen werden soll (default) oder {@code false} wenn nicht
	 */
	protected boolean shouldCloseAlways() {
		return true;
	}
	
	/**
	 * Logt das {@link PreparedStatement}, führt es aus und gibt das {@link ResultSet} zurück
	 *
	 * @param pst das {@link PreparedStatement}
	 * @return das {@link ResultSet}
	 * @throws SQLException Wenn eine {@link SQLException} aufgetreten ist
	 */
	protected @NotNull ResultSet getResultSet(final @NotNull PreparedStatement pst) throws SQLException {
		logPst(pst);
		return pst.executeQuery();
	}
	
	/**
	 * Logt ein {@link PreparedStatement}
	 *
	 * @param pst das {@link PreparedStatement}
	 */
	protected void logPst(final @NotNull PreparedStatement pst) {
		log.debug(SQLUtils.pstToSQL(pst));
	}
	
	/**
	 * Schließt das {@link PreparedStatement} und die Datenbankverbindung, wenn gewünscht
	 *
	 * @param con die zu schließende {@link DAOConnectionBase}
	 * @param pst das {@link PreparedStatement}
	 */
	protected void doCloseAlways(@Nullable C con, final @Nullable PreparedStatement pst) {
		if (shouldCloseAlways()) {
			if (con != null) {
				try {
					connectionPool.invalidateObject(con);
				}
				catch (RuntimeException e) {
					throw e;
				}
				catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			closeSqlAutocloseable(pst);
		}
		else if (con != null) connectionPool.returnObject(con);
	}
	
	/**
	 * Schließt alle Datenbankverbindungen im {@link DAOConnectionPool}
	 */
	@Override
	public void close() {
		connectionPool.close();
		connectionPool = createConnectionPool();
	}
	
	/**
	 * Schließt {@link AutoCloseable} mit {@code null} Check und Fehlerabfangung. Besondere Fehlerbeschreibung bei SQL Fehlern
	 *
	 * @param closeables die {@link AutoCloseable}
	 */
	protected void closeSqlAutocloseable(final @Nullable AutoCloseable... closeables) {
		SQLUtils.closeSqlAutocloseable(log, closeables);
	}
	
}
