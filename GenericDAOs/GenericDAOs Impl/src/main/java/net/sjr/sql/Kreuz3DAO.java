package net.sjr.sql;

import java.util.List;

/**
 * Created by Jan on 20.05.2017.
 */
public abstract class Kreuz3DAO<A extends DBObject<PA>, PA extends Number, B extends DBObject<PB>, PB extends Number, C extends DBObject<PC>, PC extends Number> extends KreuzDAOBase<A, PA, B, PB> {
	protected abstract DAO<C, PC> getcDAO();

	protected abstract String getKreuzColC();

	protected Integer getTypeC() {
		return null;
	}

	@Override
	protected String getAllKreuzCols() {
		return getKreuzColA() + ", " + getKreuzColB() + ", " + getKreuzColC();
	}


	/**
	 * Lädt eine Liste aller Objekte a, die mit c über die Kreuztabelle verbunden sind
	 *
	 * @param c             das Objekt mit dem Verbunden sein muss
	 * @param loadedObjects Objekte, die schon geladen wurden und somit nicht neu geladen werden müssen
	 *
	 * @return die Liste aller verbundenen Objekte
	 */
	public List<A> loadAfromC(C c, DBObject... loadedObjects) {
		return executeFrom1(c, getaDAO(), getKreuzColA(), getKreuzColC(), getTypeC(), loadedObjects);
	}

	/**
	 * Lädt eine Liste aller Objekte b, die mit c über die Kreuztabelle verbunden sind
	 *
	 * @param c             das Objekt mit dem Verbunden sein muss
	 * @param loadedObjects Objekte, die schon geladen wurden und somit nicht neu geladen werden müssen
	 *
	 * @return die Liste aller verbundenen Objekte
	 */
	public List<B> loadBfromC(C c, DBObject... loadedObjects) {
		return executeFrom1(c, getbDAO(), getKreuzColB(), getKreuzColC(), getTypeC(), loadedObjects);
	}

	/**
	 * Lädt eine Liste aller Objekte c, die mit a über die Kreuztabelle verbunden sind
	 *
	 * @param a             das Objekt mit dem Verbunden sein muss
	 * @param loadedObjects Objekte, die schon geladen wurden und somit nicht neu geladen werden müssen
	 *
	 * @return die Liste aller verbundenen Objekte
	 */
	public List<C> loadCfromA(A a, DBObject... loadedObjects) {
		return executeFrom1(a, getcDAO(), getKreuzColC(), getKreuzColA(), getTypeA(), loadedObjects);
	}

	/**
	 * Lädt eine Liste aller Objekte c, die mit b über die Kreuztabelle verbunden sind
	 *
	 * @param b             das Objekt mit dem Verbunden sein muss
	 * @param loadedObjects Objekte, die schon geladen wurden und somit nicht neu geladen werden müssen
	 *
	 * @return die Liste aller verbundenen Objekte
	 */
	public List<C> loadCfromB(B b, DBObject... loadedObjects) {
		return executeFrom1(b, getcDAO(), getKreuzColC(), getKreuzColB(), getTypeB(), loadedObjects);
	}

	/**
	 * Lädt eine Liste aller Objekte a, die mit b und c über die Kreuztabelle verbunden sind
	 *
	 * @param b             das erste Objekt mit dem Verbunden sein muss
	 * @param c             das zweite Objekt mit dem Verbunden sein muss
	 * @param loadedObjects Objekte, die schon geladen wurden und somit nicht neu geladen werden müssen
	 *
	 * @return die Liste aller verbundenen Objekte
	 */
	public List<A> loadAfromBundC(B b, C c, DBObject... loadedObjects) {
		return executeFrom2(b, c, getaDAO(), getKreuzColA(), getKreuzColB(), getKreuzColC(), getTypeB(), getTypeC(), loadedObjects);
	}

	/**
	 * Lädt eine Liste aller Objekte b, die mit a und c über die Kreuztabelle verbunden sind
	 *
	 * @param a             das erste Objekt mit dem Verbunden sein muss
	 * @param c             das zweite Objekt mit dem Verbunden sein muss
	 * @param loadedObjects Objekte, die schon geladen wurden und somit nicht neu geladen werden müssen
	 *
	 * @return die Liste aller verbundenen Objekte
	 */
	public List<B> loadBfromAundC(A a, C c, DBObject... loadedObjects) {
		return executeFrom2(a, c, getbDAO(), getKreuzColB(), getKreuzColA(), getKreuzColC(), getTypeA(), getTypeC(), loadedObjects);
	}

	/**
	 * Lädt eine Liste aller Objekte a, die mit b und c über die Kreuztabelle verbunden sind
	 *
	 * @param a             das erste Objekt mit dem Verbunden sein muss
	 * @param b             das zweite Objekt mit dem Verbunden sein muss
	 * @param loadedObjects Objekte, die schon geladen wurden und somit nicht neu geladen werden müssen
	 *
	 * @return die Liste aller verbundenen Objekte
	 */
	public List<C> loadCfromAundB(A a, B b, DBObject... loadedObjects) {
		return executeFrom2(a, b, getcDAO(), getKreuzColC(), getKreuzColA(), getKreuzColB(), getTypeA(), getTypeB(), loadedObjects);
	}

	<T extends DBObject<P>, P extends Number> List<T> executeFrom2(DBObject a, DBObject b, DAO<T, P> dao, String resultKreuzCol, String aKreuzCol, String bKreuzCol, Integer typeA, Integer typeB, DBObject... loadedObjects) {
		return dao.loadAllFromWhere(
				getKreuzTable() + " ON " + getKreuzTable() + "." + resultKreuzCol + "=" + dao.getTable() + "." + dao.getPrimaryCol(),
				getKreuzTable() + "." + aKreuzCol + "=? AND " + getKreuzTable() + "." + bKreuzCol + "=?", new ParameterList(new Parameter(a, typeA), new Parameter(b, typeB)),
				null, null,
				getKreuzTable() + ".load" + resultKreuzCol + "from" + aKreuzCol + "und" + bKreuzCol, loadedObjects);
	}

	/**
	 * Erstellt eine neue Kreuzverbindung zwischen 3 Objekten
	 *
	 * @param a das erste zu verbindende Objekt
	 * @param b das zweite zu verbindende Objekt
	 * @param c das dritte zu verbindende Objekt
	 */
	public void createKreuzInDB(final A a, final B b, final C c) {
		super.createKreuzInDB(a, b, c);
	}

	/**
	 * Löscht eine Kreuzverbindung zwischen 3 Objekten aus der Datenbank<br>
	 *
	 * @param a das erste verbundene Objekt
	 * @param b das zweite verbundene Objekt
	 * @param c das dritte verbundene Objekt
	 */
	public void deleteKreuzFromDB(final A a, final B b, final C c) {
		super.deleteKreuzFromDB(a, b, c);
	}
}
