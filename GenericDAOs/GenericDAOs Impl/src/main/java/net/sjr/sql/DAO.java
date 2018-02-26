package net.sjr.sql;

import net.sjr.sql.exceptions.EntryNotFoundException;
import net.sjr.sql.exceptions.UncheckedSQLException;
import net.sjr.sql.exceptions.UnsupportedPrimaryException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.sql.DataSource;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO Oberklasse um Datenbankzugriffe zu vereinfachen
 *
 * @param <T> Typ des zu speichernden Java Objektes
 * @param <P> Typ des Primary Keys
 */
@SuppressWarnings({"WeakerAccess", "JavaDoc", "SqlDialectInspection", "SqlNoDataSourceInspection", "unchecked", "unused", "SameParameterValue", "SqlResolve", "UnusedReturnValue", "SameReturnValue", "SynchronizationOnLocalVariableOrMethodParameter"})
public abstract class DAO<T extends DBObject<P>, P extends Number> extends DAOBase<DAOConnectionPool, DAOConnection> implements DAOBaseInterface<T, P> {
	private Class<P> primaryClass = null;
	
	/**
	 * Erstellt die {@link DAO} mit einer {@link DataSource}
	 *
	 * @param ds die {@link DataSource}
	 */
	public DAO(final @NotNull DataSource ds) {
		super(ds);
	}
	
	/**
	 * Erstellt die {@link DAO} mit einer bereits vorhandenen Datenbankverbindung
	 *
	 * @param con die bereits vorhandene Datenbankverbindung
	 */
	public DAO(final @NotNull Connection con) {
		super(con);
	}
	
	/**
	 * Erstellt die {@link DAO} mit einem bereits vorhandenen {@link DAOBase}
	 *
	 * @param dao die bereits vorhandene {@link DAOBase}
	 */
	public DAO(final @NotNull DAOBase<?, ?> dao) {
		super(dao);
	}
	
	@Override
	protected DAOConnectionPool createConnectionPool() {
		return new DAOConnectionPool(this);
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
	 * @return Der name der Primary Zeile
	 */
	protected abstract @NotNull String getPrimaryCol();
	
	/**
	 * @param v Das Objekt von dem die {@link ParameterList} erstellt werden soll
	 * @return eine {@link ParameterList} aller {@link Parameter} Spalten für die DB
	 */
	protected abstract @NotNull ParameterList getPList(@NotNull T v);
	
	/**
	 * Wird aufgerufen vor einem Insert um die Möglichkeit zu bieten abhängige Objekte auch einzufügen
	 *
	 * @param v            das einzufügende Objekt
	 * @param cascadeInfos optionale zusätzliche Parameter, die zwischen den verbundenen Methoden weiter gegeben werden können
	 */
	protected void cascadeInsert(final @NotNull T v, final Object... cascadeInfos) {
	}
	
	/**
	 * Wird aufgerufen vor einem Update um die Möglichkeit zu bieten abhängige Objekte auch zu updaten
	 *
	 * @param v            das upzudatende Objekt
	 * @param cascadeInfos optionale zusätzliche Parameter, die zwischen den verbundenen Methoden weiter gegeben werden können
	 */
	protected void cascadeUpdate(final @NotNull T v, final Object... cascadeInfos) {
	}
	
	/**
	 * Wird aufgerufen vor einem Delete um die Möglichkeit zu bieten abhängige Objekte auch zu löschen
	 *
	 * @param v            das zu löschende Objekt
	 * @param cascadeInfos optionale zusätzliche Parameter, die zwischen den verbundenen Methoden weiter gegeben werden können
	 */
	protected void cascadeDelete(final @NotNull T v, final Object... cascadeInfos) {
	}
	
	/**
	 * Wird aufgerufen nach einem Insert um die Möglichkeit zu bieten abhängige Listen auch einzufügen
	 *
	 * @param v            das eingefügte Objekt
	 * @param cascadeInfos optionale zusätzliche Parameter, die zwischen den verbundenen Methoden weiter gegeben werden können
	 */
	protected void afterInsert(final @NotNull T v, final Object... cascadeInfos) {
	}
	
	/**
	 * Wird aufgerufen nach einem Update um die Möglichkeit zu bieten abhängige Listen auch zu updaten
	 *
	 * @param v            das upgedatete Objekt
	 * @param cascadeInfos optionale zusätzliche Parameter, die zwischen den verbundenen Methoden weiter gegeben werden können
	 */
	protected void afterUpdate(final @NotNull T v, final Object... cascadeInfos) {
	}
	
	/**
	 * Wird aufgerufen nach einem Delete um die Möglichkeit zu bieten abhängige Listen auch zu löschen
	 *
	 * @param v            das gelöschte Objekt
	 * @param cascadeInfos optionale zusätzliche Parameter, die zwischen den verbundenen Methoden weiter gegeben werden können
	 */
	protected void afterDelete(final @NotNull T v, final Object... cascadeInfos) {
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
	 * Fügt ein Objekt von T in die Datenbank ein<br>
	 * <b>Das Objekt darf noch keine PrimaryID haben!</b>
	 *
	 * @param v das einzufügende Objekt
	 * @throws IllegalStateException wenn das Objekt eine PrimaryID hat
	 */
	@Override
	public void insertIntoDB(final @NotNull T v) {
		insertIntoDB(v, new Object[0]);
	}
	
	/**
	 * Fügt ein Objekt von T in die Datenbank ein<br>
	 * <b>Das Objekt darf noch keine PrimaryID haben!</b>
	 *
	 * @param v            das einzufügende Objekt
	 * @param cascadeInfos optionale zusätzliche Parameter, die an die cascade Methoden weiter gegeben werden
	 * @throws IllegalStateException wenn das Objekt eine PrimaryID hat
	 */
	protected void insertIntoDB(final @NotNull T v, final Object... cascadeInfos) {
		if (v.getPrimary() == null) {
			cascadeInsert(v, cascadeInfos);
			
			int pos = 1;
			DAOConnection con = null;
			PreparedStatement pst = null;
			try {
				con = connectionPool.borrowObject();
				pst = con.insertPst();
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
					}
					else throw new RuntimeException("rs.next returned false for generated keys");
				}
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
				doCloseAlways(con, pst);
			}
			afterInsert(v, cascadeInfos);
		}
		else throw new IllegalStateException("Der Eintrag wurde bereits in die Datenbank eingefügt!");
	}
	
	/**
	 * Findet die Klasse des Primary Keys heraus
	 *
	 * @return die Klasse des Primary Keys
	 */
	protected @NotNull Class<P> getPrimaryClass() {
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
	 * Aktualisiert ein Objekt von T in der Datenbank<br>
	 * <b>Das Objekt muss eine PrimaryID haben um es in der Datenbank zu identifizieren!</b>
	 *
	 * @param v das zu aktualisierende Objekt
	 * @throws IllegalStateException wenn das Objekt keine PrimaryID hat
	 */
	@Override
	public void updateIntoDB(final @NotNull T v) {
		updateIntoDB(v, new Object[0]);
	}
	
	/**
	 * Aktualisiert ein Objekt von T in der Datenbank<br>
	 * <b>Das Objekt muss eine PrimaryID haben um es in der Datenbank zu identifizieren!</b>
	 *
	 * @param v            das zu aktualisierende Objekt
	 * @param cascadeInfos optionale zusätzliche Parameter, die an die cascade Methoden weiter gegeben werden
	 * @throws IllegalStateException wenn das Objekt keine PrimaryID hat
	 */
	protected void updateIntoDB(final @NotNull T v, final Object... cascadeInfos) {
		if (v.getPrimary() != null) {
			cascadeUpdate(v, cascadeInfos);
			
			ParameterList pList = getPList(v);
			pList.addParameter(v.getPrimary());
			
			int pos = 1;
			DAOConnection con = null;
			PreparedStatement pst = null;
			try {
				con = connectionPool.borrowObject();
				pst = con.updatePst();
				if (getDtype() != null) {
					pos = new Parameter(getDtype()).setParameter(pst, pos);
				}
				
				pList.setParameter(pst, pos);
				logPst(pst);
				pst.executeUpdate();
				
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
				doCloseAlways(con, pst);
			}
			afterUpdate(v, cascadeInfos);
		}
		else throw new IllegalStateException("Der Eintrag wurde noch nicht in die Datenbank eingefügt!");
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
		deleteFromDB(v, new Object[0]);
	}
	
	/**
	 * Löscht ein Objekt von T aus der Datenbank<br>
	 * <b>Das Objekt muss eine PrimaryID haben um es in der Datenbank zu identifizieren!</b>
	 *
	 * @param v            das zu löschende Objekt
	 * @param cascadeInfos optionale zusätzliche Parameter, die an die cascade Methoden weiter gegeben werden
	 * @throws IllegalStateException wenn das Objekt keine PrimaryID hat
	 */
	protected void deleteFromDB(final @NotNull T v, final Object... cascadeInfos) {
		if (v.getPrimary() != null) {
			cascadeDelete(v, cascadeInfos);
			
			DAOConnection con = null;
			PreparedStatement pst = null;
			try {
				con = connectionPool.borrowObject();
				pst = con.deletePst();
				new Parameter(v.getPrimary()).setParameter(pst, 1);
				
				if (getDtype() != null) {
					new Parameter(getDtype()).setParameter(pst, 2);
				}
				logPst(pst);
				pst.executeUpdate();
				
				v.setPrimary(null);
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
				doCloseAlways(con, pst);
			}
			afterDelete(v, cascadeInfos);
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
		insertOrUpdate(v, new Object[0]);
	}
	
	/**
	 * Aktualisiert ein Objekt von T in der Datenbank oder fügt es ein, je nachdem ob es eine PrimaryID hat, oder nicht
	 *
	 * @param v            das einzufügende oder zu aktualisierende Objekt
	 * @param cascadeInfos optionale zusätzliche Parameter, die an die cascade Methoden weiter gegeben werden
	 */
	protected void insertOrUpdate(final @NotNull T v, final Object... cascadeInfos) {
		if (v.getPrimary() == null) {
			insertIntoDB(v, cascadeInfos);
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
		DAOConnection con = null;
		PreparedStatement pst = null;
		try {
			con = connectionPool.borrowObject();
			pst = con.getPst(getFelderID(), join, where, limit, order, cacheKey, params);
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
			doCloseAlways(con, pst);
		}
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
		DAOConnection con = null;
		PreparedStatement pst = null;
		try {
			con = connectionPool.borrowObject();
			pst = con.getPst("count(*)", join, where, null, null, cacheKey, params);
			setParameter(params, pst);
			
			try (ResultSet rs = getResultSet(pst)) {
				if (rs.next()) {
					return rs.getLong(1);
				}
				throw new RuntimeException("rs.next() bei SELECT count(*) ist false");
			}
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
			doCloseAlways(con, pst);
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
		DAOConnection con = null;
		PreparedStatement pst = null;
		try {
			con = connectionPool.borrowObject();
			pst = con.getPst("DISTINCT " + feld, join, where, limit, order, cacheKey, params);
			setParameter(params, pst);
			
			try (ResultSet rs = getResultSet(pst)) {
				List<String> result = new ArrayList<>();
				while (rs.next()) {
					result.add(rs.getString(1));
				}
				return result;
			}
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
			doCloseAlways(con, pst);
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
	
}