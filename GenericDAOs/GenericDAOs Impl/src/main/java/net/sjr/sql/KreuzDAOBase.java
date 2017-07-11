package net.sjr.sql;

import net.sjr.sql.exceptions.UncheckedSQLException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Jan on 20.05.2017.
 */
public abstract class KreuzDAOBase<A extends DBObject<PA>, PA extends Number, B extends DBObject<PB>, PB extends Number, KO extends Kreuz2Objekt<A, PA, B, PB>> {
	private final Logger log = LogManager.getLogger(getClass());
	private final Map<String, PreparedStatement> pstCache = new HashMap<>();

	protected abstract DAO<A, PA> getaDAO();

	protected abstract DAO<B, PB> getbDAO();

	protected abstract String getKreuzTable();

	protected abstract String getKreuzColA();

	protected abstract String getKreuzColB();

	protected abstract String getAllKreuzCols();

	protected abstract KO getKreuzObjekt(ResultSet rs, DBObject... loadedObjects) throws SQLException;

	protected Integer getTypeA() {
		return null;
	}

	protected Integer getTypeB() {
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
	public List<A> loadAfromB(B b, DBObject... loadedObjects) {
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
	public List<B> loadBfromA(A a, DBObject... loadedObjects) {
		return executeFrom1(a, getbDAO(), getKreuzColB(), getKreuzColA(), getTypeA(), loadedObjects);
	}

	protected <T extends DBObject<P>, P extends Number> List<T> executeFrom1(DBObject a, DAO<T, P> dao, String resultKreuzCol, String aKreuzCol, Integer typeA, DBObject... loadedObjects) {
		return dao.loadAllFromCol(getKreuzTable() + " ON " + getKreuzTable() + "." + resultKreuzCol + "=" + dao.getTable() + "." + dao.getPrimaryCol(),
				getKreuzTable() + "." + aKreuzCol, new Parameter(a, typeA),
				null, null, getKreuzTable() + ".load" + resultKreuzCol + "from" + aKreuzCol, loadedObjects);
	}

	private PreparedStatement createKreuzPst() throws SQLException {
		PreparedStatement result = pstCache.get("createKreuz");
		if (result == null) {
			result = getaDAO().getConnection().prepareStatement("INSERT INTO " + getKreuzTable() + " (" + getAllKreuzCols() + ") VALUES (" + SQLUtils.getFragezeichenInsert(getAllKreuzCols()) + ")");
			pstCache.put("createKreuz", result);
		}
		return result;
	}

	/**
	 * Erstellt eine neue Kreuzverbindung zwischen Objekten
	 *
	 * @param params die zu verbindende Objekte
	 */
	protected void createKreuzInDB(DBObject... params) {
		try {
			PreparedStatement pst = createKreuzPst();

			new ParameterList(params).setParameter(pst, 1);
			logPst(pst);
			pst.executeUpdate();
		}
		catch (SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}


	private PreparedStatement deleteKreuzPst() throws SQLException {
		PreparedStatement result = pstCache.get("deleteKreuz");
		if (result == null) {
			result = getaDAO().getConnection().prepareStatement("DELETE FROM " + getKreuzTable() + " WHERE " + SQLUtils.getFragezeichenSelect(getAllKreuzCols(), " AND ", "="));
			pstCache.put("deleteKreuz", result);
		}
		return result;
	}

	/**
	 * Löscht eine Kreuzverbindung zwischen Objekten aus der Datenbank<br>
	 *
	 * @param params die verbundene Objekte
	 */
	protected void deleteKreuzFromDB(DBObject... params) {
		try {
			PreparedStatement pst = deleteKreuzPst();
			new ParameterList(params).setParameter(pst, 1);

			logPst(pst);
			pst.executeUpdate();
		}
		catch (SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}

	/**
	 * Lädt alle möglichen Kreuzobjekte aus der Datenbank
	 *
	 * @return eine Liste aller gefundenen Kreuzobjekten. Niemals null
	 */
	public List<KO> loadAllKreuze() {
		return loadKreuzeFromWhere(null, null, null, null, null, "loadAllKreuze");
	}

	/**
	 * Lädt alle möglichen Kreuzobjekte aus der Datenbank von einer Spalte
	 *
	 * @param join          Die JOIN Klausel oder null
	 * @param col           Die Spalte für WHERE
	 * @param param         Der Wert für WHERE
	 * @param limit         das Limit für die Anzahl der Ergebnisse oder null
	 * @param order         Die ORDER Klausel oder null
	 * @param loadedObjects Objekte, die schon geladen wurden und somit nicht neu geladen werden müssen
	 *
	 * @return eine Liste aller gefundenen Kreuzobjekten. Niemals null
	 */
	protected List<KO> loadKreuzeFromCol(final String join, final String col, final Object param, final String limit, final String order, final DBObject... loadedObjects) {
		return loadKreuzeFromCol(join, col, param, limit, order, null, loadedObjects);
	}

	/**
	 * Lädt alle möglichen Kreuzobjekte aus der Datenbank von einer Spalte
	 *
	 * @param join          Die JOIN Klausel oder null
	 * @param col           Die Spalte für WHERE
	 * @param param         Der Wert für WHERE
	 * @param limit         das Limit für die Anzahl der Ergebnisse oder null
	 * @param order         Die ORDER Klausel oder null
	 * @param cacheKey      der Key für den pstCache
	 * @param loadedObjects Objekte, die schon geladen wurden und somit nicht neu geladen werden müssen
	 *
	 * @return eine Liste aller gefundenen Kreuzobjekten. Niemals null
	 */
	protected List<KO> loadKreuzeFromCol(final String join, final String col, final Object param, final String limit, final String order, final String cacheKey, final DBObject... loadedObjects) {
		return loadKreuzeFromWhere(join, col + "=?", new ParameterList(param), limit, order, cacheKey, loadedObjects);
	}

	/**
	 * Lädt alle möglichen Kreuzobjekte aus der Datenbank mit benutzerspezifizierten Bedingungen
	 *
	 * @param join          Die JOIN Klausel oder null
	 * @param where         Die WHERE Klausel oder null
	 * @param params        Die Parameter oder null
	 * @param limit         das Limit für die Anzahl der Ergebnisse oder null
	 * @param order         Die ORDER Klausel oder null
	 * @param loadedObjects Objekte, die schon geladen wurden und somit nicht neu geladen werden müssen
	 *
	 * @return eine Liste aller gefundenen Kreuzobjekten. Niemals null
	 */
	protected List<KO> loadKreuzeFromWhere(final String join, final String where, final ParameterList params, final String limit, final String order, final DBObject... loadedObjects) {
		return loadKreuzeFromWhere(join, where, params, limit, order, null, loadedObjects);
	}

	/**
	 * Lädt alle möglichen Kreuzobjekte aus der Datenbank mit benutzerspezifizierten Bedingungen
	 *
	 * @param join          Die JOIN Klausel oder null
	 * @param where         Die WHERE Klausel oder null
	 * @param params        Die Parameter oder null
	 * @param limit         das Limit für die Anzahl der Ergebnisse oder null
	 * @param order         Die ORDER Klausel oder null
	 * @param cacheKey      der Key für den pstCache
	 * @param loadedObjects Objekte, die schon geladen wurden und somit nicht neu geladen werden müssen
	 *
	 * @return eine Liste aller gefundenen Kreuzobjekten. Niemals null
	 */
	protected List<KO> loadKreuzeFromWhere(final String join, final String where, final ParameterList params, final String limit, final String order, final String cacheKey, final DBObject... loadedObjects) {
		try {
			PreparedStatement pst = DAO.getPst(getaDAO().getConnection(), pstCache, getKreuzTable(), null, getAllKreuzCols(), join, where, limit, order, cacheKey, params);

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
		catch (SQLException e) {
			throw new UncheckedSQLException(e);
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
	public long loadAllCountFromA(A a) {
		return loadCountFromCol(null, getKreuzColA(), a, "loadAllCountFromA");
	}

	/**
	 * Lädt die Anzahl aller möglichen Kreuzobjekte mit B aus der Datenbank
	 *
	 * @param b B zu welcher die Anzahl gesucht wird
	 *
	 * @return die Anzahl aller Kreuze
	 */
	public long loadAllCountFromB(B b) {
		return loadCountFromCol(null, getKreuzColB(), b, "loadAllCountFromB");
	}

	/**
	 * Lädt die Anzahl aller möglichen Kreuzobjekte aus der Datenbank von einer Spalte
	 *
	 * @param join  Die JOIN Klausel oder null
	 * @param col   Die Spalte für WHERE
	 * @param param Der Wert für WHERE
	 *
	 * @return die Anzahl aller Kreuze
	 */
	protected long loadCountFromCol(final String join, final String col, final Object param) {
		return loadCountFromCol(join, col, param, null);
	}

	/**
	 * Lädt die Anzahl aller möglichen Kreuzobjekte aus der Datenbank von einer Spalte
	 *
	 * @param join     Die JOIN Klausel oder null
	 * @param col      Die Spalte für WHERE
	 * @param param    Der Wert für WHERE
	 * @param cacheKey der Key für den pstCache
	 *
	 * @return die Anzahl aller Kreuze
	 */
	protected long loadCountFromCol(final String join, final String col, final Object param, final String cacheKey) {
		return loadCountFromWhere(join, col + "=?", new ParameterList(param), cacheKey);
	}

	/**
	 * Lädt die Anzahl aller möglichen Kreuzobjekte aus der Datenbank mit benutzerspezifizierten Bedingungen
	 *
	 * @param join   Die JOIN Klausel oder null
	 * @param where  Die WHERE Klausel oder null
	 * @param params Die Parameter oder null
	 *
	 * @return die Anzahl aller Kreuze
	 */
	protected long loadCountFromWhere(final String join, final String where, final ParameterList params) {
		return loadCountFromWhere(join, where, params, null);
	}

	/**
	 * Lädt die Anzahl aller möglichen Kreuzobjekte aus der Datenbank mit benutzerspezifizierten Bedingungen
	 *
	 * @param join     Die JOIN Klausel oder null
	 * @param where    Die WHERE Klausel oder null
	 * @param params   Die Parameter oder null
	 * @param cacheKey der Key für den pstCache
	 *
	 * @return die Anzahl aller Kreuze
	 */
	protected long loadCountFromWhere(final String join, final String where, final ParameterList params, final String cacheKey) {
		try {
			PreparedStatement pst = DAO.getPst(getaDAO().getConnection(), pstCache, getKreuzTable(), null, "count(*)", join, where, null, null, cacheKey, params);

			if (params != null) params.setParameter(pst, 1);

			logPst(pst);
			try (ResultSet rs = pst.executeQuery()) {
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

	private void logPst(PreparedStatement pst) {
		log.debug(SQLUtils.pstToSQL(pst));
	}

	public void close() {
		for (PreparedStatement pst : pstCache.values()) {
			try {
				pst.close();
			}
			catch (SQLException ignored) {
			}
		}
		pstCache.clear();
	}

}
