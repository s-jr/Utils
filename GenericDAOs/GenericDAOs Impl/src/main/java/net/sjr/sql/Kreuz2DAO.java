package net.sjr.sql;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO um Daten aus Kreuztabellen für 2 via n:m Verbindung verbundene Tabellen laden zu können
 * @param <A> Typ des ersten Java Objektes
 * @param <PA> Typ des Primary Keys des ersten Java Objektes
 * @param <B> Typ des zweiten Java Objektes
 * @param <PB> Typ des Primary Keys des zweiten Java Objektes
 */
@SuppressWarnings("unused")
public abstract class Kreuz2DAO<A extends DBObject<PA>, PA extends Number, B extends DBObject<PB>, PB extends Number> extends KreuzDAOBase<A, PA, B, PB, Kreuz2Objekt<A, PA, B, PB>> implements AutoCloseable {
	
	/**
	 * Erstellt die {@link Kreuz2DAO} mit einer {@link DataSource}
	 *
	 * @param ds die {@link DataSource}
	 */
	public Kreuz2DAO(final @NotNull DataSource ds) {
		super(ds);
	}
	
	/**
	 * Erstellt die {@link Kreuz2DAO} mit einer bereits vorhandenen Datenbankverbindung
	 *
	 * @param con die bereits vorhandene Datenbankverbindung
	 */
	public Kreuz2DAO(final @NotNull Connection con) {
		super(con);
	}
	
	/**
	 * Erstellt die {@link Kreuz2DAO} mit einem bereits vorhandenen {@link DAOBase}
	 *
	 * @param dao die bereits vorhandene {@link DAOBase}
	 */
	public Kreuz2DAO(final @NotNull DAOBase<?, ?> dao) {
		super(dao);
	}
	
	/**
	 * Gibt alle Spalten der Kreuztabelle zurück
	 *
	 * @return die Spalten
	 */
	@Override
	protected @NotNull String getAllKreuzCols() {
		return getKreuzColA() + ", " + getKreuzColB();
	}
	
	/**
	 * Erstellt aus einem {@link ResultSet} ein {@link Kreuz2Objekt} mit den beiden Verbundenen Objekten
	 * @param rs das {@link ResultSet}
	 * @param loadedObjects die bereits geladenen Objekte
	 * @return das {@link Kreuz2Objekt}
	 * @throws SQLException Wenn eine {@link SQLException} aufgetreten ist
	 */
	@Override
	protected @NotNull Kreuz2Objekt<A, PA, B, PB> getKreuzObjekt(final @NotNull ResultSet rs, final DBObject... loadedObjects) throws SQLException {
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
	public void createKreuzInDB(final @Nullable A a, final @Nullable B b) {
		super.createKreuzInDB(new Parameter(a, getTypeA()), new Parameter(b, getTypeB()));
	}

	/**
	 * Löscht eine Kreuzverbindung zwischen 2 Objekten aus der Datenbank<br>
	 *
	 * @param a das erste verbundene Objekt
	 * @param b das zweite verbundene Objekt
	 */
	public void deleteKreuzFromDB(final @Nullable A a, final @Nullable B b) {
		super.deleteKreuzFromDB(new Parameter(a, getTypeA()), new Parameter(b, getTypeB()));
	}
}
