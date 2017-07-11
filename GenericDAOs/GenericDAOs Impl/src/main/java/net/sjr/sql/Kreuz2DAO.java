package net.sjr.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Jan on 15.05.2017.
 */
public abstract class Kreuz2DAO<A extends DBObject<PA>, PA extends Number, B extends DBObject<PB>, PB extends Number> extends KreuzDAOBase<A, PA, B, PB, Kreuz2Objekt<A, PA, B, PB>> implements AutoCloseable {
	@Override
	protected String getAllKreuzCols() {
		return getKreuzColA() + ", " + getKreuzColB();
	}

	@Override
	protected Kreuz2Objekt<A, PA, B, PB> getKreuzObjekt(ResultSet rs, DBObject... loadedObjects) throws SQLException {
		A a = SQLUtils.loadedObjectsOrNull(1, rs, getaDAO(), loadedObjects);
		B b = SQLUtils.loadedObjectsOrNull(2, rs, getbDAO(), loadedObjects);

		return new Kreuz2Objekt<>(a, b);
	}

	/**
	 * Erstellt eine neue Kreuzverbindung zwischen 2 Objekten
	 *
	 * @param a das erste zu verbindende Objekt
	 * @param b das zweite zu verbindende Objekt
	 */
	public void createKreuzInDB(A a, B b) {
		super.createKreuzInDB(a, b);
	}

	/**
	 * Löscht eine Kreuzverbindung zwischen 2 Objekten aus der Datenbank<br>
	 *
	 * @param a das erste verbundene Objekt
	 * @param b das zweite verbundene Objekt
	 */
	public void deleteKreuzFromDB(final A a, final B b) {
		super.deleteKreuzFromDB(a, b);
	}
}
