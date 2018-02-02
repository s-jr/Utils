package net.sjr.sql;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Basisinterface für die DAOs inkl. der CRUD Methoden
 * @param <T> Typ des via DAO zu speichernden {@link DBObject}
 * @param <P> Typ des Primary Keys
 */
@SuppressWarnings("unused")
public interface DAOBase<T extends DBObject<P>, P extends Number> extends AutoCloseable {
	/**
	 * Lädt ein Objekt von T an Hand seiner PrimaryID
	 *
	 * @param primary die PrimaryID des Objektes
	 *
	 * @return das Objekt, niemals {@code null}
	 */
	@NotNull T loadFromID(@NotNull P primary);
	
	/**
	 * Lädt eine Liste aller Objekte von T
	 *
	 * @return Eine Liste aller Objekte von T. Niemals {@code null}
	 */
	@NotNull List<T> loadAll();
	
	/**
	 * Fügt ein Objekt von T in die Datenbank ein<br>
	 *
	 * @param v das einzufügende Objekt
	 */
	void insertIntoDB(@NotNull T v);
	
	/**
	 * Aktualisiert ein Objekt von T in der Datenbank<br>
	 *
	 * @param v das zu aktualisierende Objekt
	 */
	void updateIntoDB(@NotNull T v);
	
	/**
	 * Löscht ein Objekt von T aus der Datenbank<br>
	 * <b>Das Objekt muss eine PrimaryID haben um es in der Datenbank zu identifizieren!</b>
	 *
	 * @param v das zu löschende Objekt
	 */
	void deleteFromDB(@NotNull T v);
	
	void insertOrUpdate(@NotNull T v);
	
	/**
	 * Holt den Primary Key aus der Datenbank
	 * @param rs das {@link ResultSet} aus dem geladen werden soll
	 * @param pos die Position aus der geladen werden soll
	 * @return der Primary Key
	 * @throws SQLException wenn eine SQLException auftritt
	 */
	@Nullable P getPrimary(@NotNull ResultSet rs, int pos) throws SQLException;
}
