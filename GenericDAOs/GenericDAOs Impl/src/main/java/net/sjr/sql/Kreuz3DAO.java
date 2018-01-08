package net.sjr.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO um Daten aus Kreuztabellen für 3 via l:n:m Verbindung verbundene Tabellen laden zu können
 * @param <A> Typ des ersten Java Objektes
 * @param <PA> Typ des Primary Keys des ersten Java Objektes
 * @param <B> Typ des zweiten Java Objektes
 * @param <PB> Typ des Primary Keys des zweiten Java Objektes
 * @param <C> Typ des dritten Java Objektes
 * @param <PC> Typ des Primary Keys des dritten Java Objektes
 */
@SuppressWarnings({"unused", "WeakerAccess", "SameReturnValue"})
public abstract class Kreuz3DAO<A extends DBObject<PA>, PA extends Number, B extends DBObject<PB>, PB extends Number, C extends DBObject<PC>, PC extends Number> extends KreuzDAOBase<A, PA, B, PB, Kreuz3Objekt<A, PA, B, PB, C, PC>> {
	/**
	 * gibt die {@link DAO} zurück, welche für das Laden der C Objekte genutzt werden soll
	 *
	 * @return die {@link DAO}
	 */
	protected abstract DAO<C, PC> getcDAO();
	
	/**
	 * gibt die Spalte der C Objetke in der Kreuztabelle zurück
	 * @return der Spaltenname
	 */
	protected abstract String getKreuzColC();
	
	/**
	 * gibt den Spaltentyp aus der {@link java.sql.Types} Klasse der C Objetke in der Kreuztabelle zurück
	 * @return der Spaltentyp
	 */
	protected Integer getTypeC() {
		return null;
	}
	
	/**
	 * Gibt alle Spalten der Kreuztabelle zurück
	 * @return die Spalten
	 */
	@Override
	protected String getAllKreuzCols() {
		return getKreuzColA() + ", " + getKreuzColB() + ", " + getKreuzColC();
	}
	
	/**
	 * Erstellt aus einem {@link ResultSet} ein {@link Kreuz3Objekt} mit den drei Verbundenen Objekten
	 * @param rs das {@link ResultSet}
	 * @param loadedObjects die bereits geladenen Objekte
	 * @return das {@link Kreuz3Objekt}
	 * @throws SQLException Wenn eine {@link SQLException} aufgetreten ist
	 */
	@Override
	protected Kreuz3Objekt<A, PA, B, PB, C, PC> getKreuzObjekt(ResultSet rs, DBObject... loadedObjects) throws SQLException {
		A a = SQLUtils.loadedObjectsOrNull(1, rs, getaDAO(), loadedObjects);
		B b = SQLUtils.loadedObjectsOrNull(2, rs, getbDAO(), loadedObjects);
		C c = SQLUtils.loadedObjectsOrNull(3, rs, getcDAO(), loadedObjects);

		return new Kreuz3Objekt<>(a, b, c);
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
	
	/**
	 * Lädt eine Liste von Objekten an Hand der Kombination der beiden anderen Spalten der Kreuztabelle
	 * @param a erstes Objekt, nach dem gesucht werden soll
	 * @param b zweites Objekt , nach dem gesucht werden soll
	 * @param dao {@link DAO} des Zielobjektes
	 * @param resultKreuzCol Spalte des Zielobjektes
	 * @param aKreuzCol Spalte des ersten Suchobjektes
	 * @param bKreuzCol Spalte des zweiten Suchobjektes
	 * @param typeA Typ des ersten Suchobjektes
	 * @param typeB Typ des zweiten Suchobjektes
	 * @param loadedObjects Objekte, die schon geladen wurden und somit nicht neu geladen werden müssen
	 * @param <T> Typ des Zielobjektes
	 * @param <P> Typ des Primary Key des Zielobjektes
	 * @return eine Liste aller gefundenen Zielobjekte. Niemals {@code null}
	 */
	protected <T extends DBObject<P>, P extends Number> List<T> executeFrom2(DBObject a, DBObject b, DAO<T, P> dao, String resultKreuzCol, String aKreuzCol, String bKreuzCol, Integer typeA, Integer typeB, DBObject... loadedObjects) {
		return dao.loadAllFromWhere(
				getKreuzTable() + " ON " + getKreuzTable() + '.' + resultKreuzCol + '=' + dao.getTable() + '.' + dao.getPrimaryCol(),
				getKreuzTable() + '.' + aKreuzCol + "=? AND " + getKreuzTable() + '.' + bKreuzCol + "=?", new ParameterList(new Parameter(a, typeA), new Parameter(b, typeB)),
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

	/**
	 * Lädt alle Kreuzobjekte mit A
	 *
	 * @param a A zu welchem die Kreuzobjekte gesucht werden sollen
	 *
	 * @return Die Liste aller Kreuzobjekte. Niemals null
	 */
	public List<Kreuz3Objekt<A, PA, B, PB, C, PC>> loadKreuzeFromA(A a) {
		return loadKreuzeFromCol(null, getKreuzColA(), a, null, null, "loadKreuzeFromA", a);
	}

	/**
	 * Lädt alle Kreuzobjekte mit B
	 *
	 * @param b B zu welchem die Kreuzobjekte gesucht werden sollen
	 *
	 * @return Die Liste aller Kreuzobjekte. Niemals null
	 */
	public List<Kreuz3Objekt<A, PA, B, PB, C, PC>> loadKreuzeFromB(B b) {
		return loadKreuzeFromCol(null, getKreuzColB(), b, null, null, "loadKreuzeFromB", b);
	}

	/**
	 * Lädt alle Kreuzobjekte mit C
	 *
	 * @param c C zu welchem die Kreuzobjekte gesucht werden sollen
	 *
	 * @return Die Liste aller Kreuzobjekte. Niemals null
	 */
	public List<Kreuz3Objekt<A, PA, B, PB, C, PC>> loadKreuzeFromC(C c) {
		return loadKreuzeFromCol(null, getKreuzColC(), c, null, null, "loadKreuzeFromC", c);
	}

	/**
	 * Lädt die Anzahl aller möglichen Kreuzobjekte mit C aus der Datenbank
	 *
	 * @param c C zu welcher die Anzahl gesucht wird
	 *
	 * @return die Anzahl aller Kreuze
	 */
	public long loadAllCountFromC(C c) {
		return loadCountFromCol(null, getKreuzColC(), c, "loadAllCountFromC");
	}

	/**
	 * Lädt die Anzahl aller möglichen Kreuzobjekte mit A und B aus der Datenbank
	 *
	 * @param a A zu welcher die Anzahl gesucht wird
	 * @param b B zu welcher die Anzahl gesucht wird
	 *
	 * @return die Anzahl aller Kreuze
	 */
	public long loadAllCountFromAundB(A a, B b) {
		return loadCountFromWhere(null, getKreuzColA() + "=? AND " + getKreuzColB() + "=?", new ParameterList(new Parameter(a, getTypeA()), new Parameter(b, getTypeB())), "loadAllCountFromAundB");
	}

	/**
	 * Lädt die Anzahl aller möglichen Kreuzobjekte mit A und C aus der Datenbank
	 *
	 * @param a A zu welcher die Anzahl gesucht wird
	 * @param c C zu welcher die Anzahl gesucht wird
	 *
	 * @return die Anzahl aller Kreuze
	 */
	public long loadAllCountFromAundC(A a, C c) {
		return loadCountFromWhere(null, getKreuzColA() + "=? AND " + getKreuzColC() + "=?", new ParameterList(new Parameter(a, getTypeA()), new Parameter(c, getTypeC())), "loadAllCountFromAundC");
	}

	/**
	 * Lädt die Anzahl aller möglichen Kreuzobjekte mit B und C aus der Datenbank
	 *
	 * @param b B zu welcher die Anzahl gesucht wird
	 * @param c C zu welcher die Anzahl gesucht wird
	 *
	 * @return die Anzahl aller Kreuze
	 */
	public long loadAllCountFromBundC(B b, C c) {
		return loadCountFromWhere(null, getKreuzColB() + "=? AND " + getKreuzColC() + "=?", new ParameterList(new Parameter(b, getTypeB()), new Parameter(c, getTypeC())), "loadAllCountFromBundC");
	}
}
