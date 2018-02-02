package net.sjr.sql;


import org.jetbrains.annotations.Nullable;

/**
 * Implementiert das {@link DBObject} Interface indem ein Attribut inkl. getter und setter bereitgestellt wird
 *
 * @param <P> der Typ des Primary Keys
 */
public abstract class DBObjectImpl<P extends Number> implements DBObject<P> {
	private static final long serialVersionUID = -2203270558303148510L;
	private P primary;
	
	/**
	 * gibt die Primary ID zurück
	 *
	 * @return die Primary ID
	 */
	@Override
	public @Nullable P getPrimary() {
		return primary;
	}
	
	/**
	 * setzt die Primary ID
	 *
	 * @param primary die neue Primary ID
	 * @throws IllegalStateException wenn bereits eine Primary ID vorhanden ist und etwas anderes außer {@code null} versucht wird zu setzten
	 */
	@Override
	public void setPrimary(final @Nullable P primary) {
		if (primary != null && getPrimary() != null)
			throw new IllegalStateException("Die Primary ID darf nicht verändert werden!");
		this.primary = primary;
	}
}
