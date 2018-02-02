package net.sjr.sql;

import net.sjr.sql.exceptions.EntryNotFoundException;
import net.sjr.sql.exceptions.UncheckedSQLException;
import net.sjr.sql.exceptions.UnsupportedPrimaryException;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * DAO Oberklasse um Datenbankzugriffe zu vereinfachen
 *
 * @param <T> Typ des zu speichernden Java Objektes
 * @param <P> Typ des Primary Keys
 */
@SuppressWarnings({"WeakerAccess", "JavaDoc", "SqlDialectInspection", "SqlNoDataSourceInspection", "unchecked", "unused", "SameParameterValue", "SqlResolve", "UnusedReturnValue", "SameReturnValue", "SynchronizationOnLocalVariableOrMethodParameter"})
public abstract class DAO<T extends DBObject<P>, P extends Number> implements DAOBase<T, P> {
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	protected final DataSource dataSource;
	protected Connection connection;
	protected final Map<String, PreparedStatement> pstCache = new ConcurrentHashMap<>();
	
	private Class<P> primaryClass = null;
	
	/**
	 * Erstellt die {@link DAO} mit einer {@link DataSource}
	 *
	 * @param ds die {@link DataSource}
	 */
	public DAO(final @NotNull DataSource ds) {
		dataSource = ds;
		connection = null;
	}
	
	/**
	 * Erstellt die {@link DAO} mit einer bereits vorhandenen Datenbankverbindung
	 *
	 * @param con die bereits vorhandene Datenbankverbindung
	 */
	public DAO(final @NotNull Connection con) {
		connection = con;
		dataSource = null;
	}
	
	/**
	 * Erstellt die {@link DAO} mit einem bereits vorhandenen {@link DAO}
	 *
	 * @param dao die bereits vorhandene {@link DAO}
	 */
	public DAO(final @NotNull DAO<? extends DBObject, ? extends Number> dao) {
		if (dao.dataSource == null) {
			connection = dao.getConnection();
			dataSource = null;
		}
		else {
			dataSource = dao.dataSource;
			connection = dao.connection;
		}
	}
	
	/**
	 * Öffnet eine neue Datenbankverbindung oder gibt eine bestehende zurück
	 *
	 * @return eine Datenbankverbindung
	 * @throws IllegalStateException wenn keine Connection und keine {@link DataSource}
	 */
	protected synchronized @NotNull Connection getConnection() {
		try {
			if (connection == null || connection.isClosed()) {
				if (dataSource != null) {
					connection = getConnectionFromDataSource();
				}
				else {
					throw new IllegalStateException("Die DAO hat keine Connection und keine DataSource!");
				}
			}
			return connection;
		}
		catch (final SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}
	
	/**
	 * Holt aus der DataSource die Connection. Nützlich, wenn eine andere Methode als {@link DataSource#getConnection()} genutzt werden soll
	 *
	 * @return die Connection
	 * @throws SQLException Wenn eine {@link SQLException} aufgetreten ist
	 */
	protected @NotNull Connection getConnectionFromDataSource() throws SQLException {
		return dataSource.getConnection();
	}
	
	/**
	 * Öffnet die Datenbankverbindung. Wenn shouldCloseAlways true zurück gibt, wird diese direkt wieder geschlossen. Sonst nicht
	 */
	public synchronized void tryConnection() {
		getConnection();
		doCloseAlways(null);
	}
	
	/**
	 * @return Alle Datenbankfelder inklusive ID mit Komma getrennt
	 */
	protected @NotNull String getFelderID() {
		return SQLUtils.fullQualifyTableName(getPrimaryCol() + ", " + getFelder(), getTable());
	}
	
	/**
	 * @return Alle Datenbankfelder exclusive ID mit Komma getrennt
	 */
	protected abstract @NotNull String getFelder();
	
	/**
	 * @return Der Tabellenname
	 */
	protected abstract @NotNull String getTable();
	
	/**
	 * @return Der name der Primary Zeile
	 */
	protected abstract @NotNull String getPrimaryCol();
	
	/**
	 * @param v Das Objekt von dem die {@link ParameterList} erstellt werden soll
	 * @return eine {@link ParameterList} aller {@link Parameter} Spalten für die DB
	 */
	protected abstract @NotNull ParameterList getPList(@NotNull T v);
	
	/**
	 * An Hand der Rückgabe wird entschieden, ob nach jeder Funktion die Datenbankverbindung inkl. {@link PreparedStatement}s geschlossen werden soll.
	 *
	 * @return {@code true} wenn immer geschlossen werden soll (default) oder {@code false} wenn nicht
	 */
	protected boolean shouldCloseAlways() {
		return true;
	}
	
	/**
	 * Wird aufgerufen vor einem Insert um die Möglichkeit zu bieten abhängige Objekte auch einzufügen
	 *
	 * @param v das einzufügende Objekt
	 */
	protected void cascadeInsert(final @NotNull T v) {
	}
	
	/**
	 * Wird aufgerufen vor einem Update um die Möglichkeit zu bieten abhängige Objekte auch zu updaten
	 *
	 * @param v das upzudatende Objekt
	 */
	protected void cascadeUpdate(final @NotNull T v) {
	}
	
	/**
	 * Wird aufgerufen vor einem Delete um die Möglichkeit zu bieten abhängige Objekte auch zu löschen
	 *
	 * @param v das zu löschende Objekt
	 */
	protected void cascadeDelete(final @NotNull T v) {
	}
	
	/**
	 * Wird aufgerufen nach einem Insert um die Möglichkeit zu bieten abhängige Listen auch einzufügen
	 *
	 * @param v das eingefügte Objekt
	 */
	protected void afterInsert(final @NotNull T v) {
	}
	
	/**
	 * Wird aufgerufen nach einem Update um die Möglichkeit zu bieten abhängige Listen auch zu updaten
	 *
	 * @param v das upgedatete Objekt
	 */
	protected void afterUpdate(final @NotNull T v) {
	}
	
	/**
	 * Wird aufgerufen nach einem Delete um die Möglichkeit zu bieten abhängige Listen auch zu löschen
	 *
	 * @param v das gelöschte Objekt
	 */
	protected void afterDelete(final @NotNull T v) {
	}
	
	/**
	 * Holt aus dem {@link ResultSet} alle wichtigen Daten und erstellt aus diesem ein neues Objekt
	 *
	 * @param rs            Das {@link ResultSet} mit den Daten
	 * @param loadedObjects Objekte, die schon geladen wurden und somit nicht neu geladen werden müssen
	 * @return Ein neues Objekt aus dem {@link ResultSet}
	 * @throws SQLException Wenn eine {@link SQLException} aufgetreten ist
	 */
	protected abstract @NotNull T getFromRS(@NotNull ResultSet rs, DBObject... loadedObjects) throws SQLException;
	
	/**
	 * Fülllt ein vorhandenes Objekt mit den Werten aus dem {@link ResultSet}
	 *
	 * @param rs            Das {@link ResultSet} mit den Daten
	 * @param result        das zu füllende Objekt
	 * @param loadedObjects Objekte, die schon geladen wurden und somit nicht neu geladen werden müssen
	 * @throws SQLException Wenn eine {@link SQLException} aufgetreten ist
	 */
	protected abstract void fillObject(@NotNull ResultSet rs, @NotNull T result, DBObject... loadedObjects) throws SQLException;
	
	/**
	 * Gibt den Dtype zurück, welcher bei vererbten Objekten den effektiven Typ angibt
	 *
	 * @return der DType
	 */
	protected @Nullable String getDtype() {
		return null;
	}
	
	/**
	 * Lädt ein Objekt von T an Hand seiner PrimaryID
	 *
	 * @param primary die PrimaryID des Objektes
	 * @return das Objekt, niemals {@code null}
	 * @throws EntryNotFoundException wenn es kein Objekt mit der ID gibt
	 */
	@Override
	public @NotNull T loadFromID(final @NotNull P primary) {
		return loadOneFromCol(null, getPrimaryCol(), primary, "loadFromID");
	}
	
	/**
	 * Lädt eine Liste aller Objekte von T
	 *
	 * @return Eine Liste aller Objekte von T. Niemals {@code null}
	 */
	@Override
	public @NotNull List<T> loadAll() {
		return loadAllFromWhere(null, null, null, null, null, "loadAll");
	}
	
	/**
	 * Erstellt ein {@link PreparedStatement} zum Einfügen eines Objektes oder lädt es aus dem Cache
	 *
	 * @return das {@link PreparedStatement}
	 * @throws SQLException Wenn eine {@link SQLException} aufgetreten ist
	 */
	private @NotNull PreparedStatement insertPst() throws SQLException {
		Connection connection = getConnection();
		synchronized (connection) {
			PreparedStatement result = shouldCloseAlways() ? null : pstCache.get("insert");
			if (result == null) {
				String felder = (getDtype() == null ? "" : "DType, ") + getFelder();
				if (getDatabaseType() == DatabaseType.ORACLE) {
					result = connection.prepareStatement("INSERT INTO " + getTable() + " (" + felder + ") VALUES (" + SQLUtils.getFragezeichenInsert(felder) + ')', new String[] {getPrimaryCol()});
				}
				else {
					result = connection.prepareStatement("INSERT INTO " + getTable() + " (" + felder + ") VALUES (" + SQLUtils.getFragezeichenInsert(felder) + ')', Statement.RETURN_GENERATED_KEYS);
				}
				if (!shouldCloseAlways()) pstCache.put("insert", result);
			}
			return result;
		}
	}
	
	/**
	 * Fügt ein Objekt von T in die Datenbank ein<br>
	 * <b>Das Objekt darf noch keine PrimaryID haben!</b>
	 *
	 * @param v das einzufügende Objekt
	 * @throws IllegalStateException wenn das Objekt eine PrimaryID hat
	 */
	@Override
	public void insertIntoDB(final @NotNull T v) {
		if (v.getPrimary() == null) {
			cascadeInsert(v);
			
			PreparedStatement pst = null;
			try {
				int pos = 1;
				pst = insertPst();
				synchronized (pst) {
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
							afterInsert(v);
						}
						else throw new RuntimeException("rs.next returned false for generated keys");
					}
				}
			}
			catch (final SQLException e) {
				throw new UncheckedSQLException(e);
			}
			finally {
				doCloseAlways(pst);
			}
		}
		else throw new IllegalStateException("Der Eintrag wurde bereits in die Datenbank eingefügt!");
	}
	
	/**
	 * Findet die Klasse des Primary Keys heraus
	 *
	 * @return die Klasse des Primary Keys
	 */
	protected synchronized @NotNull Class<P> getPrimaryClass() {
		if (primaryClass == null) {
			Type type = getClass();
			while (type instanceof Class) {
				type = ((Class) type).getGenericSuperclass();
			}
			primaryClass = (Class<P>) ((ParameterizedType) type).getActualTypeArguments()[1];
			if (primaryClass == null) throw new RuntimeException("Primary class konnte nicht an Hand der Generics ermittelt werden");
		}
		return primaryClass;
	}
	
	/**
	 * Holt den Primary Key aus der Datenbank
	 *
	 * @param rs  das {@link ResultSet} aus dem geladen werden soll
	 * @param pos die Position aus der geladen werden soll
	 * @return der Primary Key
	 * @throws SQLException                Wenn eine {@link SQLException} aufgetreten ist
	 * @throws UnsupportedPrimaryException wenn der Typ des Primary Keys nicht unterstützt wird
	 */
	@Override
	public @Nullable P getPrimary(final @NotNull ResultSet rs, final int pos) throws SQLException {
		Class<P> genericClass = getPrimaryClass();
		if (genericClass.equals(Integer.class)) return (P) SQLUtils.getNullableInt(rs, pos);
		if (genericClass.equals(Long.class)) return (P) SQLUtils.getNullableLong(rs, pos);
		if (genericClass.equals(Byte.class)) return (P) SQLUtils.getNullableByte(rs, pos);
		if (genericClass.equals(Short.class)) return (P) SQLUtils.getNullableShort(rs, pos);
		if (genericClass.equals(Double.class)) return (P) SQLUtils.getNullableDouble(rs, pos);
		if (genericClass.equals(Float.class)) return (P) SQLUtils.getNullableFloat(rs, pos);
		throw new UnsupportedPrimaryException(genericClass.getName());
	}
	
	/**
	 * Holt den Primary Key aus der Datenbank
	 *
	 * @param rs das {@link ResultSet} aus dem geladen werden soll
	 * @return der Primary Key
	 * @throws SQLException                Wenn eine {@link SQLException} aufgetreten ist
	 * @throws UnsupportedPrimaryException wenn der Typ des Primary Keys nicht unterstützt wird
	 */
	private @Nullable P getPrimary(final @NotNull ResultSet rs) throws SQLException {
		return getPrimary(rs, 1);
	}
	
	/**
	 * Erstellt ein {@link PreparedStatement} zum Updaten eines Objektes oder lädt es aus dem Cache
	 *
	 * @return das {@link PreparedStatement}
	 * @throws SQLException Wenn eine {@link SQLException} aufgetreten ist
	 */
	private @NotNull PreparedStatement updatePst() throws SQLException {
		Connection connection = getConnection();
		synchronized (connection) {
			PreparedStatement result = shouldCloseAlways() ? null : pstCache.get("update");
			if (result == null) {
				String felder = (getDtype() == null ? "" : "DType, ") + getFelder();
				result = connection.prepareStatement("UPDATE " + getTable() + " SET " + SQLUtils.getFragezeichenUpdate(felder) + " WHERE " + getPrimaryCol() + "=?");
				if (!shouldCloseAlways()) pstCache.put("update", result);
			}
			return result;
		}
	}
	
	/**
	 * Aktualisiert ein Objekt von T in der Datenbank<br>
	 * <b>Das Objekt muss eine PrimaryID haben um es in der Datenbank zu identifizieren!</b>
	 *
	 * @param v das zu aktualisierende Objekt
	 * @throws IllegalStateException wenn das Objekt keine PrimaryID hat
	 */
	@Override
	public void updateIntoDB(final @NotNull T v) {
		if (v.getPrimary() != null) {
			cascadeUpdate(v);
			
			ParameterList pList = getPList(v);
			pList.addParameter(v.getPrimary());
			
			int pos = 1;
			PreparedStatement pst = null;
			try {
				pst = updatePst();
				synchronized (pst) {
					if (getDtype() != null) {
						pos = new Parameter(getDtype()).setParameter(pst, pos);
					}
					
					pList.setParameter(pst, pos);
					logPst(pst);
					pst.executeUpdate();
				}
				afterUpdate(v);
			}
			catch (final SQLException e) {
				throw new UncheckedSQLException(e);
			}
			finally {
				doCloseAlways(pst);
			}
		}
		else throw new IllegalStateException("Der Eintrag wurde noch nicht in die Datenbank eingefügt!");
	}
	
	/**
	 * Erstellt ein {@link PreparedStatement} zum Löschen eines Objektes oder lädt es aus dem Cache
	 *
	 * @return das {@link PreparedStatement}
	 * @throws SQLException Wenn eine {@link SQLException} aufgetreten ist
	 */
	private @NotNull PreparedStatement deletePst() throws SQLException {
		Connection connection = getConnection();
		synchronized (connection) {
			PreparedStatement result = shouldCloseAlways() ? null : pstCache.get("delete");
			if (result == null) {
				result = connection.prepareStatement("DELETE FROM " + getTable() + " WHERE " + getPrimaryCol() + "=?" + (getDtype() != null ? " AND DType=?" : ""));
				if (!shouldCloseAlways()) pstCache.put("delete", result);
			}
			return result;
		}
	}
	
	/**
	 * Löscht ein Objekt von T aus der Datenbank<br>
	 * <b>Das Objekt muss eine PrimaryID haben um es in der Datenbank zu identifizieren!</b>
	 *
	 * @param v das zu löschende Objekt
	 * @throws IllegalStateException wenn das Objekt keine PrimaryID hat
	 */
	@Override
	public void deleteFromDB(final @NotNull T v) {
		if (v.getPrimary() != null) {
			cascadeDelete(v);
			
			PreparedStatement pst = null;
			try {
				pst = deletePst();
				synchronized (pst) {
					new Parameter(v.getPrimary()).setParameter(pst, 1);
					
					if (getDtype() != null) {
						new Parameter(getDtype()).setParameter(pst, 2);
					}
					logPst(pst);
					pst.executeUpdate();
				}
				v.setPrimary(null);
				
				afterDelete(v);
			}
			catch (final SQLException e) {
				throw new UncheckedSQLException(e);
			}
			finally {
				doCloseAlways(pst);
			}
		}
		else throw new IllegalStateException("Der Eintrag wurde noch nicht in die Datenbank eingefügt!");
	}
	
	/**
	 * Aktualisiert ein Objekt von T in der Datenbank oder fügt es ein, je nachdem ob es eine PrimaryID hat, oder nicht
	 *
	 * @param v das einzufügende oder zu aktualisierende Objekt
	 */
	@Override
	public void insertOrUpdate(final @NotNull T v) {
		if (v.getPrimary() == null) {
			insertIntoDB(v);
		}
		else {
			updateIntoDB(v);
		}
	}
	
	/**
	 * Lädt einen Eintrag aus der Datenbank mit benutzerspezifizierten Bedingungen
	 *
	 * @param join          Die JOIN Klausel oder {@code null}
	 * @param col           Die Spalte für WHERE
	 * @param param         Der Wert für WHERE
	 * @param loadedObjects Objekte, die schon geladen wurden und somit nicht neu geladen werden müssen
	 * @return das gefundene Objekt. Niemals {@code null}
	 * @throws EntryNotFoundException wenn kein Eintrag gefunden wurde
	 */
	protected @NotNull T loadOneFromCol(final @Nullable String join, final @NotNull String col, final @NotNull Object param, final DBObject... loadedObjects) {
		return loadOneFromCol(join, col, param, null, loadedObjects);
	}
	
	/**
	 * Lädt einen Eintrag aus der Datenbank mit benutzerspezifizierten Bedingungen
	 *
	 * @param join          Die JOIN Klausel oder {@code null}
	 * @param col           Die Spalte für WHERE
	 * @param param         Der Wert für WHERE
	 * @param cacheKey      der Key für den pstCache
	 * @param loadedObjects Objekte, die schon geladen wurden und somit nicht neu geladen werden müssen
	 * @return das gefundene Objekt. Niemals {@code null}
	 * @throws EntryNotFoundException wenn kein Eintrag gefunden wurde
	 */
	protected @NotNull T loadOneFromCol(final @Nullable String join, final @NotNull String col, final @NotNull Object param, final @Nullable String cacheKey, final DBObject... loadedObjects) {
		List<T> list = loadAllFromCol(join, col, param, "1", null, cacheKey, loadedObjects);
		if (list.size() < 1) {
			throw new EntryNotFoundException(col, param);
		}
		T result = list.get(0);
		if (result == null) {
			throw new RuntimeException("Result in List was null");
		}
		return result;
	}
	
	/**
	 * Lädt alle möglichen Einträge aus der Datenbank mit benutzerspezifizierten Bedingungen
	 *
	 * @param join          Die JOIN Klausel oder {@code null}
	 * @param col           Die Spalte für WHERE
	 * @param param         Der Wert für WHERE
	 * @param limit         das Limit für die Anzahl der Ergebnisse oder {@code null}
	 * @param order         Die ORDER Klausel oder {@code null}
	 * @param loadedObjects Objekte, die schon geladen wurden und somit nicht neu geladen werden müssen
	 * @return eine Liste aller gefundenen Objekte. Niemals {@code null}
	 */
	protected @NotNull List<T> loadAllFromCol(final @Nullable String join, final @NotNull String col, final @NotNull Object param, final @Nullable String limit, final @Nullable String order, final DBObject... loadedObjects) {
		return loadAllFromCol(join, col, param, limit, order, null, loadedObjects);
	}
	
	/**
	 * Lädt alle möglichen Einträge aus der Datenbank mit benutzerspezifizierten Bedingungen
	 *
	 * @param join          Die JOIN Klausel oder {@code null}
	 * @param col           Die Spalte für WHERE
	 * @param param         Der Wert für WHERE
	 * @param limit         das Limit für die Anzahl der Ergebnisse oder {@code null}
	 * @param order         Die ORDER Klausel oder {@code null}
	 * @param cacheKey      der Key für den pstCache
	 * @param loadedObjects Objekte, die schon geladen wurden und somit nicht neu geladen werden müssen
	 * @return eine Liste aller gefundenen Objekte. Niemals {@code null}
	 */
	protected @NotNull List<T> loadAllFromCol(final @Nullable String join, final @NotNull String col, final @NotNull Object param, final @Nullable String limit, final @Nullable String order, final @Nullable String cacheKey, final DBObject... loadedObjects) {
		return loadAllFromWhere(join, col + "=?", new ParameterList(param), limit, order, cacheKey, loadedObjects);
	}
	
	/**
	 * Lädt alle möglichen Einträge aus der Datenbank mit benutzerspezifizierten Bedingungen
	 *
	 * @param join          Die JOIN Klausel oder {@code null}
	 * @param where         Die WHERE Klausel oder {@code null}
	 * @param params        Die {@link Parameter} oder {@code null}
	 * @param loadedObjects Objekte, die schon geladen wurden und somit nicht neu geladen werden müssen
	 * @return eine Liste aller gefundenen Objekte. Niemals {@code null}
	 */
	protected @NotNull T loadOneFromWhere(final @Nullable String join, final @Nullable String where, final @Nullable ParameterList params, final DBObject... loadedObjects) {
		return loadOneFromWhere(join, where, params, null, loadedObjects);
	}
	
	/**
	 * Lädt alle möglichen Einträge aus der Datenbank mit benutzerspezifizierten Bedingungen
	 *
	 * @param join          Die JOIN Klausel oder {@code null}
	 * @param where         Die WHERE Klausel oder {@code null}
	 * @param params        Die {@link Parameter} oder {@code null}
	 * @param cacheKey      der Key für den pstCache
	 * @param loadedObjects Objekte, die schon geladen wurden und somit nicht neu geladen werden müssen
	 * @return eine Liste aller gefundenen Objekte. Niemals {@code null}
	 */
	protected @NotNull T loadOneFromWhere(final @Nullable String join, final @Nullable String where, final @Nullable ParameterList params, final @Nullable String cacheKey, final DBObject... loadedObjects) {
		List<T> list = loadAllFromWhere(join, where, params, "1", null, cacheKey, loadedObjects);
		if (list.size() < 1) {
			throw new EntryNotFoundException(where, params);
		}
		T result = list.get(0);
		if (result == null) {
			throw new RuntimeException("Result in List was null");
		}
		return result;
	}
	
	/**
	 * Lädt alle möglichen Einträge aus der Datenbank mit benutzerspezifizierten Bedingungen
	 *
	 * @param join          Die JOIN Klausel oder {@code null}
	 * @param where         Die WHERE Klausel oder {@code null}
	 * @param params        Die {@link Parameter} oder {@code null}
	 * @param limit         das Limit für die Anzahl der Ergebnisse oder {@code null}
	 * @param order         Die ORDER Klausel oder {@code null}
	 * @param loadedObjects Objekte, die schon geladen wurden und somit nicht neu geladen werden müssen
	 * @return eine Liste aller gefundenen Objekte. Niemals {@code null}
	 */
	protected @NotNull List<T> loadAllFromWhere(final @Nullable String join, final @Nullable String where, final @Nullable ParameterList params, final @Nullable String limit, final @Nullable String order, final DBObject... loadedObjects) {
		return loadAllFromWhere(join, where, params, limit, order, null, loadedObjects);
	}
	
	/**
	 * Lädt alle möglichen Einträge aus der Datenbank mit benutzerspezifizierten Bedingungen
	 *
	 * @param join          Die JOIN Klausel oder {@code null}
	 * @param where         Die WHERE Klausel oder {@code null}
	 * @param params        Die {@link Parameter} oder {@code null}
	 * @param limit         das Limit für die Anzahl der Ergebnisse oder {@code null}
	 * @param order         Die ORDER Klausel oder {@code null}
	 * @param cacheKey      der Key für den pstCache
	 * @param loadedObjects Objekte, die schon geladen wurden und somit nicht neu geladen werden müssen
	 * @return eine Liste aller gefundenen Objekte. Niemals {@code null}
	 */
	protected @NotNull List<T> loadAllFromWhere(final @Nullable String join, final @Nullable String where, final @Nullable ParameterList params, final @Nullable String limit, final @Nullable String order, final @Nullable String cacheKey, final DBObject... loadedObjects) {
		PreparedStatement pst = null;
		try {
			pst = getPst(getFelderID(), join, where, limit, order, cacheKey, params);
			synchronized (pst) {
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
		}
		catch (final SQLException e) {
			throw new UncheckedSQLException(e);
		}
		finally {
			doCloseAlways(pst);
		}
	}
	
	/**
	 * Logt das {@link PreparedStatement}, führt es aus und gibt das {@link ResultSet} zurück
	 *
	 * @param pst das {@link PreparedStatement}
	 * @return das {@link ResultSet}
	 * @throws SQLException Wenn eine {@link SQLException} aufgetreten ist
	 */
	private @NotNull ResultSet getResultSet(final @NotNull PreparedStatement pst) throws SQLException {
		logPst(pst);
		return pst.executeQuery();
	}
	
	/**
	 * Lädt die Anzahl aller möglichen Einträge aus der Datenbank mit benutzerspezifizierten Bedingungen
	 *
	 * @param join  Die JOIN Klausel oder {@code null}
	 * @param col   Die Spalte für WHERE
	 * @param param Der Wert für WHERE
	 * @return eine Liste aller gefundenen Objekte. Niemals {@code null}
	 */
	protected long loadCountFromCol(final @Nullable String join, final @NotNull String col, final @NotNull Object param) {
		return loadCountFromCol(join, col, param, null);
	}
	
	/**
	 * Lädt die Anzahl aller möglichen Einträge aus der Datenbank mit benutzerspezifizierten Bedingungen
	 *
	 * @param join     Die JOIN Klausel oder {@code null}
	 * @param col      Die Spalte für WHERE
	 * @param param    Der Wert für WHERE
	 * @param cacheKey der Key für den pstCache
	 * @return eine Liste aller gefundenen Objekte. Niemals {@code null}
	 */
	protected long loadCountFromCol(final @Nullable String join, final @NotNull String col, final @NotNull Object param, final @Nullable String cacheKey) {
		return loadCountFromWhere(join, col + "=?", new ParameterList(param), cacheKey);
	}
	
	/**
	 * Lädt die Anzahl aller möglichen Einträge aus der Datenbank mit benutzerspezifizierten Bedingungen
	 *
	 * @param join   Die JOIN Klausel oder {@code null}
	 * @param where  Die WHERE Klausel oder {@code null}
	 * @param params Die Parameter
	 * @return die Anzahl der Parameter
	 */
	protected long loadCountFromWhere(final @Nullable String join, final @Nullable String where, final @Nullable ParameterList params) {
		return loadCountFromWhere(join, where, params, null);
	}
	
	/**
	 * Lädt die Anzahl aller möglichen Einträge aus der Datenbank mit benutzerspezifizierten Bedingungen
	 *
	 * @param join     Die JOIN Klausel oder {@code null}
	 * @param where    Die WHERE Klausel oder {@code null}
	 * @param params   Die {@link Parameter} oder {@code null}
	 * @param cacheKey der Key für den pstCache
	 * @return die Anzahl der Parameter
	 */
	protected long loadCountFromWhere(final @Nullable String join, final @Nullable String where, final @Nullable ParameterList params, final @Nullable String cacheKey) {
		PreparedStatement pst = null;
		try {
			pst = getPst("count(*)", join, where, null, null, cacheKey, params);
			synchronized (pst) {
				setParameter(params, pst);
				
				try (ResultSet rs = getResultSet(pst)) {
					if (rs.next()) {
						return rs.getLong(1);
					}
					throw new RuntimeException("rs.next() bei SELECT count(*) ist false");
				}
			}
		}
		catch (final SQLException e) {
			throw new UncheckedSQLException(e);
		}
		finally {
			doCloseAlways(pst);
		}
	}
	
	/**
	 * Lädt einen Liste mit einem einzigen Wert als String
	 *
	 * @param feld   das Feld aus dem der Wert geladen werden soll
	 * @param join   Die JOIN Klausel oder {@code null}
	 * @param where  Die WHERE Klausel oder {@code null}
	 * @param params Die {@link Parameter} oder {@code null}
	 * @param limit  das Limit für die Anzahl der Ergebnisse oder {@code null}
	 * @param order  Die ORDER Klausel oder {@code null}
	 * @return die Liste mit Strings
	 */
	protected @NotNull List<String> loadSingleValuesAsString(final @Nullable String feld, final @Nullable String join, final @Nullable String where, final @Nullable ParameterList params, final @Nullable String limit, final @Nullable String order) {
		return loadSingleValuesAsString(feld, join, where, params, limit, order, null);
	}
	
	/**
	 * Lädt einen Liste mit einem einzigen Wert als String
	 *
	 * @param feld     das Feld aus dem der Wert geladen werden soll
	 * @param join     Die JOIN Klausel oder {@code null}
	 * @param where    Die WHERE Klausel oder {@code null}
	 * @param params   Die {@link Parameter} oder {@code null}
	 * @param limit    das Limit für die Anzahl der Ergebnisse oder {@code null}
	 * @param order    Die ORDER Klausel oder {@code null}
	 * @param cacheKey der Key für den pstCache
	 * @return die Liste mit Strings
	 */
	protected @NotNull List<String> loadSingleValuesAsString(final @Nullable String feld, final @Nullable String join, final @Nullable String where, final @Nullable ParameterList params, final @Nullable String limit, final @Nullable String order, final @Nullable String cacheKey) {
		PreparedStatement pst = null;
		try {
			pst = getPst("DISTINCT " + feld, join, where, limit, order, cacheKey, params);
			synchronized (pst) {
				setParameter(params, pst);
				
				try (ResultSet rs = getResultSet(pst)) {
					List<String> result = new ArrayList<>();
					while (rs.next()) {
						result.add(rs.getString(1));
					}
					return result;
				}
			}
		}
		catch (final SQLException e) {
			throw new UncheckedSQLException(e);
		}
		finally {
			doCloseAlways(pst);
		}
	}
	
	/**
	 * Findet den Typ der Datenbank heraus
	 *
	 * @return der Datenbanktyp
	 */
	protected @NotNull DatabaseType getDatabaseType() {
		Connection connection = getConnection();
		synchronized (connection) {
			return getDatabaseType(connection);
		}
	}
	
	/**
	 * Findet den Typ der Datenbank heraus
	 *
	 * @param connection die Datenbankverbindung, von der der Datenbanktyp herausgefunden werden soll
	 * @return der Datenbanktyp
	 */
	protected static @NotNull DatabaseType getDatabaseType(final @NotNull Connection connection) {
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
	 * Setzt die {@link ParameterList} in das {@link PreparedStatement} ein
	 *
	 * @param params die {@link ParameterList}
	 * @param pst    das {@link PreparedStatement}
	 * @throws SQLException Wenn eine {@link SQLException} aufgetreten ist
	 */
	private void setParameter(final @Nullable ParameterList params, final @NotNull PreparedStatement pst) throws SQLException {
		if (params == null) return;
		int pos = params.setParameter(pst, 1);
		
		if (getDtype() != null) {
			new Parameter(getDtype()).setParameter(pst, pos);
		}
	}
	
	/**
	 * Baut aus diversen Parametern ein {@link PreparedStatement} zusammen
	 *
	 * @param connection        die Datenbankverbindung mit der das {@link PreparedStatement} erstellt werden soll
	 * @param pstCache          der {@link PreparedStatement} Cache
	 * @param table             die Tabelle für die FROM Klausel
	 * @param dType             der DType
	 * @param select            die Felder für die SELECT Klausel
	 * @param join              Die JOIN Klausel oder {@code null}
	 * @param where             Die WHERE Klausel oder {@code null}
	 * @param limit             das Limit für die Anzahl der Ergebnisse oder {@code null}
	 * @param order             Die ORDER Klausel oder {@code null}
	 * @param cacheKey          der Key für den pstCache
	 * @param shouldCloseAlways die Angabe, ob nach jedem Statement die Datenbankverbindung geschlossen wird
	 * @param params            die Parameter, die in das {@link PreparedStatement} eingefügt werden
	 * @return das zusammengebaute {@link PreparedStatement}
	 * @throws SQLException Wenn eine {@link SQLException} aufgetreten ist
	 */
	static @NotNull PreparedStatement getPst(final @NotNull Connection connection, final @NotNull Map<String, PreparedStatement> pstCache, final @NotNull String table, final @Nullable String dType, final @NotNull String select, final @Nullable String join, final @Nullable String where, final @Nullable String limit, final @Nullable String order, final @Nullable String cacheKey, final boolean shouldCloseAlways, final @Nullable ParameterList params) throws SQLException {
		PreparedStatement result = shouldCloseAlways || cacheKey == null ? null : pstCache.get(cacheKey);
		if (result == null || result.isClosed()) {
			result = connection.prepareStatement("SELECT " + select + " FROM " + table + (StringUtils.isBlank(join) ? "" : " JOIN " + join) + (StringUtils
					.isBlank(where) && dType == null ? "" : " WHERE " + (StringUtils
					.isBlank(where) ? "" : SQLUtils.nullableWhere(where, params)))
					+ (dType != null ? " AND DType=?" : "") + (StringUtils.isBlank(order) ? "" : " ORDER BY " + order)
					+ (StringUtils.isBlank(limit) || getDatabaseType(connection) == DatabaseType.ORACLE ? "" : " LIMIT " + limit));
			if (cacheKey != null && !shouldCloseAlways) pstCache.put(cacheKey, result);
		}
		return result;
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
	private @NotNull PreparedStatement getPst(final @NotNull String select, final @Nullable String join, final @Nullable String where, final @Nullable String limit, final @Nullable String order, final @Nullable String cacheKey, final @Nullable ParameterList params) throws SQLException {
		Connection connection = getConnection();
		synchronized (connection) {
			return getPst(connection, pstCache, getTable(), getDtype(), select, join, where, limit, order, cacheKey, shouldCloseAlways(), params);
		}
	}
	
	/**
	 * Logt ein {@link PreparedStatement}
	 *
	 * @param pst das {@link PreparedStatement}
	 */
	private void logPst(final @NotNull PreparedStatement pst) {
		log.debug(SQLUtils.pstToSQL(pst));
	}
	
	/**
	 * Schließt das {@link PreparedStatement} und die Datenbankverbindung, wenn gewünscht
	 *
	 * @param pst das {@link PreparedStatement}
	 */
	private void doCloseAlways(final @Nullable PreparedStatement pst) {
		if (shouldCloseAlways()) {
			close();
			closeSqlAutocloseable(pst);
		}
	}
	
	/**
	 * Schließt das {@link PreparedStatement} und die Datenbankverbindung, wenn gewünscht
	 *
	 * @param pst               das {@link PreparedStatement}
	 * @param shouldCloseAlways die Angabe, ob nach jedem Statement die Datenbankverbindung geschlossen wird
	 * @param connection        die Datenbankverbindung
	 * @param dataSource        die {@link DataSource} oder {@code null}
	 * @param log               der {@link Logger}
	 * @param pstCache          der {@link PreparedStatement} Cache
	 */
	static void doCloseAlways(final @Nullable PreparedStatement pst, final boolean shouldCloseAlways, final @Nullable Connection connection, final @Nullable DataSource dataSource, final @Nullable Logger log, final @Nullable Map<String, PreparedStatement> pstCache) {
		if (shouldCloseAlways) {
			close(connection, dataSource, log, pstCache);
			SQLUtils.closeSqlAutocloseable(pst, log);
		}
	}
	
	/**
	 * Schließt die Datenbankverbindung und alle {@link PreparedStatement} im Cache
	 */
	@Override
	public void close() {
		close(connection, dataSource, log, pstCache);
	}
	
	/**
	 * Schließt die Datenbankverbindung und alle {@link PreparedStatement} im Cache
	 *
	 * @param connection die Datenbankverbindung
	 * @param dataSource die {@link DataSource} oder {@code null}
	 * @param log        der {@link Logger}
	 * @param pstCache   der {@link PreparedStatement} Cache
	 */
	static void close(final @Nullable Connection connection, final @Nullable DataSource dataSource, final @Nullable Logger log, final @Nullable Map<String, PreparedStatement> pstCache) {
		if (log != null) log.debug("Closing DAO...");
		if (pstCache != null) {
			for (final PreparedStatement pst : pstCache.values()) {
				SQLUtils.closeSqlAutocloseable(pst, log);
			}
			pstCache.clear();
		}
		if (dataSource != null) {
			SQLUtils.closeSqlAutocloseable(connection, log);
		}
	}
	
	/**
	 * Schließt {@link AutoCloseable} mit {@code null} Check und Fehlerabfangung. Besondere Fehlerbeschreibung bei SQL Fehlern
	 *
	 * @param closeable das {@link AutoCloseable}
	 */
	protected void closeSqlAutocloseable(final @Nullable AutoCloseable closeable) {
		SQLUtils.closeSqlAutocloseable(closeable, log);
	}
	
}