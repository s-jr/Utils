package net.sjr.sql;

import java.io.Serializable;

/**
 * Interface um ein {@link Object} in der Datenbank speicherbar zu machen
 *
 * @param <P> der Typ des Primary Keys
 */
public interface DBObject<P extends Number> extends Serializable, DBConvertable {
	/**
	 * gibt die Primary ID zurück
	 *
	 * @return die Primary ID
	 */
	P getPrimary();
	
	/**
	 * setzt die Primary ID
	 * @param primary die neue Primary ID
	 */
	void setPrimary(P primary);
}
