package net.sjr.sql.rsloader;

import net.sjr.sql.DAOBaseInterface;
import net.sjr.sql.DBObject;
import net.sjr.sql.exceptions.UncheckedSQLException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.sql.*;

/**
 * Klasse, die sich um das Laden von Werten aus {@link ResultSet} kümmert in dem es sich z.B. die aktuelle Cursor Position merkt
 */
@SuppressWarnings("WeakerAccess")
public class RsLoader {
	private final ResultSet rs;
	private int pos = 1;
	private final DBObject[] loadedObjects;
	
	/**
	 * Erstellt einen RsLoader für das gegebene {@link ResultSet} und die bereits geladenen Objekte
	 *
	 * @param rs            das für die Datenbankabfragen zu nutzende {@link ResultSet}
	 * @param loadedObjects bereits geladene Objekte, welche bei {@link #nextDBObject(DAOBaseInterface) nextDBObject} Aufrufen genutzt werden
	 */
	public RsLoader(final @NotNull ResultSet rs, final DBObject... loadedObjects) {
		this.rs = rs;
		this.loadedObjects = loadedObjects;
	}
	
	/**
	 * Überspringt die nächste Spalte im ResultSet
	 * @return sich Selbst
	 */
	public @NotNull RsLoader skip() {
		return skip(1);
	}
	
	/**
	 * Überspringt die gegebene Anzahl an Spalten
	 * @param steps Anzahl an zu überspringenden Spalten
	 * @return sich selbst
	 */
	public @NotNull RsLoader skip(final int steps) {
		pos += steps;
		return this;
	}
	
	/**
	 * Gibt den {@link String} an der aktuellen Position zurück und geht eine Position weiter
	 * @return der aktuelle {@link String}
	 */
	public @Nullable String nextString() {
		try {
			return rs.getString(pos++);
		}
		catch (final SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}
	
	/**
	 * Gibt den {@code char} an der aktuellen Position zurück und geht eine Position weiter
	 *
	 * @return der aktuelle {@code char}
	 *
	 * @throws IllegalArgumentException wenn das Datenbankfeld null oder leer ist
	 */
	public char nextChar() {
		String value = nextString();
		if (value == null || value.isEmpty()) throw new IllegalArgumentException("Der String ist null oder leer!");
		return value.charAt(0);
	}
	
	/**
	 * Gibt den {@link Character} an der aktuellen Position zurück und geht eine Position weiter
	 *
	 * @return der aktuelle {@link Character}
	 */
	public @Nullable Character nextNullChar() {
		String value = nextString();
		if (value == null || value.isEmpty()) return null;
		return value.charAt(0);
	}
	
	/**
	 * Gibt den {@code boolean} an der aktuellen Position zurück und geht eine Position weiter
	 * @return der aktuelle {@code boolean} ({@code false}, wenn {@code null} in der Datenbank)
	 */
	public boolean nextBoolean() {
		try {
			return rs.getBoolean(pos++);
		}
		catch (final SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}
	
	/**
	 * Gibt den {@link Boolean} an der aktuellen Position zurück und geht eine Position weiter
	 * @return der aktuelle {@link Boolean}
	 */
	public @Nullable Boolean nextNullBoolean() {
		try {
			return RsUtils.getNullableBoolean(rs, pos++);
		}
		catch (final SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}
	
	/**
	 * Gibt den {@code byte} an der aktuellen Position zurück und geht eine Position weiter
	 * @return der aktuelle {@code byte} ({@code 0}, wenn {@code null} in der Datenbank)
	 */
	public byte nextByte() {
		try {
			return rs.getByte(pos++);
		}
		catch (final SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}
	
	/**
	 * Gibt den {@link Byte} an der aktuellen Position zurück und geht eine Position weiter
	 * @return der aktuelle {@link Byte}
	 */
	public @Nullable Byte nextNullByte() {
		try {
			return RsUtils.getNullableByte(rs, pos++);
		}
		catch (final SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}
	
	/**
	 * Gibt den {@code short} an der aktuellen Position zurück und geht eine Position weiter
	 * @return der aktuelle {@code short} ({@code 0}, wenn {@code null} in der Datenbank)
	 */
	public short nextShort() {
		try {
			return rs.getShort(pos++);
		}
		catch (final SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}
	
	/**
	 * Gibt den {@link Short} an der aktuellen Position zurück und geht eine Position weiter
	 * @return der aktuelle {@link Short}
	 */
	public @Nullable Short nextNullShort() {
		try {
			return RsUtils.getNullableShort(rs, pos++);
		}
		catch (final SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}
	
	/**
	 * Gibt den {@code int} an der aktuellen Position zurück und geht eine Position weiter
	 * @return der aktuelle {@code int} ({@code 0}, wenn {@code null} in der Datenbank)
	 */
	public int nextInt() {
		try {
			return rs.getInt(pos++);
		}
		catch (final SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}
	
	/**
	 * Gibt den {@link Integer} an der aktuellen Position zurück und geht eine Position weiter
	 * @return der aktuelle {@link Integer}
	 */
	public @Nullable Integer nextNullInt() {
		try {
			return RsUtils.getNullableInt(rs, pos++);
		}
		catch (final SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}
	
	/**
	 * Gibt den {@code long} an der aktuellen Position zurück und geht eine Position weiter
	 * @return der aktuelle {@code long} ({@code 0L}, wenn {@code null} in der Datenbank)
	 */
	public long nextLong() {
		try {
			return rs.getLong(pos++);
		}
		catch (final SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}
	
	/**
	 * Gibt den {@link Long} an der aktuellen Position zurück und geht eine Position weiter
	 * @return der aktuelle {@link Long}
	 */
	public @Nullable Long nextNullLong() {
		try {
			return RsUtils.getNullableLong(rs, pos++);
		}
		catch (final SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}
	
	/**
	 * Gibt den {@code float} an der aktuellen Position zurück und geht eine Position weiter
	 * @return der aktuelle {@code float} ({@code 0.0f}, wenn {@code null} in der Datenbank)
	 */
	public float nextFloat() {
		try {
			return rs.getFloat(pos++);
		}
		catch (final SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}
	
	/**
	 * Gibt den {@link Float} an der aktuellen Position zurück und geht eine Position weiter
	 * @return der aktuelle {@link Float}
	 */
	public @Nullable Float nextNullFloat() {
		try {
			return RsUtils.getNullableFloat(rs, pos++);
		}
		catch (final SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}
	
	/**
	 * Gibt den {@code double} an der aktuellen Position zurück und geht eine Position weiter
	 * @return der aktuelle {@code double} ({@code 0.0}, wenn {@code null} in der Datenbank)
	 */
	public double nextDouble() {
		try {
			return rs.getFloat(pos++);
		}
		catch (final SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}
	
	/**
	 * Gibt den {@link Double} an der aktuellen Position zurück und geht eine Position weiter
	 * @return der aktuelle {@link Double}
	 */
	public @Nullable Double nextNullDouble() {
		try {
			return RsUtils.getNullableDouble(rs, pos++);
		}
		catch (final SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}
	
	/**
	 * Gibt den {@link BigDecimal} an der aktuellen Position zurück und geht eine Position weiter
	 * @return der aktuelle {@link BigDecimal}
	 */
	public @Nullable BigDecimal nextBigDecimal() {
		try {
			return rs.getBigDecimal(pos++);
		}
		catch (final SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}
	
	/**
	 * Gibt das {@link Date} an der aktuellen Position zurück und geht eine Position weiter
	 * @return das aktuelle {@link Date}
	 */
	public @Nullable Date nextDate() {
		try {
			return rs.getDate(pos++);
		}
		catch (final SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}
	
	/**
	 * Gibt die {@link Time} an der aktuellen Position zurück und geht eine Position weiter
	 * @return die aktuelle {@link Time}
	 */
	public @Nullable Time nextTime() {
		try {
			return rs.getTime(pos++);
		}
		catch (final SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}
	
	/**
	 * Gibt den {@link Timestamp} an der aktuellen Position zurück und geht eine Position weiter
	 * @return der aktuelle {@link Timestamp}
	 */
	public @Nullable Timestamp nextTimestamp() {
		try {
			return rs.getTimestamp(pos++);
		}
		catch (final SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}
	
	/**
	 * Gibt das {@link java.util.Date} an der aktuellen Position zurück und geht eine Position weiter
	 * @return das aktuelle {@link java.util.Date}
	 */
	public @Nullable java.util.Date nextUtilDate() {
		Timestamp value = nextTimestamp();
		if (value == null) return null;
		return new java.util.Date(value.getTime());
	}
	
	/**
	 * Gibt das {@link DBObject} an der aktuellen Position zurück und geht eine Position weiter
	 * @param dao die {@link DAOBaseInterface}, welche wenn nötig zum Laden des Objektes genutzt werden soll
	 * @param <T> Typ des {@link DBObject}
	 * @param <P> Typ des Primary Keys des {@link DBObject}
	 * @return das aktuelle {@link DBObject}
	 */
	public @Nullable <T extends DBObject<P>, P extends Number> T nextDBObject(final @NotNull DAOBaseInterface<T, P> dao) {
		try {
			return RsUtils.loadedObjectsOrNull(pos++, rs, dao, loadedObjects);
		}
		catch (final SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}
}
