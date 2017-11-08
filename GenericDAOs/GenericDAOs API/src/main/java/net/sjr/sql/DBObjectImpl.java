package net.sjr.sql;

/**
 * Created by Jan on 02.05.2017.
 */
public abstract class DBObjectImpl<P extends Number> implements DBObject<P> {
	private static final long serialVersionUID = -2203270558303148510L;
	private P primary;
	
	@Override
	public P getPrimary() {
		return primary;
	}
	
	@Override
	public void setPrimary(P primary) {
		if (primary != null && getPrimary() != null)
			throw new IllegalStateException("Die Primary ID darf nicht verändert werden!");
		this.primary = primary;
	}
}
