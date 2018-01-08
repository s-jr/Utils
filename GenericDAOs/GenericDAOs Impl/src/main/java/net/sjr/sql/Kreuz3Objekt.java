package net.sjr.sql;

/**
 * Eine Klasse um drei über Kreuztabellen verbundene Objekte zu beinhalten
 * @param <A> Typ des ersten Java Objektes
 * @param <PA> Typ des Primary Keys des ersten Java Objektes
 * @param <B> Typ des zweiten Java Objektes
 * @param <PB> Typ des Primary Keys des zweiten Java Objektes
 * @param <C> Typ des dritten Java Objektes
 * @param <PC> Typ des Primary Keys des dritten Java Objektes
 */
@SuppressWarnings("WeakerAccess")
public class Kreuz3Objekt<A extends DBObject<PA>, PA extends Number, B extends DBObject<PB>, PB extends Number, C extends DBObject<PC>, PC extends Number> extends Kreuz2Objekt<A, PA, B, PB> {
	protected final C c;
	
	/**
	 * Erstellt ein neues Kreuzobjekt
	 *
	 * @param a erstes verbundene Objekt
	 * @param b zweites verbundene Objekt
	 * @param c drittes verbundene Objekt
	 */
	public Kreuz3Objekt(A a, B b, C c) {
		super(a, b);
		this.c = c;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		Kreuz3Objekt<?, ?, ?, ?, ?, ?> that = (Kreuz3Objekt<?, ?, ?, ?, ?, ?>) o;

		return c != null ? c.equals(that.c) : that.c == null;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (c != null ? c.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Kreuz3Objekt{" +
				"a=" + a +
				", b=" + b +
				", c=" + c +
				'}';
	}
}
