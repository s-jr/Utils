package net.sjr.sql;

/**
 * Eine Klasse um zwei über Kreuztabellen verbundene Objekte zu beinhalten
 * @param <A> Typ des ersten Java Objektes
 * @param <PA> Typ des Primary Keys des ersten Java Objektes
 * @param <B> Typ des zweiten Java Objektes
 * @param <PB> Typ des Primary Keys des zweiten Java Objektes
 */
public class Kreuz2Objekt<A extends DBObject<PA>, PA extends Number, B extends DBObject<PB>, PB extends Number> {
	public final A a;
	public final B b;
	
	/**
	 * Erstellt ein neues Kreuzobjekt
	 *
	 * @param a erstes verbundene Objekt
	 * @param b zweites verbundene Objekt
	 */
	public Kreuz2Objekt(A a, B b) {
		this.a = a;
		this.b = b;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Kreuz2Objekt<?, ?, ?, ?> that = (Kreuz2Objekt<?, ?, ?, ?>) o;

		if (a != null ? !a.equals(that.a) : that.a != null) return false;
		return b != null ? b.equals(that.b) : that.b == null;
	}

	@Override
	public int hashCode() {
		int result = a != null ? a.hashCode() : 0;
		result = 31 * result + (b != null ? b.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Kreuz2Objekt{" +
				"a=" + a +
				", b=" + b +
				'}';
	}
}