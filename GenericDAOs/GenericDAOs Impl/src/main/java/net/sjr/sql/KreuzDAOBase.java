package net.sjr.sql;

import net.sjr.sql.exceptions.UncheckedSQLException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jan on 20.05.2017.
 */
public abstract class KreuzDAOBase<A extends DBObject<PA>, PA extends Number, B extends DBObject<PB>, PB extends Number> {
	private final Logger log = LogManager.getLogger(getClass());
	private final Map<String, PreparedStatement> pstCache = new HashMap<>();

	protected abstract DAO<A, PA> getaDAO();

	protected abstract DAO<B, PB> getbDAO();

	protected abstract String getKreuzTable();

	protected abstract String getKreuzColA();

	protected abstract String getKreuzColB();

	protected abstract String getAllKreuzCols();

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

	<T extends DBObject<P>, P extends Number> List<T> executeFrom1(DBObject a, DAO<T, P> dao, String resultKreuzCol, String aKreuzCol, Integer typeA, DBObject... loadedObjects) {
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
	void createKreuzInDB(DBObject... params) {
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
	void deleteKreuzFromDB(DBObject... params) {
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
