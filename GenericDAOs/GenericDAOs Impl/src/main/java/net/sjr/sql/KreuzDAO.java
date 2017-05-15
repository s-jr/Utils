package net.sjr.sql;

import net.sjr.sql.exceptions.UncheckedSQLException;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jan on 15.05.2017.
 */
public abstract class KreuzDAO<A extends DBObject<PA>, PA extends Number, B extends DBObject<PB>, PB extends Number> implements AutoCloseable {
	private final Map<String, PreparedStatement> pstCache = new HashMap<>();

	protected abstract DAO<A, PA> getaDAO();

	protected abstract DAO<B, PB> getbDAO();

	protected abstract String getKreuzTable();

	protected abstract String getKreuzColA();

	protected abstract String getKreuzColB();

	/**
	 * Lädt eine Liste aller Objekte a, die mit b über die Kreuztabelle verbunden sind
	 *
	 * @param b             das Objekt mit dem Verbunden sein muss
	 * @param loadedObjects Objekte, die schon geladen wurden und somit nicht neu geladen werden müssen
	 *
	 * @return die Liste aller verbundenen Objekte
	 */
	public List<A> loadAfromB(B b, DBObject... loadedObjects) {
		return getaDAO().loadAllFromCol(getKreuzTable() + " ON " + getKreuzTable() + "." + getKreuzColA() + "=" + getaDAO().getTable() + "." + getaDAO().getPrimaryCol(),
				getKreuzTable() + "." + getKreuzColB(), b,
				null, null, getKreuzTable() + ".loadAfromB", loadedObjects);
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
		return getbDAO().loadAllFromCol(getKreuzTable() + " ON " + getKreuzTable() + "." + getKreuzColB() + "=" + getbDAO().getTable() + "." + getbDAO().getPrimaryCol(),
				getKreuzTable() + "." + getKreuzColA(), a,
				null, null, getKreuzTable() + ".loadBfromA", loadedObjects);
	}

	private PreparedStatement createKreuzPst() throws SQLException {
		PreparedStatement result = pstCache.get("createKreuz");
		if (result == null) {
			result = getaDAO().connection.prepareStatement("INSERT INTO " + getKreuzTable() + " (" + getKreuzColA() + ", " + getKreuzColB() + ") VALUES (?, ?)");
			pstCache.put("createKreuz", result);
		}
		return result;
	}

	/**
	 * Erstellt eine neue Kreuzverbindung zwischen 2 Objekten
	 *
	 * @param a das erste zu verbindende Objekt
	 * @param b das zweite zu verbindende Objekt
	 */
	public void createKreuzInDB(A a, B b) {
		try {
			PreparedStatement pst = createKreuzPst();

			new ParameterList(a, b).setParameter(pst, 1);
			getaDAO().logPst(pst);
			pst.executeUpdate();
		}
		catch (SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}

	private PreparedStatement deleteKreuzPst() throws SQLException {
		PreparedStatement result = pstCache.get("deleteKreuz");
		if (result == null) {
			result = getaDAO().connection.prepareStatement("DELETE FROM " + getKreuzTable() + " WHERE " + getKreuzColA() + "=? AND " + getKreuzColB() + "=?");
			pstCache.put("deleteKreuz", result);
		}
		return result;
	}

	/**
	 * Löscht eine Kreuzverbindung zwischen 2 Objekten aus der Datenbank<br>
	 *
	 * @param a das erste verbundene Objekt
	 * @param b das zweite verbundene Objekt
	 */
	public void deleteKreuzFromDB(final A a, final B b) {
		try {
			PreparedStatement pst = deleteKreuzPst();
			new ParameterList(a, b).setParameter(pst, 1);

			getaDAO().logPst(pst);
			pst.executeUpdate();
		}
		catch (SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}

	@Override
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
