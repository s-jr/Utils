package net.sjr.sql;

import net.sjr.sql.exceptions.EntryNotFoundException;
import net.sjr.sql.exceptions.UncheckedSQLException;
import net.sjr.sql.exceptions.UnsupportedPrimaryException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"WeakerAccess", "JavaDoc", "SqlDialectInspection", "SqlNoDataSourceInspection", "unchecked", "unused", "SameParameterValue", "SqlResolve", "UnusedReturnValue"})
public abstract class DAO<T extends DBObject<P>, P extends Number> implements AutoCloseable {
	private final Logger log = LogManager.getLogger(getClass());

	private final DataSource dataSource;
	private Connection connection;
	protected final Map<String, PreparedStatement> pstCache = new HashMap<>();

	/**
	 * Erstellt die DAO mit einer DataSource
	 *
	 * @param ds die DataSource
	 */
	public DAO(final DataSource ds) {
		dataSource = ds;
		connection = null;
	}

	/**
	 * Erstellt die DAO mit einer bereits vorhandenen Datenbankverbindung
	 *
	 * @param con die bereits vorhandene Datenbankverbindung
	 */
	public DAO(final Connection con) {
		connection = con;
		dataSource = null;
	}

	/**
	 * Erstellt die DAO mit einem bereits vorhandenen DAO
	 *
	 * @param dao das bereits vorhandene DAO
	 */
	public DAO(final DAO<? extends DBObject, ? extends Number> dao) {
		if (dao.dataSource == null) {
			this.connection = dao.getConnection();
			this.dataSource = null;
		}
		else {
			this.dataSource = dao.dataSource;
			this.connection = dao.connection;
		}
	}


	protected Connection getConnection() {
		try {
			if (connection == null || connection.isClosed()) {
				if (dataSource != null) {
					connection = dataSource.getConnection();
				}
				else {
					throw new IllegalStateException("Die DAO hat keine Connection und keine DataSource!");
				}
			}
			return connection;
		}
		catch (SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}

	/**
	 * @return Alle Datenbankfelder inklusive ID mit Komma getrennt
	 */
	protected String getFelderID() {
		return SQLUtils.fullQualifyTableName(getPrimaryCol() + ", " + getFelder(), getTable());
	}

	/**
	 * @return Alle Datenbankfelder exclusive ID mit Komma getrennt
	 */
	protected abstract String getFelder();

	/**
	 * @return Der Tabellenname
	 */
	protected abstract String getTable();

	/**
	 * @return Der name der Primary Zeile
	 */
	protected abstract String getPrimaryCol();

	/**
	 * @param v Das Objekt von dem die Parameterliste erstellt werden soll
	 *
	 * @return eine ParameterList aller Parameter Spalten für die DB
	 */
	protected abstract ParameterList getPList(T v);

	/**
	 * Wird aufgerufen wärend einem Insert um die Möglichkeit zu bieten abhängige Objekte auch einzufügen
	 *
	 * @param v das einzufügende Objekt
	 *
	 * @return eine Map mit allen geänderten Werten
	 */
	protected Map<String, P> cascadeInsert(T v) {
		return null;
	}

	/**
	 * Wird aufgerufen wärend einem Update um die Möglichkeit zu bieten abhängige Objekte auch zu updaten
	 *
	 * @param v das upzudatende Objekt
	 *
	 * @return eine Map mit allen geänderten Werten
	 */
	protected Map<String, P> cascadeUpdate(T v) {
		return null;
	}

	/**
	 * Wird aufgerufen wärend einem Delete um die Möglichkeit zu bieten abhängige Objekte auch zu löschen
	 *
	 * @param v das zu löschende Objekt
	 *
	 * @return eine Map mit allen geänderten Werten
	 */
	protected Map<String, P> cascadeDelete(T v) {
		return null;
	}

	/**
	 * Holt aus dem ResultSet alle wichtigen Daten und erstellt aus diesem ein neues Objekt
	 *
	 * @param rs            Das ResultSet mit den Daten
	 * @param loadedObjects Objekte, die schon geladen wurden und somit nicht neu geladen werden müssen
	 *
	 * @return Ein neues Objekt aus dem ResultSet
	 *
	 * @throws SQLException wenn eine SQLException auftritt
	 */
	protected abstract T getFromRS(ResultSet rs, DBObject... loadedObjects) throws SQLException;

	protected abstract void fillObject(ResultSet rs, T result, DBObject... loadedObjects) throws SQLException;

	protected String getDtype() {
		return null;
	}

	private PreparedStatement loadFromIDPst() throws SQLException {
		PreparedStatement result = pstCache.get("loadFromID");
		if (result == null) {
			result = getConnection().prepareStatement("SELECT " + getFelderID() + " FROM " + getTable() + " WHERE " + getPrimaryCol() + (getDtype() != null ? "=? AND DType" : "") + "=? LIMIT 1");
			pstCache.put("loadFromID", result);
		}
		return result;
	}

	/**
	 * Lädt ein Objekt von T an Hand seiner PrimaryID
	 *
	 * @param primary die PrimaryID des Objektes
	 *
	 * @return das Objekt, niemals null
	 *
	 * @throws EntryNotFoundException wenn es kein Objekt mit der ID gibt
	 */
	public T loadFromID(final P primary) {
		try {
			PreparedStatement pst = loadFromIDPst();
			new Parameter(primary).setParameter(pst, 1);
			if (getDtype() != null) {
				new Parameter(getDtype()).setParameter(pst, 2);
			}
			try (ResultSet rs = getResultSet(pst)) {
				if (rs.next()) {
					return getFromRS(rs);
				}
				throw new EntryNotFoundException(getPrimaryCol(), primary);
			}
		}
		catch (SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}

	private PreparedStatement loadAllPst() throws SQLException {
		PreparedStatement result = pstCache.get("loadAll");
		if (result == null) {
			result = getConnection().prepareStatement("SELECT " + getFelderID() + " FROM " + getTable() + (getDtype() != null ? " WHERE DType=?" : ""));
			pstCache.put("loadAll", result);
		}
		return result;
	}

	/**
	 * Lädt eine Liste aller Objekte von T
	 *
	 * @return Eine Liste aller Objekte von T. Niemals null
	 */
	public List<T> loadAll() {
		try {
			PreparedStatement pst = loadAllPst();
			try (ResultSet rs = getResultSet(pst)) {
				if (getDtype() != null) {
					new Parameter(getDtype()).setParameter(pst, 1);
				}
				List<T> result = new ArrayList<>();
				while (rs.next()) {
					T b = getFromRS(rs);
					result.add(b);
				}
				return result;
			}
		}
		catch (SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}

	private PreparedStatement insertPst() throws SQLException {
		PreparedStatement result = pstCache.get("insert");
		if (result == null) {
			String felder = (getDtype() == null ? "" : "DType, ") + getFelder();
			result = getConnection().prepareStatement("INSERT INTO " + getTable() + " (" + felder + ") VALUES (" + SQLUtils.getFragezeichenInsert(felder) + ")", Statement.RETURN_GENERATED_KEYS);
			pstCache.put("insert", result);
		}
		return result;
	}

	/**
	 * Fügt ein Objekt von T in die Datenbank ein<br>
	 * <b>Das Objekt darf noch keine PrimaryID haben!</b>
	 *
	 * @param v das einzufügende Objekt
	 *
	 * @return eine Map mit allen geänderten Werten
	 *
	 * @throws IllegalStateException wenn das Objekt eine PrimaryID hat
	 */
	public Map<String, P> insertIntoDB(final T v) {
		if (v.getPrimary() == null) {
			Map<String, P> result = new HashMap<>();

			Map<String, P> cascadeIDs = cascadeInsert(v);
			if (cascadeIDs != null) {
				result.putAll(cascadeIDs);
			}

			try {
				PreparedStatement pst = insertPst();
				int pos = 1;
				if (getDtype() != null) {
					pos = new Parameter(getDtype()).setParameter(pst, pos);
				}

				ParameterList pList = getPList(v);

				pList.setParameter(pst, pos);
				logPst(pst);
				pst.executeUpdate();

				try (ResultSet rs = pst.getGeneratedKeys()) {
					if (rs.next()) {
						v.setPrimary(getPrimary(rs));
						result.put(getTable() + "." + getPrimaryCol(), v.getPrimary());
						return result;
					}
					throw new RuntimeException("rs.next returned false for generated keys");
				}
			}
			catch (SQLException e) {
				throw new UncheckedSQLException(e);
			}
		}
		throw new IllegalStateException("Der Eintrag wurde bereits in die Datenbank eingefügt!");
	}

	P getPrimary(ResultSet rs, int pos) throws SQLException {
		Type type = getClass();
		while (type instanceof Class) {
			type = ((Class) type).getGenericSuperclass();
		}
		Class<P> genericClass = (Class<P>) ((ParameterizedType) type).getActualTypeArguments()[1];
		if (genericClass.equals(Integer.class)) return (P) SQLUtils.getNullableInt(rs, pos);
		if (genericClass.equals(Long.class)) return (P) SQLUtils.getNullableLong(rs, pos);
		if (genericClass.equals(Byte.class)) return (P) SQLUtils.getNullableByte(rs, pos);
		if (genericClass.equals(Short.class)) return (P) SQLUtils.getNullableShort(rs, pos);
		if (genericClass.equals(Double.class)) return (P) SQLUtils.getNullableDouble(rs, pos);
		if (genericClass.equals(Float.class)) return (P) SQLUtils.getNullableFloat(rs, pos);
		throw new UnsupportedPrimaryException(genericClass.getName());
	}

	private P getPrimary(ResultSet rs) throws SQLException {
		return getPrimary(rs, 1);
	}

	private PreparedStatement updatePst() throws SQLException {
		PreparedStatement result = pstCache.get("update");
		if (result == null) {
			String felder = (getDtype() == null ? "" : "DType, ") + getFelder();
			result = getConnection().prepareStatement("UPDATE " + getTable() + " SET " + SQLUtils.getFragezeichenUpdate(felder) + " WHERE " + getPrimaryCol() + "=?");
			pstCache.put("update", result);
		}
		return result;
	}

	/**
	 * Aktualisiert ein Objekt von T in der Datenbank<br>
	 * <b>Das Objekt muss eine PrimaryID haben um es in der Datenbank zu identifizieren!</b>
	 *
	 * @param v das zu aktualisierende Objekt
	 *
	 * @return eine Map mit allen geänderten Werten
	 *
	 * @throws IllegalStateException wenn das Objekt keine PrimaryID hat
	 */
	public Map<String, P> updateIntoDB(final T v) {
		if (v.getPrimary() != null) {
			Map<String, P> result = new HashMap<>();

			Map<String, P> cascadeIDs = cascadeUpdate(v);
			if (cascadeIDs != null) {
				result.putAll(cascadeIDs);
			}

			try {
				PreparedStatement pst = updatePst();
				ParameterList pList = getPList(v);
				pList.addParameter(v.getPrimary());

				int pos = 1;
				if (getDtype() != null) {
					pos = new Parameter(getDtype()).setParameter(pst, pos);
				}

				pList.setParameter(pst, pos);
				logPst(pst);
				pst.executeUpdate();

				return result;
			}
			catch (SQLException e) {
				throw new UncheckedSQLException(e);
			}
		}
		throw new IllegalStateException("Der Eintrag wurde noch nicht in die Datenbank eingefügt!");
	}

	private PreparedStatement deletePst() throws SQLException {
		PreparedStatement result = pstCache.get("delete");
		if (result == null) {
			result = getConnection().prepareStatement("DELETE FROM " + getTable() + " WHERE " + getPrimaryCol() + "=?" + (getDtype() != null ? " AND DType=?" : ""));
			pstCache.put("delete", result);
		}
		return result;
	}

	/**
	 * Löscht ein Objekt von T aus der Datenbank<br>
	 * <b>Das Objekt muss eine PrimaryID haben um es in der Datenbank zu identifizieren!</b>
	 *
	 * @param v das zu löschende Objekt
	 *
	 * @return eine Map mit allen geänderten Werten
	 *
	 * @throws IllegalStateException wenn das Objekt keine PrimaryID hat
	 */
	public Map<String, P> deleteFromDB(final T v) {
		if (v.getPrimary() != null) {

			try {
				PreparedStatement pst = deletePst();
				new Parameter(v.getPrimary()).setParameter(pst, 1);

				if (getDtype() != null) {
					new Parameter(getDtype()).setParameter(pst, 2);
				}
				logPst(pst);
				pst.executeUpdate();
				v.setPrimary(null);
				Map<String, P> result = new HashMap<>();
				result.put(getTable() + "." + getPrimaryCol(), null);

				Map<String, P> cascadeIDs = cascadeDelete(v);
				if (cascadeIDs != null) {
					result.putAll(cascadeIDs);
				}
				return result;
			}
			catch (SQLException e) {
				throw new UncheckedSQLException(e);
			}
		}
		throw new IllegalStateException("Der Eintrag wurde noch nicht in die Datenbank eingefügt!");
	}

	/**
	 * Aktualisiert ein Objekt von T in der Datenbank oder fügt es ein, je nachdem ob es eine PrimaryID hat, oder nicht
	 *
	 * @param v das einzufügende oder zu aktualisierende Objekt
	 *
	 * @return eine Map mit allen geänderten Werten
	 */
	public Map<String, P> insertOrUpdate(final T v) {
		if (v.getPrimary() == null) {
			return insertIntoDB(v);
		}
		else {
			return updateIntoDB(v);
		}
	}

	/**
	 * Lädt einen Eintrag aus der Datenbank mit benutzerspezifizierten Bedingungen
	 *
	 * @param join          Die JOIN Klausel oder null
	 * @param col           Die Spalte für WHERE
	 * @param param         Der Wert für WHERE
	 * @param loadedObjects Objekte, die schon geladen wurden und somit nicht neu geladen werden müssen
	 *
	 * @return das gefundene Objekt. Niemals null
	 *
	 * @throws EntryNotFoundException wenn kein Eintrag gefunden wurde
	 */
	protected T loadOneFromCol(final String join, final String col, final Object param, final DBObject... loadedObjects) {
		return loadOneFromCol(join, col, param, null, loadedObjects);
	}

	/**
	 * Lädt einen Eintrag aus der Datenbank mit benutzerspezifizierten Bedingungen
	 *
	 * @param join          Die JOIN Klausel oder null
	 * @param col           Die Spalte für WHERE
	 * @param param         Der Wert für WHERE
	 * @param cacheKey      der Key für den pstCache
	 * @param loadedObjects Objekte, die schon geladen wurden und somit nicht neu geladen werden müssen
	 *
	 * @return das gefundene Objekt. Niemals null
	 *
	 * @throws EntryNotFoundException wenn kein Eintrag gefunden wurde
	 */
	protected T loadOneFromCol(final String join, final String col, final Object param, final String cacheKey, final DBObject... loadedObjects) {
		List<T> list = loadAllFromCol(join, col, param, "1", null, cacheKey, loadedObjects);
		if (list.size() < 1) {
			throw new EntryNotFoundException(col, param);
		}
		T result = list.get(0);
		if (result == null) {
			throw new RuntimeException();
		}
		return result;
	}

	/**
	 * Lädt alle möglichen Einträge aus der Datenbank mit benutzerspezifizierten Bedingungen
	 *
	 * @param join          Die JOIN Klausel oder null
	 * @param col           Die Spalte für WHERE
	 * @param param         Der Wert für WHERE
	 * @param limit         das Limit für die Anzahl der Ergebnisse oder null
	 * @param order         Die ORDER Klausel oder null
	 * @param loadedObjects Objekte, die schon geladen wurden und somit nicht neu geladen werden müssen
	 *
	 * @return eine Liste aller gefundenen Objekte. Niemals null
	 */
	protected List<T> loadAllFromCol(final String join, final String col, final Object param, final String limit, final String order, final DBObject... loadedObjects) {
		return loadAllFromCol(join, col, param, limit, order, null, loadedObjects);
	}

	/**
	 * Lädt alle möglichen Einträge aus der Datenbank mit benutzerspezifizierten Bedingungen
	 *
	 * @param join          Die JOIN Klausel oder null
	 * @param col           Die Spalte für WHERE
	 * @param param         Der Wert für WHERE
	 * @param limit         das Limit für die Anzahl der Ergebnisse oder null
	 * @param order         Die ORDER Klausel oder null
	 * @param cacheKey      der Key für den pstCache
	 * @param loadedObjects Objekte, die schon geladen wurden und somit nicht neu geladen werden müssen
	 *
	 * @return eine Liste aller gefundenen Objekte. Niemals null
	 */
	protected List<T> loadAllFromCol(final String join, final String col, final Object param, final String limit, final String order, final String cacheKey, final DBObject... loadedObjects) {
		return loadAllFromWhere(join, col + "=?", new ParameterList(param), limit, order, cacheKey, loadedObjects);
	}

	/**
	 * Lädt alle möglichen Einträge aus der Datenbank mit benutzerspezifizierten Bedingungen
	 *
	 * @param join          Die JOIN Klausel oder null
	 * @param where         Die WHERE Klausel oder null
	 * @param params        Die Parameter oder null
	 * @param loadedObjects Objekte, die schon geladen wurden und somit nicht neu geladen werden müssen
	 *
	 * @return eine Liste aller gefundenen Objekte. Niemals null
	 */
	protected T loadOneFromWhere(final String join, final String where, final ParameterList params, final DBObject... loadedObjects) {
		return loadOneFromWhere(join, where, params, null, loadedObjects);
	}

	/**
	 * Lädt alle möglichen Einträge aus der Datenbank mit benutzerspezifizierten Bedingungen
	 *
	 * @param join          Die JOIN Klausel oder null
	 * @param where         Die WHERE Klausel oder null
	 * @param params        Die Parameter oder null
	 * @param cacheKey      der Key für den pstCache
	 * @param loadedObjects Objekte, die schon geladen wurden und somit nicht neu geladen werden müssen
	 *
	 * @return eine Liste aller gefundenen Objekte. Niemals null
	 */
	protected T loadOneFromWhere(final String join, final String where, final ParameterList params, final String cacheKey, final DBObject... loadedObjects) {
		List<T> list = loadAllFromWhere(join, where, params, "1", null, cacheKey, loadedObjects);
		if (list.size() < 1) {
			throw new EntryNotFoundException();
		}
		T result = list.get(0);
		if (result == null) {
			throw new RuntimeException();
		}
		return result;
	}

	/**
	 * Lädt alle möglichen Einträge aus der Datenbank mit benutzerspezifizierten Bedingungen
	 *
	 * @param join          Die JOIN Klausel oder null
	 * @param where         Die WHERE Klausel oder null
	 * @param params        Die Parameter oder null
	 * @param limit         das Limit für die Anzahl der Ergebnisse oder null
	 * @param order         Die ORDER Klausel oder null
	 * @param loadedObjects Objekte, die schon geladen wurden und somit nicht neu geladen werden müssen
	 *
	 * @return eine Liste aller gefundenen Objekte. Niemals null
	 */
	protected List<T> loadAllFromWhere(final String join, final String where, final ParameterList params, final String limit, final String order, final DBObject... loadedObjects) {
		return loadAllFromWhere(join, where, params, limit, order, null, loadedObjects);
	}

	/**
	 * Lädt alle möglichen Einträge aus der Datenbank mit benutzerspezifizierten Bedingungen
	 *
	 * @param join          Die JOIN Klausel oder null
	 * @param where         Die WHERE Klausel oder null
	 * @param params        Die Parameter oder null
	 * @param limit         das Limit für die Anzahl der Ergebnisse oder null
	 * @param order         Die ORDER Klausel oder null
	 * @param cacheKey      der Key für den pstCache
	 * @param loadedObjects Objekte, die schon geladen wurden und somit nicht neu geladen werden müssen
	 *
	 * @return eine Liste aller gefundenen Objekte. Niemals null
	 */
	protected List<T> loadAllFromWhere(final String join, final String where, final ParameterList params, final String limit, final String order, final String cacheKey, final DBObject... loadedObjects) {
		try (PreparedStatement pst = getPst(getFelderID(), join, where, limit, order, cacheKey, params)) {
			setParameter(params, pst);

			try (ResultSet rs = getResultSet(pst)) {
				List<T> result = new ArrayList<>();
				while (rs.next()) {
					T b = getFromRS(rs, loadedObjects);
					result.add(b);
				}
				return result;
			}
		}
		catch (SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}

	private ResultSet getResultSet(PreparedStatement pst) throws SQLException {
		logPst(pst);
		return pst.executeQuery();
	}

	/**
	 * Lädt die Anzahl aller möglichen Einträge aus der Datenbank mit benutzerspezifizierten Bedingungen
	 *
	 * @param join  Die JOIN Klausel oder null
	 * @param col   Die Spalte für WHERE
	 * @param param Der Wert für WHERE
	 *
	 * @return eine Liste aller gefundenen Objekte. Niemals null
	 */
	protected long loadCountFromCol(final String join, final String col, final Object param) {
		return loadCountFromCol(join, col, param, null);
	}

	/**
	 * Lädt die Anzahl aller möglichen Einträge aus der Datenbank mit benutzerspezifizierten Bedingungen
	 *
	 * @param join     Die JOIN Klausel oder null
	 * @param col      Die Spalte für WHERE
	 * @param param    Der Wert für WHERE
	 * @param cacheKey der Key für den pstCache
	 *
	 * @return eine Liste aller gefundenen Objekte. Niemals null
	 */
	protected long loadCountFromCol(final String join, final String col, final Object param, final String cacheKey) {
		return loadCountFromWhere(join, col + "=?", new ParameterList(param), cacheKey);
	}

	/**
	 * Lädt die Anzahl aller möglichen Einträge aus der Datenbank mit benutzerspezifizierten Bedingungen
	 *
	 * @param join   Die JOIN Klausel oder null
	 * @param where  Die WHERE Klausel oder null
	 * @param params Die Parameter
	 *
	 * @return die Anzahl der Parameter
	 */
	protected long loadCountFromWhere(final String join, final String where, final ParameterList params) {
		return loadCountFromWhere(join, where, params, null);
	}

	/**
	 * Lädt die Anzahl aller möglichen Einträge aus der Datenbank mit benutzerspezifizierten Bedingungen
	 *
	 * @param join     Die JOIN Klausel oder null
	 * @param where    Die WHERE Klausel oder null
	 * @param params   Die Parameter oder null
	 * @param cacheKey der Key für den pstCache
	 *
	 * @return die Anzahl der Parameter
	 */
	protected long loadCountFromWhere(final String join, final String where, final ParameterList params, final String cacheKey) {
		try (PreparedStatement pst = getPst("count(*)", join, where, null, null, cacheKey, params)) {
			setParameter(params, pst);

			try (ResultSet rs = getResultSet(pst)) {
				if (rs.next()) {
					return rs.getLong(1);
				}
				throw new RuntimeException("rs.next() bei SELECT count(*) ist false");
			}
		}
		catch (SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}

	/**
	 * Lädt einen Liste mit einem einzigen Wert als String
	 *
	 * @param feld   das Feld aus dem der Wert geladen werden soll
	 * @param join   Die JOIN Klausel oder null
	 * @param where  Die WHERE Klausel oder null
	 * @param params Die Parameter oder null
	 * @param limit  das Limit für die Anzahl der Ergebnisse oder null
	 * @param order  Die ORDER Klausel oder null
	 *
	 * @return die Liste mit Strings
	 */
	protected List<String> loadSingleValuesAsString(final String feld, final String join, final String where, final ParameterList params, final String limit, final String order) {
		return loadSingleValuesAsString(feld, join, where, params, limit, order, null);
	}

	/**
	 * Lädt einen Liste mit einem einzigen Wert als String
	 *
	 * @param feld     das Feld aus dem der Wert geladen werden soll
	 * @param join     Die JOIN Klausel oder null
	 * @param where    Die WHERE Klausel oder null
	 * @param params   Die Parameter oder null
	 * @param limit    das Limit für die Anzahl der Ergebnisse oder null
	 * @param order    Die ORDER Klausel oder null
	 * @param cacheKey der Key für den pstCache
	 *
	 * @return die Liste mit Strings
	 */
	protected List<String> loadSingleValuesAsString(final String feld, final String join, final String where, final ParameterList params, final String limit, final String order, final String cacheKey) {
		try (PreparedStatement pst = getPst("DISTINCT " + feld, join, where, limit, order, cacheKey, params)) {
			setParameter(params, pst);

			try (ResultSet rs = getResultSet(pst)) {
				List<String> result = new ArrayList<>();
				while (rs.next()) {
					result.add(rs.getString(1));
				}
				return result;
			}
		}
		catch (SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}

	private void setParameter(ParameterList params, PreparedStatement pst) throws SQLException {
		if (params == null) return;
		int pos = params.setParameter(pst, 1);

		if (getDtype() != null) {
			new Parameter(getDtype()).setParameter(pst, pos);
		}
	}

	static PreparedStatement getPst(final Connection connection, final Map<String, PreparedStatement> pstCache, final String table, final String dType, final String select, final String join, final String where, final String limit, final String order, final String cacheKey, final ParameterList params) throws SQLException {
		PreparedStatement result = cacheKey == null ? null : pstCache.get(cacheKey);
		if (result == null || result.isClosed()) {
			result = connection.prepareStatement(
					"SELECT " + select + " FROM " + table + (StringUtils.isBlank(join) ? "" : " JOIN " + join) + (StringUtils.isBlank(where) && dType == null ? "" : " WHERE " + (StringUtils.isBlank(where) ? "" : SQLUtils.nullableWhere(where, params)))
							+ (dType != null ? " AND DType=?" : "") + (StringUtils.isBlank(order) ? "" : " ORDER BY " + order)
							+ (StringUtils.isBlank(limit) ? "" : " LIMIT " + limit));
			if (cacheKey != null) pstCache.put(cacheKey, result);
		}
		return result;
	}

	private PreparedStatement getPst(final String select, final String join, final String where, final String limit, final String order, final String cacheKey, final ParameterList params) throws SQLException {
		return getPst(getConnection(), pstCache, getTable(), getDtype(), select, join, where, limit, order, cacheKey, params);
	}

	private void logPst(PreparedStatement pst) {
		log.debug(SQLUtils.pstToSQL(pst));
	}

	@Override
	public void close() {
		if (log != null) log.debug("Closing DAO...");
		for (PreparedStatement pst : pstCache.values()) {
			try {
				pst.close();
			}
			catch (SQLException e) {
				if (log != null) log.error(e);
			}
		}
		pstCache.clear();
		if (dataSource != null) {
			try {
				connection.close();
			}
			catch (SQLException e) {
				if (log != null) log.error(e);
			}
		}
	}
}