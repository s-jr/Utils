package net.sjr.sql;

import net.sjr.sql.exceptions.UncheckedSQLException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * Basisklasse einer DAO um Daten aus Kreuztabellen für mindestens 2 via n:m Verbindung verbundene Tabellen laden zu können. Kann zu beliebig vielen Verbundenen Objekten erweitert werden
 *
 * @param <A>  Typ des ersten Java Objektes
 * @param <PA> Typ des Primary Keys des ersten Java Objektes
 * @param <B>  Typ des zweiten Java Objektes
 * @param <PB> Typ des Primary Keys des zweiten Java Objektes
 * @param <KO> Typ des KreuzObjektes mit allen verbundenen Objekten
 */
@SuppressWarnings({"WeakerAccess", "unused", "SameReturnValue", "SqlDialectInspection"})
public abstract class KreuzDAOBase<A extends DBObject<PA>, PA extends Number, B extends DBObject<PB>, PB extends Number, KO extends Kreuz2Objekt<A, PA, B, PB>> extends DAOBase<KreuzDAOConnectionPool, KreuzDAOConnection> {
	
	/**
	 * Erstellt die {@link KreuzDAOBase} mit einer {@link DataSource}
	 *
	 * @param ds die {@link DataSource}
	 */
	public KreuzDAOBase(final @NotNull DataSource ds) {
		super(ds);
	}
	
	/**
	 * Erstellt die {@link KreuzDAOBase} mit einer bereits vorhandenen Datenbankverbindung
	 *
	 * @param con die bereits vorhandene Datenbankverbindung
	 */
	public KreuzDAOBase(final @NotNull Connection con) {
		super(con);
	}
	
	/**
	 * Erstellt die {@link KreuzDAOBase} mit einem bereits vorhandenen {@link DAOBase}
	 *
	 * @param dao die bereits vorhandene {@link DAOBase}
	 */
	public KreuzDAOBase(final @NotNull DAOBase<?, ?> dao) {
		super(dao);
	}
	
	@Override
	protected KreuzDAOConnectionPool createConnectionPool() {
		return new KreuzDAOConnectionPool(this);
	}
	
	/**
	 * Gibt die {@link DAO} des ersten Objekts zurück
	 *
	 * @return die {@link DAO} des ersten Objekts
	 */
	protected abstract @NotNull DAO<A, PA> getaDAO();
	
	/**
	 * Gibt die {@link DAO} des zweiten Objekts zurück
	 *
	 * @return die {@link DAO} des zweiten Objekts
	 */
	protected abstract @NotNull DAO<B, PB> getbDAO();
	
	/**
	 * Gibt den Namen der A Spalte der Kreuztabelle zurück
	 *
	 * @return der Name der A Spalte Kreuztabelle
	 */
	protected abstract @NotNull String getKreuzColA();
	
	/**
	 * Gibt den Namen der B Spalte derKreuztabelle zurück
	 *
	 * @return der Name der B Spalte der Kreuztabelle
	 */
	protected abstract @NotNull String getKreuzColB();
	
	/**
	 * Gibt die Namen aller Spalten der Kreuztabelle zurück
	 *
	 * @return die Name aller Spalten der Kreuztabelle
	 */
	protected abstract @NotNull String getAllKreuzCols();
	
	/**
	 * Erstellt aus einem {@link ResultSet} mindestens ein {@link Kreuz2Objekt} mit allen Verbundenen Objekten
	 *
	 * @param rs            das {@link ResultSet}
	 * @param loadedObjects die bereits geladenen Objekte
	 *
	 * @return das {@link Kreuz2Objekt}
	 *
	 * @throws SQLException Wenn eine {@link SQLException} aufgetreten ist
	 */
	protected abstract @NotNull KO getKreuzObjekt(@NotNull ResultSet rs, DBObject... loadedObjects) throws SQLException;
	
	/**
	 * gibt den Spaltentyp aus der {@link java.sql.Types} Klasse der A Objetke in der Kreuztabelle zurück
	 *
	 * @return der Spaltentyp
	 */
	protected @Nullable Integer getTypeA() {
		return null;
	}
	
	/**
	 * gibt den Spaltentyp aus der {@link java.sql.Types} Klasse der B Objetke in der Kreuztabelle zurück
	 *
	 * @return der Spaltentyp
	 */
	protected @Nullable Integer getTypeB() {
		return null;
	}
	
	/**
	 * Lädt eine Liste aller Objekte a, die mit b über die Kreuztabelle verbunden sind
	 *
	 * @param b             das Objekt mit dem Verbunden sein muss
	 * @param loadedObjects Objekte, die schon geladen wurden und somit nicht neu geladen werden müssen
	 *
	 * @return die Liste aller verbundenen Objekte
	 */
	public @NotNull List<A> loadAfromB(final @Nullable B b, final DBObject... loadedObjects) {
		return executeFrom1(b, getaDAO(), getKreuzColA(), getKreuzColB(), getTypeB(), loadedObjects);
	}
	
	/**
	 * Lädt eine Liste aller Objekte b, die mit a über die Kreuztabelle verbunden sind
	 *
	 * @param a             das Objekt mit dem Verbunden sein muss
	 * @param loadedObjects Objekte, die schon geladen wurden und somit nicht neu geladen werden müssen
	 *
	 * @return die Liste aller verbundenen Objekte
	 */
	public @NotNull List<B> loadBfromA(final @Nullable A a, final DBObject... loadedObjects) {
		return executeFrom1(a, getbDAO(), getKreuzColB(), getKreuzColA(), getTypeA(), loadedObjects);
	}
	
	/**
	 * Lädt eine Liste von Objekten an Hand der anderen Spalte der Kreuztabelle
	 *
	 * @param a              Objekt, nach dem gesucht werden soll
	 * @param dao            {@link DAO} des Zielobjektes
	 * @param resultKreuzCol Spalte des Zielobjektes
	 * @param aKreuzCol      Spalte des Suchobjektes
	 * @param typeA          Typ des Suchobjektes
	 * @param loadedObjects  Objekte, die schon geladen wurden und somit nicht neu geladen werden müssen
	 * @param <T>            Typ des Zielobjektes
	 * @param <P>            Typ des Primary Key des Zielobjektes
	 *
	 * @return eine Liste aller gefundenen Zielobjekte. Niemals {@code {@code null}}
	 */
	protected @NotNull <T extends DBObject<P>, P extends Number> List<T> executeFrom1(final @Nullable DBObject a, final @NotNull DAO<T, P> dao, final @NotNull String resultKreuzCol, final @NotNull String aKreuzCol, final @Nullable Integer typeA, final DBObject... loadedObjects) {
		return dao.loadAllFromCol(getTable() + " ON " + getTable() + '.' + resultKreuzCol + '=' + dao.getTable() + '.' + dao.getPrimaryCol(),
				getTable() + '.' + aKreuzCol, new Parameter(a, typeA),
				null, null, getTable() + ".load" + resultKreuzCol + "from" + aKreuzCol, loadedObjects);
	}

	
	/**
	 * Erstellt eine neue Kreuzverbindung zwischen Objekten
	 *
	 * @param params die zu verbindende Objekte
	 */
	protected void createKreuzInDB(final Parameter... params) {
		KreuzDAOConnection con = null;
		PreparedStatement pst = null;
		try {
			con = connectionPool.borrowObject();
			pst = con.createKreuzPst();
			
			new ParameterList((Object[]) params).setParameter(pst, 1);
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
	}
	
	/**
	 * Löscht eine Kreuzverbindung zwischen Objekten aus der Datenbank<br>
	 *
	 * @param params die verbundene Objekte
	 */
	protected void deleteKreuzFromDB(final Parameter... params) {
		KreuzDAOConnection con = null;
		PreparedStatement pst = null;
		try {
			con = connectionPool.borrowObject();
			pst = con.deleteKreuzPst();
			new ParameterList((Object[]) params).setParameter(pst, 1);
			
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
	}
	
	/**
	 * Lädt alle möglichen Kreuzobjekte aus der Datenbank
	 *
	 * @return eine Liste aller gefundenen Kreuzobjekten. Niemals {@code null}
	 */
	public @NotNull List<KO> loadAllKreuze() {
		return loadKreuzeFromWhere(null, null, null, null, null, "loadAllKreuze");
	}
	
	/**
	 * Lädt alle möglichen Kreuzobjekte aus der Datenbank von einer Spalte
	 *
	 * @param join          Die JOIN Klausel oder {@code null}
	 * @param col           Die Spalte für WHERE
	 * @param param         Der Wert für WHERE
	 * @param limit         das Limit für die Anzahl der Ergebnisse oder {@code null}
	 * @param order         Die ORDER Klausel oder {@code null}
	 * @param loadedObjects Objekte, die schon geladen wurden und somit nicht neu geladen werden müssen
	 *
	 * @return eine Liste aller gefundenen Kreuzobjekten. Niemals {@code null}
	 */
	protected @NotNull List<KO> loadKreuzeFromCol(final @Nullable String join, final @NotNull String col, final @NotNull Object param, final @Nullable String limit, final @Nullable String order, final DBObject... loadedObjects) {
		return loadKreuzeFromCol(join, col, param, limit, order, null, loadedObjects);
	}
	
	/**
	 * Lädt alle möglichen Kreuzobjekte aus der Datenbank von einer Spalte
	 *
	 * @param join          Die JOIN Klausel oder {@code null}
	 * @param col           Die Spalte für WHERE
	 * @param param         Der Wert für WHERE
	 * @param limit         das Limit für die Anzahl der Ergebnisse oder {@code null}
	 * @param order         Die ORDER Klausel oder {@code null}
	 * @param cacheKey      der Key für den pstCache
	 * @param loadedObjects Objekte, die schon geladen wurden und somit nicht neu geladen werden müssen
	 *
	 * @return eine Liste aller gefundenen Kreuzobjekten. Niemals {@code null}
	 */
	protected @NotNull List<KO> loadKreuzeFromCol(final @Nullable String join, final @NotNull String col, final @NotNull Object param, final @Nullable String limit, final @Nullable String order, final @Nullable String cacheKey, final DBObject... loadedObjects) {
		return loadKreuzeFromWhere(join, col + "=?", new ParameterList(param), limit, order, cacheKey, loadedObjects);
	}
	
	/**
	 * Lädt alle möglichen Kreuzobjekte aus der Datenbank mit benutzerspezifizierten Bedingungen
	 *
	 * @param join          Die JOIN Klausel oder {@code null}
	 * @param where         Die WHERE Klausel oder {@code null}
	 * @param params        Die Parameter oder {@code null}
	 * @param limit         das Limit für die Anzahl der Ergebnisse oder {@code null}
	 * @param order         Die ORDER Klausel oder {@code null}
	 * @param loadedObjects Objekte, die schon geladen wurden und somit nicht neu geladen werden müssen
	 *
	 * @return eine Liste aller gefundenen Kreuzobjekten. Niemals {@code null}
	 */
	protected @NotNull List<KO> loadKreuzeFromWhere(final @Nullable String join, final @Nullable String where, final @Nullable ParameterList params, final @Nullable String limit, final @Nullable String order, final DBObject... loadedObjects) {
		return loadKreuzeFromWhere(join, where, params, limit, order, null, loadedObjects);
	}
	
	/**
	 * Lädt alle möglichen Kreuzobjekte aus der Datenbank mit benutzerspezifizierten Bedingungen
	 *
	 * @param join          Die JOIN Klausel oder {@code null}
	 * @param where         Die WHERE Klausel oder {@code null}
	 * @param params        Die Parameter oder {@code null}
	 * @param limit         das Limit für die Anzahl der Ergebnisse oder {@code null}
	 * @param order         Die ORDER Klausel oder {@code null}
	 * @param cacheKey      der Key für den pstCache
	 * @param loadedObjects Objekte, die schon geladen wurden und somit nicht neu geladen werden müssen
	 *
	 * @return eine Liste aller gefundenen Kreuzobjekten. Niemals {@code null}
	 */
	protected @NotNull List<KO> loadKreuzeFromWhere(final @Nullable String join, final @Nullable String where, final @Nullable ParameterList params, final @Nullable String limit, final @Nullable String order, final @Nullable String cacheKey, final DBObject... loadedObjects) {
		KreuzDAOConnection con = null;
		PreparedStatement pst = null;
		try {
			con = connectionPool.borrowObject();
			pst = con.getPst(getAllKreuzCols(), join, where, limit, order, cacheKey, params);
			
			if (params != null) params.setParameter(pst, 1);
			
			logPst(pst);
			try (ResultSet rs = pst.executeQuery()) {
				List<KO> result = new LinkedList<>();
				while (rs.next()) {
					KO ko = getKreuzObjekt(rs, loadedObjects);
					result.add(ko);
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
	 * Lädt die Anzahl aller möglichen Kreuzobjekte aus der Datenbank
	 *
	 * @return die Anzahl aller Kreuze
	 */
	public long loadAllCount() {
		return loadCountFromWhere(null, null, null, "loadAllCount");
	}
	
	/**
	 * Lädt die Anzahl aller möglichen Kreuzobjekte mit A aus der Datenbank
	 *
	 * @param a A zu welcher die Anzahl gesucht wird
	 *
	 * @return die Anzahl aller Kreuze
	 */
	public long loadAllCountFromA(final @Nullable A a) {
		return loadCountFromCol(null, getKreuzColA(), new Parameter(a, getTypeA()), "loadAllCountFromA");
	}
	
	/**
	 * Lädt die Anzahl aller möglichen Kreuzobjekte mit B aus der Datenbank
	 *
	 * @param b B zu welcher die Anzahl gesucht wird
	 *
	 * @return die Anzahl aller Kreuze
	 */
	public long loadAllCountFromB(final @Nullable B b) {
		return loadCountFromCol(null, getKreuzColB(), new Parameter(b, getTypeB()), "loadAllCountFromB");
	}
	
	/**
	 * Lädt die Anzahl aller möglichen Kreuzobjekte aus der Datenbank von einer Spalte
	 *
	 * @param join  Die JOIN Klausel oder {@code null}
	 * @param col   Die Spalte für WHERE
	 * @param param Der Wert für WHERE
	 *
	 * @return die Anzahl aller Kreuze
	 */
	protected long loadCountFromCol(final @Nullable String join, final @NotNull String col, final @NotNull Object param) {
		return loadCountFromCol(join, col, param, null);
	}
	
	/**
	 * Lädt die Anzahl aller möglichen Kreuzobjekte aus der Datenbank von einer Spalte
	 *
	 * @param join     Die JOIN Klausel oder {@code null}
	 * @param col      Die Spalte für WHERE
	 * @param param    Der Wert für WHERE
	 * @param cacheKey der Key für den pstCache
	 *
	 * @return die Anzahl aller Kreuze
	 */
	protected long loadCountFromCol(final @Nullable String join, final @NotNull String col, final @NotNull Object param, final @Nullable String cacheKey) {
		return loadCountFromWhere(join, col + "=?", new ParameterList(param), cacheKey);
	}
	
	/**
	 * Lädt die Anzahl aller möglichen Kreuzobjekte aus der Datenbank mit benutzerspezifizierten Bedingungen
	 *
	 * @param join   Die JOIN Klausel oder {@code null}
	 * @param where  Die WHERE Klausel oder {@code null}
	 * @param params Die Parameter oder {@code null}
	 *
	 * @return die Anzahl aller Kreuze
	 */
	protected long loadCountFromWhere(final @Nullable String join, final @Nullable String where, final @Nullable ParameterList params) {
		return loadCountFromWhere(join, where, params, null);
	}
	
	/**
	 * Lädt die Anzahl aller möglichen Kreuzobjekte aus der Datenbank mit benutzerspezifizierten Bedingungen
	 *
	 * @param join     Die JOIN Klausel oder {@code null}
	 * @param where    Die WHERE Klausel oder {@code null}
	 * @param params   Die Parameter oder {@code null}
	 * @param cacheKey der Key für den pstCache
	 *
	 * @return die Anzahl aller Kreuze
	 */
	protected long loadCountFromWhere(final @Nullable String join, final @Nullable String where, final @Nullable ParameterList params, final @Nullable String cacheKey) {
		KreuzDAOConnection con = null;
		PreparedStatement pst = null;
		try {
			con = connectionPool.borrowObject();
			pst = con.getPst("count(*)", join, where, null, null, cacheKey, params);
			
			if (params != null) params.setParameter(pst, 1);
			
			logPst(pst);
			try (ResultSet rs = pst.executeQuery()) {
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
	
}
