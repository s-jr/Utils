package net.sjr.sql;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Eine Klasse um zwei über Kreuztabellen verbundene Objekte zu beinhalten
 * @param <A> Typ des ersten Java Objektes
 * @param <PA> Typ des Primary Keys des ersten Java Objektes
 * @param <B> Typ des zweiten Java Objektes
 * @param <PB> Typ des Primary Keys des zweiten Java Objektes
 */
@SuppressWarnings("WeakerAccess")
public class Kreuz2Objekt<A extends DBObject<PA>, PA extends Number, B extends DBObject<PB>, PB extends Number> {
	protected final A a;
	protected final B b;
	
	/**
	 * Erstellt ein neues Kreuzobjekt
	 *
	 * @param a erstes verbundene Objekt
	 * @param b zweites verbundene Objekt
	 */
	public Kreuz2Objekt(final @Nullable A a, final @Nullable B b) {
		this.a = a;
		this.b = b;
	}
	
	@Override
	public boolean equals(final @Nullable Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Kreuz2Objekt<?, ?, ?, ?> that = (Kreuz2Objekt<?, ?, ?, ?>) o;
		return Objects.equals(a, that.a) &&
				Objects.equals(b, that.b);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(a, b);
	}
	
	@Override
	public @NotNull String toString() {
		return "Kreuz2Objekt{" +
				"a=" + a +
				", b=" + b +
				'}';
	}
}