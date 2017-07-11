package net.sjr.sql;

/**
 * Created by Jan on 11.07.2017.
 */
public class Kreuz3Objekt<A extends DBObject<PA>, PA extends Number, B extends DBObject<PB>, PB extends Number, C extends DBObject<PC>, PC extends Number> extends Kreuz2Objekt<A, PA, B, PB> {
	public final C c;

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
