package net.sjr.sql;

import net.sjr.sql.exceptions.UncheckedSQLException;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class DAOConnectionBase<D extends DAOBase<?, ?>> implements AutoCloseable {
	protected final Connection connection;
	protected final D dao;
	protected final Map<String, PreparedStatement> pstCache = new HashMap<>();
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	/**
	 * Erstellt eine neue {@link DAOConnectionBase}
	 *
	 * @param connection die zu benutzende {@link Connection}
	 * @param dao        die zu benutzende {@link DAOBase}
	 */
	public DAOConnectionBase(Connection connection, D dao) {
		if (connection == null) throw new IllegalArgumentException("Keine Connection angegeben");
		this.connection = connection;
		this.dao = dao;
	}
	
	@Override
	public void close() {
		if (log != null) log.debug("Closing DAOConnection...");
		for (final PreparedStatement pst : pstCache.values()) {
			SQLUtils.closeSqlAutocloseable(log, pst);
		}
		pstCache.clear();
		if (dao.dataSource != null) {
			try {
				dao.closeConnectionFromDataSource(connection);
			}
			catch (SQLException e) {
				if (log != null) log.error("Fehler beim Schließen der Datenbankverbindung", e);
			}
		}
	}
	
	/**
	 * Findet den Typ der Datenbank heraus
	 *
	 * @return der Datenbanktyp
	 */
	public @NotNull DatabaseType getDatabaseType() {
		try {
			DatabaseMetaData metaData = connection.getMetaData();
			String productName = metaData.getDatabaseProductName();
			return DatabaseType.getFromIdentifier(productName);
		}
		catch (final SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}
	
	/**
	 * Baut aus diversen Parametern ein {@link PreparedStatement} zusammen
	 *
	 * @param select   die Felder für die SELECT Klausel
	 * @param join     Die JOIN Klausel oder {@code null}
	 * @param where    Die WHERE Klausel oder {@code null}
	 * @param limit    das Limit für die Anzahl der Ergebnisse oder {@code null}
	 * @param order    Die ORDER Klausel oder {@code null}
	 * @param cacheKey der Key für den pstCache
	 * @param params   die Parameter, die in das {@link PreparedStatement} eingefügt werden
	 * @return das zusammengebaute {@link PreparedStatement}
	 * @throws SQLException Wenn eine {@link SQLException} aufgetreten ist
	 */
	public @NotNull PreparedStatement getPst(final @NotNull String select, final @Nullable String join, final @Nullable String where, final @Nullable String limit, final @Nullable String order, final @Nullable String cacheKey, final @Nullable ParameterList params) throws SQLException {
		PreparedStatement result = dao.shouldCloseAlways() || cacheKey == null ? null : pstCache.get(cacheKey);
		if (result == null || result.isClosed()) {
			String query = "SELECT " + select;
			query += " FROM " + dao.getTable();
			if (!StringUtils.isBlank(join)) query += (join.contains("JOIN") ? " " : " JOIN ") + join;
			if (!StringUtils.isBlank(where) || dao.getDtype() != null) {
				query += " WHERE ";
				if (StringUtils.isBlank(where)) {
					query += "DType=?";
				}
				else {
					query += SQLUtils.nullableWhere(where, params);
					if (dao.getDtype() != null) query += " AND DType=?";
				}
			}
			if (!StringUtils.isBlank(order)) query += " ORDER BY " + order;
			if (!StringUtils.isBlank(limit) && getDatabaseType() != DatabaseType.ORACLE) query += " LIMIT " + limit;
			
			result = prepareStatement(query);
			if (cacheKey != null && !dao.shouldCloseAlways()) pstCache.put(cacheKey, result);
		}
		return result;
	}
	
	/**
	 * Reicht die Abfrage weiter an {@link Connection#createStatement()}
	 *
	 * @see Connection#createStatement()
	 */
	public Statement createStatement() throws SQLException {
		return connection.createStatement();
	}
	
	/**
	 * Reicht die Abfrage weiter an {@link Connection#createStatement(int, int)}
	 *
	 * @see Connection#createStatement(int, int)
	 */
	public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
		return connection.createStatement(resultSetType, resultSetConcurrency);
	}
	
	/**
	 * Reicht die Abfrage weiter an {@link Connection#createStatement(int, int, int)}
	 *
	 * @see Connection#createStatement(int, int, int)
	 */
	public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		return connection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
	}
	
	/**
	 * Reicht die Abfrage weiter an {@link Connection#prepareStatement(String)}
	 *
	 * @see Connection#prepareStatement(String)
	 */
	public PreparedStatement prepareStatement(String query) throws SQLException {
		return connection.prepareStatement(query);
	}
	
	/**
	 * Reicht die Abfrage weiter an {@link Connection#prepareStatement(String, int, int)}
	 *
	 * @see Connection#prepareStatement(String, int, int)
	 */
	public PreparedStatement prepareStatement(String query, int resultSetType, int resultSetConcurrency) throws SQLException {
		return connection.prepareStatement(query, resultSetType, resultSetConcurrency);
	}
	
	/**
	 * Reicht die Abfrage weiter an {@link Connection#prepareStatement(String, int, int, int)}
	 *
	 * @see Connection#prepareStatement(String, int, int, int)
	 */
	public PreparedStatement prepareStatement(String query, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		return connection.prepareStatement(query, resultSetType, resultSetConcurrency, resultSetHoldability);
	}
	
	/**
	 * Reicht die Abfrage weiter an {@link Connection#prepareStatement(String, int)}
	 *
	 * @see Connection#prepareStatement(String, int)
	 */
	public PreparedStatement prepareStatement(String query, int autoGenerateKeys) throws SQLException {
		return connection.prepareStatement(query, autoGenerateKeys);
	}
	
	/**
	 * Reicht die Abfrage weiter an {@link Connection#prepareStatement(String, int[])}
	 *
	 * @see Connection#prepareStatement(String, int[])
	 */
	public PreparedStatement prepareStatement(String query, int[] columnIndexes) throws SQLException {
		return connection.prepareStatement(query, columnIndexes);
	}
	
	/**
	 * Reicht die Abfrage weiter an {@link Connection#prepareStatement(String, String[])}
	 *
	 * @see Connection#prepareStatement(String, String[])
	 */
	public PreparedStatement prepareStatement(String query, String[] columnNames) throws SQLException {
		return connection.prepareStatement(query, columnNames);
	}
}
