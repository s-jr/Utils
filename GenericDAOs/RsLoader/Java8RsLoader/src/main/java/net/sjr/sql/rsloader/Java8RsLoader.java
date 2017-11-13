package net.sjr.sql.rsloader;

import net.sjr.converterutils.Java8ConverterUtils;
import net.sjr.sql.DAOBase;
import net.sjr.sql.DBColumn;
import net.sjr.sql.DBEnum;
import net.sjr.sql.DBObject;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Erweitert den {@link RsLoader} um die Java 8 Time Klassen und die Möglichkeit die Werte via Funktionaler Programmierung zu füllen
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class Java8RsLoader extends RsLoader {
	
	/**
	 * Überspringt die nächste Spalte im {@link ResultSet}
	 *
	 * @return sich Selbst
	 */
	@Override
	public Java8RsLoader skip() {
		return (Java8RsLoader) super.skip();
	}
	
	/**
	 * Überspringt die gegebene Anzahl an Spalten
	 *
	 * @param steps Anzahl an zu überspringenden Spalten
	 *
	 * @return sich selbst
	 */
	@Override
	public Java8RsLoader skip(final int steps) {
		return (Java8RsLoader) super.skip(steps);
	}
	
	/**
	 * Überspringt die nächste Spalte im {@link ResultSet} und gibt den Wert aus dem getter direkt in den setter weiter
	 * Sinnvoll chaining trotz eigener get und set implementierung
	 *
	 * @param getter der eigene Getter
	 * @param setter der eigene Setter
	 *
	 * @return sich Selbst
	 */
	public <T> Java8RsLoader skip(Supplier<T> getter, Consumer<T> setter) {
		setter.accept(getter.get());
		return skip();
	}
	
	/**
	 * Überspringt die gegebene Anzahl an Spalten im {@link ResultSet} und gibt den Wert aus dem getter direkt in den setter weiter
	 * Sinnvoll chaining trotz eigener get und set implementierung
	 *
	 * @param getter der eigene Getter
	 * @param setter der eigene Setter
	 * @param steps  Anzahl an zu überspringenden Spalten
	 *
	 * @return sich Selbst
	 */
	public <T> Java8RsLoader skip(Supplier<T> getter, Consumer<T> setter, final int steps) {
		setter.accept(getter.get());
		return skip(steps);
	}
	
	/**
	 * Erstellt einen {@link Java8RsLoader} für das gegebene {@link ResultSet} und die bereits geladenen Objekte
	 *
	 * @param rs            das für die Datenbankabfragen zu nutzende {@link ResultSet}
	 * @param loadedObjects bereits geladene Objekte, welche bei {@link #nextDBObject(DAOBase) nextDBObject} Aufrufen genutzt werden
	 */
	public Java8RsLoader(final ResultSet rs, final DBObject... loadedObjects) {
		super(rs, loadedObjects);
	}
	
	/**
	 * Lädt den {@link String} an der aktuellen Position, gibt ihn in die Setter Funktion und geht eine Position weiter
	 *
	 * @param setter die Funktion, in die der Wert reingegeben werden soll
	 *
	 * @return sich Selbst
	 */
	public Java8RsLoader nextString(Consumer<String> setter) {
		return nextString(setter, Function.identity());
	}
	
	/**
	 * Lädt den {@link String} an der aktuellen Position, gibt ihn in die mapper Funktion und das Ergebnis dann in die Setter Funktion und geht eine Position weiter
	 *
	 * @param setter die Funktion, in die der finale Wert reingegeben werden soll
	 * @param mapper Funktion, die zwischen laden des Wertes und setter steht
	 * @param <T>    Ergebnistyp des mappers und somit Parametertyp des setters
	 *
	 * @return sich Selbst
	 */
	public <T> Java8RsLoader nextString(Consumer<T> setter, Function<String, T> mapper) {
		setter.accept(mapper.apply(nextString()));
		return this;
	}
	
	/**
	 * Lädt den {@code char} an der aktuellen Position, gibt ihn in die Setter Funktion und geht eine Position weiter
	 *
	 * @param setter die Funktion, in die der Wert reingegeben werden soll
	 *
	 * @return sich Selbst
	 *
	 * @throws IllegalArgumentException wenn das Datenbankfeld null oder leer ist
	 */
	public Java8RsLoader nextChar(Consumer<Character> setter) {
		return nextChar(setter, Function.identity());
	}
	
	/**
	 * Lädt den {@code char} an der aktuellen Position, gibt ihn in die mapper Funktion und das Ergebnis dann in die Setter Funktion und geht eine Position weiter
	 *
	 * @param setter die Funktion, in die der finale Wert reingegeben werden soll
	 * @param mapper Funktion, die zwischen laden des Wertes und setter steht
	 * @param <T>    Ergebnistyp des mappers und somit Parametertyp des setters
	 *
	 * @return sich Selbst
	 *
	 * @throws IllegalArgumentException wenn das Datenbankfeld null oder leer ist
	 */
	public <T> Java8RsLoader nextChar(Consumer<T> setter, Function<Character, T> mapper) {
		setter.accept(mapper.apply(nextChar()));
		return this;
	}
	
	/**
	 * Lädt den {@link Character} an der aktuellen Position, gibt ihn in die Setter Funktion und geht eine Position weiter
	 *
	 * @param setter die Funktion, in die der Wert reingegeben werden soll
	 *
	 * @return sich Selbst
	 */
	public Java8RsLoader nextNullChar(Consumer<Character> setter) {
		return nextNullChar(setter, Function.identity());
	}
	
	/**
	 * Lädt den {@link Character} an der aktuellen Position, gibt ihn in die mapper Funktion und das Ergebnis dann in die Setter Funktion und geht eine Position weiter
	 *
	 * @param setter die Funktion, in die der finale Wert reingegeben werden soll
	 * @param mapper Funktion, die zwischen laden des Wertes und setter steht
	 * @param <T>    Ergebnistyp des mappers und somit Parametertyp des setters
	 *
	 * @return sich Selbst
	 */
	public <T> Java8RsLoader nextNullChar(Consumer<T> setter, Function<Character, T> mapper) {
		setter.accept(mapper.apply(nextNullChar()));
		return this;
	}
	
	/**
	 * Lädt den {@code boolean} an der aktuellen Position, gibt ihn in die Setter Funktion und geht eine Position weiter
	 *
	 * @param setter die Funktion, in die der Wert reingegeben werden soll
	 *
	 * @return sich Selbst
	 */
	public Java8RsLoader nextBoolean(Consumer<Boolean> setter) {
		return nextBoolean(setter, Function.identity());
	}
	
	/**
	 * Lädt den {@code boolean} an der aktuellen Position, gibt ihn in die mapper Funktion und das Ergebnis dann in die Setter Funktion und geht eine Position weiter
	 *
	 * @param setter die Funktion, in die der finale Wert reingegeben werden soll
	 * @param mapper Funktion, die zwischen laden des Wertes und setter steht
	 * @param <T>    Ergebnistyp des mappers und somit Parametertyp des setters
	 *
	 * @return sich Selbst
	 */
	public <T> Java8RsLoader nextBoolean(Consumer<T> setter, Function<Boolean, T> mapper) {
		setter.accept(mapper.apply(nextBoolean()));
		return this;
	}
	
	/**
	 * Lädt den {@link Boolean} an der aktuellen Position, gibt ihn in die Setter Funktion und geht eine Position weiter
	 *
	 * @param setter die Funktion, in die der Wert reingegeben werden soll
	 *
	 * @return sich Selbst
	 */
	public Java8RsLoader nextNullBoolean(Consumer<Boolean> setter) {
		return nextNullBoolean(setter, Function.identity());
	}
	
	/**
	 * Lädt den {@link Boolean} an der aktuellen Position, gibt ihn in die mapper Funktion und das Ergebnis dann in die Setter Funktion und geht eine Position weiter
	 *
	 * @param setter die Funktion, in die der finale Wert reingegeben werden soll
	 * @param mapper Funktion, die zwischen laden des Wertes und setter steht
	 * @param <T>    Ergebnistyp des mappers und somit Parametertyp des setters
	 *
	 * @return sich Selbst
	 */
	public <T> Java8RsLoader nextNullBoolean(Consumer<T> setter, Function<Boolean, T> mapper) {
		setter.accept(mapper.apply(nextNullBoolean()));
		return this;
	}
	
	/**
	 * Lädt den {@code byte} an der aktuellen Position, gibt ihn in die Setter Funktion und geht eine Position weiter
	 *
	 * @param setter die Funktion, in die der Wert reingegeben werden soll
	 *
	 * @return sich Selbst
	 */
	public Java8RsLoader nextByte(Consumer<Byte> setter) {
		return nextByte(setter, Function.identity());
	}
	
	/**
	 * Lädt den {@code byte} an der aktuellen Position, gibt ihn in die mapper Funktion und das Ergebnis dann in die Setter Funktion und geht eine Position weiter
	 *
	 * @param setter die Funktion, in die der finale Wert reingegeben werden soll
	 * @param mapper Funktion, die zwischen laden des Wertes und setter steht
	 * @param <T>    Ergebnistyp des mappers und somit Parametertyp des setters
	 *
	 * @return sich Selbst
	 */
	public <T> Java8RsLoader nextByte(Consumer<T> setter, Function<Byte, T> mapper) {
		setter.accept(mapper.apply(nextByte()));
		return this;
	}
	
	/**
	 * Lädt den {@link Byte} an der aktuellen Position, gibt ihn in die Setter Funktion und geht eine Position weiter
	 *
	 * @param setter die Funktion, in die der Wert reingegeben werden soll
	 *
	 * @return sich Selbst
	 */
	public Java8RsLoader nextNullByte(Consumer<Byte> setter) {
		return nextNullByte(setter, Function.identity());
	}
	
	/**
	 * Lädt den {@link Byte} an der aktuellen Position, gibt ihn in die mapper Funktion und das Ergebnis dann in die Setter Funktion und geht eine Position weiter
	 *
	 * @param setter die Funktion, in die der finale Wert reingegeben werden soll
	 * @param mapper Funktion, die zwischen laden des Wertes und setter steht
	 * @param <T>    Ergebnistyp des mappers und somit Parametertyp des setters
	 *
	 * @return sich Selbst
	 */
	public <T> Java8RsLoader nextNullByte(Consumer<T> setter, Function<Byte, T> mapper) {
		setter.accept(mapper.apply(nextNullByte()));
		return this;
	}
	
	/**
	 * Lädt den {@code short} an der aktuellen Position, gibt ihn in die Setter Funktion und geht eine Position weiter
	 *
	 * @param setter die Funktion, in die der Wert reingegeben werden soll
	 *
	 * @return sich Selbst
	 */
	public Java8RsLoader nextShort(Consumer<Short> setter) {
		return nextShort(setter, Function.identity());
	}
	
	/**
	 * Lädt den {@code short} an der aktuellen Position, gibt ihn in die mapper Funktion und das Ergebnis dann in die Setter Funktion und geht eine Position weiter
	 *
	 * @param setter die Funktion, in die der finale Wert reingegeben werden soll
	 * @param mapper Funktion, die zwischen laden des Wertes und setter steht
	 * @param <T>    Ergebnistyp des mappers und somit Parametertyp des setters
	 *
	 * @return sich Selbst
	 */
	public <T> Java8RsLoader nextShort(Consumer<T> setter, Function<Short, T> mapper) {
		setter.accept(mapper.apply(nextShort()));
		return this;
	}
	
	/**
	 * Lädt den {@link Short} an der aktuellen Position, gibt ihn in die Setter Funktion und geht eine Position weiter
	 *
	 * @param setter die Funktion, in die der Wert reingegeben werden soll
	 *
	 * @return sich Selbst
	 */
	public Java8RsLoader nextNullShort(Consumer<Short> setter) {
		return nextNullShort(setter, Function.identity());
	}
	
	/**
	 * Lädt den {@link Short} an der aktuellen Position, gibt ihn in die mapper Funktion und das Ergebnis dann in die Setter Funktion und geht eine Position weiter
	 *
	 * @param setter die Funktion, in die der finale Wert reingegeben werden soll
	 * @param mapper Funktion, die zwischen laden des Wertes und setter steht
	 * @param <T>    Ergebnistyp des mappers und somit Parametertyp des setters
	 *
	 * @return sich Selbst
	 */
	public <T> Java8RsLoader nextNullShort(Consumer<T> setter, Function<Short, T> mapper) {
		setter.accept(mapper.apply(nextNullShort()));
		return this;
	}
	
	/**
	 * Lädt den {@code int} an der aktuellen Position, gibt ihn in die Setter Funktion und geht eine Position weiter
	 *
	 * @param setter die Funktion, in die der Wert reingegeben werden soll
	 *
	 * @return sich Selbst
	 */
	public Java8RsLoader nextInt(Consumer<Integer> setter) {
		return nextInt(setter, Function.identity());
	}
	
	/**
	 * Lädt den {@code int} an der aktuellen Position, gibt ihn in die mapper Funktion und das Ergebnis dann in die Setter Funktion und geht eine Position weiter
	 *
	 * @param setter die Funktion, in die der finale Wert reingegeben werden soll
	 * @param mapper Funktion, die zwischen laden des Wertes und setter steht
	 * @param <T>    Ergebnistyp des mappers und somit Parametertyp des setters
	 *
	 * @return sich Selbst
	 */
	public <T> Java8RsLoader nextInt(Consumer<T> setter, Function<Integer, T> mapper) {
		setter.accept(mapper.apply(nextInt()));
		return this;
	}
	
	/**
	 * Lädt den {@link Integer} an der aktuellen Position, gibt ihn in die Setter Funktion und geht eine Position weiter
	 *
	 * @param setter die Funktion, in die der Wert reingegeben werden soll
	 *
	 * @return sich Selbst
	 */
	public Java8RsLoader nextNullInt(Consumer<Integer> setter) {
		return nextNullInt(setter, Function.identity());
	}
	
	/**
	 * Lädt den {@link Integer} an der aktuellen Position, gibt ihn in die mapper Funktion und das Ergebnis dann in die Setter Funktion und geht eine Position weiter
	 *
	 * @param setter die Funktion, in die der finale Wert reingegeben werden soll
	 * @param mapper Funktion, die zwischen laden des Wertes und setter steht
	 * @param <T>    Ergebnistyp des mappers und somit Parametertyp des setters
	 *
	 * @return sich Selbst
	 */
	public <T> Java8RsLoader nextNullInt(Consumer<T> setter, Function<Integer, T> mapper) {
		setter.accept(mapper.apply(nextNullInt()));
		return this;
	}
	
	/**
	 * Lädt den {@code long} an der aktuellen Position, gibt ihn in die Setter Funktion und geht eine Position weiter
	 *
	 * @param setter die Funktion, in die der Wert reingegeben werden soll
	 *
	 * @return sich Selbst
	 */
	public Java8RsLoader nextLong(Consumer<Long> setter) {
		return nextLong(setter, Function.identity());
	}
	
	/**
	 * Lädt den {@code long} an der aktuellen Position, gibt ihn in die mapper Funktion und das Ergebnis dann in die Setter Funktion und geht eine Position weiter
	 *
	 * @param setter die Funktion, in die der finale Wert reingegeben werden soll
	 * @param mapper Funktion, die zwischen laden des Wertes und setter steht
	 * @param <T>    Ergebnistyp des mappers und somit Parametertyp des setters
	 *
	 * @return sich Selbst
	 */
	public <T> Java8RsLoader nextLong(Consumer<T> setter, Function<Long, T> mapper) {
		setter.accept(mapper.apply(nextLong()));
		return this;
	}
	
	/**
	 * Lädt den {@link Long} an der aktuellen Position, gibt ihn in die Setter Funktion und geht eine Position weiter
	 *
	 * @param setter die Funktion, in die der Wert reingegeben werden soll
	 *
	 * @return sich Selbst
	 */
	public Java8RsLoader nextNullLong(Consumer<Long> setter) {
		return nextNullLong(setter, Function.identity());
	}
	
	/**
	 * Lädt den {@link Long} an der aktuellen Position, gibt ihn in die mapper Funktion und das Ergebnis dann in die Setter Funktion und geht eine Position weiter
	 *
	 * @param setter die Funktion, in die der finale Wert reingegeben werden soll
	 * @param mapper Funktion, die zwischen laden des Wertes und setter steht
	 * @param <T>    Ergebnistyp des mappers und somit Parametertyp des setters
	 *
	 * @return sich Selbst
	 */
	public <T> Java8RsLoader nextNullLong(Consumer<T> setter, Function<Long, T> mapper) {
		setter.accept(mapper.apply(nextNullLong()));
		return this;
	}
	
	/**
	 * Lädt den {@code float} an der aktuellen Position, gibt ihn in die Setter Funktion und geht eine Position weiter
	 *
	 * @param setter die Funktion, in die der Wert reingegeben werden soll
	 *
	 * @return sich Selbst
	 */
	public Java8RsLoader nextFloat(Consumer<Float> setter) {
		return nextFloat(setter, Function.identity());
	}
	
	/**
	 * Lädt den {@code float} an der aktuellen Position, gibt ihn in die mapper Funktion und das Ergebnis dann in die Setter Funktion und geht eine Position weiter
	 *
	 * @param setter die Funktion, in die der finale Wert reingegeben werden soll
	 * @param mapper Funktion, die zwischen laden des Wertes und setter steht
	 * @param <T>    Ergebnistyp des mappers und somit Parametertyp des setters
	 *
	 * @return sich Selbst
	 */
	public <T> Java8RsLoader nextFloat(Consumer<T> setter, Function<Float, T> mapper) {
		setter.accept(mapper.apply(nextFloat()));
		return this;
	}
	
	/**
	 * Lädt den {@link Float} an der aktuellen Position, gibt ihn in die Setter Funktion und geht eine Position weiter
	 *
	 * @param setter die Funktion, in die der Wert reingegeben werden soll
	 *
	 * @return sich Selbst
	 */
	public Java8RsLoader nextNullFloat(Consumer<Float> setter) {
		return nextNullFloat(setter, Function.identity());
	}
	
	/**
	 * Lädt den {@link Float} an der aktuellen Position, gibt ihn in die mapper Funktion und das Ergebnis dann in die Setter Funktion und geht eine Position weiter
	 *
	 * @param setter die Funktion, in die der finale Wert reingegeben werden soll
	 * @param mapper Funktion, die zwischen laden des Wertes und setter steht
	 * @param <T>    Ergebnistyp des mappers und somit Parametertyp des setters
	 *
	 * @return sich Selbst
	 */
	public <T> Java8RsLoader nextNullFloat(Consumer<T> setter, Function<Float, T> mapper) {
		setter.accept(mapper.apply(nextNullFloat()));
		return this;
	}
	
	/**
	 * Lädt den {@code double} an der aktuellen Position, gibt ihn in die Setter Funktion und geht eine Position weiter
	 *
	 * @param setter die Funktion, in die der Wert reingegeben werden soll
	 *
	 * @return sich Selbst
	 */
	public Java8RsLoader nextDouble(Consumer<Double> setter) {
		return nextDouble(setter, Function.identity());
	}
	
	/**
	 * Lädt den {@code double} an der aktuellen Position, gibt ihn in die mapper Funktion und das Ergebnis dann in die Setter Funktion und geht eine Position weiter
	 *
	 * @param setter die Funktion, in die der finale Wert reingegeben werden soll
	 * @param mapper Funktion, die zwischen laden des Wertes und setter steht
	 * @param <T>    Ergebnistyp des mappers und somit Parametertyp des setters
	 *
	 * @return sich Selbst
	 */
	public <T> Java8RsLoader nextDouble(Consumer<T> setter, Function<Double, T> mapper) {
		setter.accept(mapper.apply(nextDouble()));
		return this;
	}
	
	/**
	 * Lädt den {@link Double} an der aktuellen Position, gibt ihn in die Setter Funktion und geht eine Position weiter
	 *
	 * @param setter die Funktion, in die der Wert reingegeben werden soll
	 *
	 * @return sich Selbst
	 */
	public Java8RsLoader nextNullDouble(Consumer<Double> setter) {
		return nextNullDouble(setter, Function.identity());
	}
	
	/**
	 * Lädt den {@link Double} an der aktuellen Position, gibt ihn in die mapper Funktion und das Ergebnis dann in die Setter Funktion und geht eine Position weiter
	 *
	 * @param setter die Funktion, in die der finale Wert reingegeben werden soll
	 * @param mapper Funktion, die zwischen laden des Wertes und setter steht
	 * @param <T>    Ergebnistyp des mappers und somit Parametertyp des setters
	 *
	 * @return sich Selbst
	 */
	public <T> Java8RsLoader nextNullDouble(Consumer<T> setter, Function<Double, T> mapper) {
		setter.accept(mapper.apply(nextNullDouble()));
		return this;
	}
	
	/**
	 * Lädt den {@link BigDecimal} an der aktuellen Position, gibt ihn in die Setter Funktion und geht eine Position weiter
	 *
	 * @param setter die Funktion, in die der Wert reingegeben werden soll
	 *
	 * @return sich Selbst
	 */
	public Java8RsLoader nextBigDecimal(Consumer<BigDecimal> setter) {
		return nextBigDecimal(setter, Function.identity());
	}
	
	/**
	 * Lädt den {@link BigDecimal} an der aktuellen Position, gibt ihn in die mapper Funktion und das Ergebnis dann in die Setter Funktion und geht eine Position weiter
	 *
	 * @param setter die Funktion, in die der finale Wert reingegeben werden soll
	 * @param mapper Funktion, die zwischen laden des Wertes und setter steht
	 * @param <T>    Ergebnistyp des mappers und somit Parametertyp des setters
	 *
	 * @return sich Selbst
	 */
	public <T> Java8RsLoader nextBigDecimal(Consumer<T> setter, Function<BigDecimal, T> mapper) {
		setter.accept(mapper.apply(nextBigDecimal()));
		return this;
	}
	
	/**
	 * Lädt das {@link Date} an der aktuellen Position, gibt es in die Setter Funktion und geht eine Position weiter
	 *
	 * @param setter die Funktion, in die der Wert reingegeben werden soll
	 *
	 * @return sich Selbst
	 */
	public Java8RsLoader nextDate(Consumer<Date> setter) {
		return nextDate(setter, Function.identity());
	}
	
	/**
	 * Lädt das {@link Date} an der aktuellen Position, gibt ihn es die mapper Funktion und das Ergebnis dann in die Setter Funktion und geht eine Position weiter
	 *
	 * @param setter die Funktion, in die der finale Wert reingegeben werden soll
	 * @param mapper Funktion, die zwischen laden des Wertes und setter steht
	 * @param <T>    Ergebnistyp des mappers und somit Parametertyp des setters
	 *
	 * @return sich Selbst
	 */
	public <T> Java8RsLoader nextDate(Consumer<T> setter, Function<Date, T> mapper) {
		setter.accept(mapper.apply(nextDate()));
		return this;
	}
	
	/**
	 * Lädt die {@link Time} an der aktuellen Position, gibt sie in die Setter Funktion und geht eine Position weiter
	 *
	 * @param setter die Funktion, in die der Wert reingegeben werden soll
	 *
	 * @return sich Selbst
	 */
	public Java8RsLoader nextTime(Consumer<Time> setter) {
		return nextTime(setter, Function.identity());
	}
	
	/**
	 * Lädt die {@link Time} an der aktuellen Position, gibt sie in die mapper Funktion und das Ergebnis dann in die Setter Funktion und geht eine Position weiter
	 *
	 * @param setter die Funktion, in die der finale Wert reingegeben werden soll
	 * @param mapper Funktion, die zwischen laden des Wertes und setter steht
	 * @param <T>    Ergebnistyp des mappers und somit Parametertyp des setters
	 *
	 * @return sich Selbst
	 */
	public <T> Java8RsLoader nextTime(Consumer<T> setter, Function<Time, T> mapper) {
		setter.accept(mapper.apply(nextTime()));
		return this;
	}
	
	/**
	 * Lädt den {@link Timestamp} an der aktuellen Position, gibt ihn in die Setter Funktion und geht eine Position weiter
	 *
	 * @param setter die Funktion, in die der Wert reingegeben werden soll
	 *
	 * @return sich Selbst
	 */
	public Java8RsLoader nextTimestamp(Consumer<Timestamp> setter) {
		return nextTimestamp(setter, Function.identity());
	}
	
	/**
	 * Lädt den {@link Timestamp} an der aktuellen Position, gibt ihn in die mapper Funktion und das Ergebnis dann in die Setter Funktion und geht eine Position weiter
	 *
	 * @param setter die Funktion, in die der finale Wert reingegeben werden soll
	 * @param mapper Funktion, die zwischen laden des Wertes und setter steht
	 * @param <T>    Ergebnistyp des mappers und somit Parametertyp des setters
	 *
	 * @return sich Selbst
	 */
	public <T> Java8RsLoader nextTimestamp(Consumer<T> setter, Function<Timestamp, T> mapper) {
		setter.accept(mapper.apply(nextTimestamp()));
		return this;
	}
	
	/**
	 * Lädt das {@link java.util.Date} an der aktuellen Position, gibt es in die Setter Funktion und geht eine Position weiter
	 *
	 * @param setter die Funktion, in die der Wert reingegeben werden soll
	 *
	 * @return sich Selbst
	 */
	public Java8RsLoader nextUtilDate(Consumer<java.util.Date> setter) {
		return nextUtilDate(setter, Function.identity());
	}
	
	/**
	 * Lädt das {@link java.util.Date} an der aktuellen Position, gibt es in die mapper Funktion und das Ergebnis dann in die Setter Funktion und geht eine Position weiter
	 *
	 * @param setter die Funktion, in die der finale Wert reingegeben werden soll
	 * @param mapper Funktion, die zwischen laden des Wertes und setter steht
	 * @param <T>    Ergebnistyp des mappers und somit Parametertyp des setters
	 *
	 * @return sich Selbst
	 */
	public <T> Java8RsLoader nextUtilDate(Consumer<T> setter, Function<java.util.Date, T> mapper) {
		setter.accept(mapper.apply(nextUtilDate()));
		return this;
	}
	
	/**
	 * Lädt das {@link DBObject} an der aktuellen Position, gibt ihn in die Setter Funktion und geht eine Position weiter
	 *
	 * @param dao    die {@link DAOBase} die das Objekt zur Not laden soll
	 * @param setter die Funktion, in die der Wert reingegeben werden soll
	 * @param <T>    der Typ des Objektes
	 * @param <P>    der Typ des Primary Keys
	 *
	 * @return sich Selbst
	 */
	public <T extends DBObject<P>, P extends Number> Java8RsLoader nextDBObject(final DAOBase<T, P> dao, Consumer<T> setter) {
		return nextDBObject(dao, setter, Function.identity());
	}
	
	/**
	 * Lädt das {@link DBObject} an der aktuellen Position, gibt ihn in die mapper Funktion und das Ergebnis dann in die Setter Funktion und geht eine Position weiter
	 *
	 * @param dao    die {@link DAOBase} die das Objekt zur Not laden soll
	 * @param setter die Funktion, in die der finale Wert reingegeben werden soll
	 * @param mapper Funktion, die zwischen laden des Wertes und setter steht
	 * @param <T>    der Typ des Objektes
	 * @param <P>    der Typ des Primary Keys
	 * @param <R>    Ergebnistyp des mappers und somit Parametertyp des setters
	 *
	 * @return sich Selbst
	 */
	public <T extends DBObject<P>, P extends Number, R> Java8RsLoader nextDBObject(final DAOBase<T, P> dao, Consumer<R> setter, Function<T, R> mapper) {
		setter.accept(mapper.apply(nextDBObject(dao)));
		return this;
	}
	
	/**
	 * Gibt das {@link LocalDate} an der aktuellen Position zurück und geht eine Position weiter
	 *
	 * @return das aktuelle {@link LocalDate}
	 */
	public LocalDate nextLocalDate() {
		return Java8ConverterUtils.SQLDate.sqlDateToLocalDate(nextDate());
	}
	
	/**
	 * Lädt das {@link LocalDate} an der aktuellen Position, gibt ihn in die Setter Funktion und geht eine Position weiter
	 *
	 * @param setter die Funktion, in die der Wert reingegeben werden soll
	 *
	 * @return sich Selbst
	 */
	public Java8RsLoader nextLocalDate(Consumer<LocalDate> setter) {
		return nextLocalDate(setter, Function.identity());
	}
	
	/**
	 * Lädt das {@link LocalDate} an der aktuellen Position, gibt ihn in die mapper Funktion und das Ergebnis dann in die Setter Funktion und geht eine Position weiter
	 *
	 * @param setter die Funktion, in die der finale Wert reingegeben werden soll
	 * @param mapper Funktion, die zwischen laden des Wertes und setter steht
	 * @param <T>    Ergebnistyp des mappers und somit Parametertyp des setters
	 *
	 * @return sich Selbst
	 */
	public <T> Java8RsLoader nextLocalDate(Consumer<T> setter, Function<LocalDate, T> mapper) {
		setter.accept(mapper.apply(nextLocalDate()));
		return this;
	}
	
	/**
	 * Gibt die {@link LocalDateTime} an der aktuellen Position zurück und geht eine Position weiter
	 *
	 * @return die aktuelle {@link LocalDateTime}
	 */
	public LocalDateTime nextLocalDateTime() {
		return Java8ConverterUtils.SQLDate.timestampToLocalDateTime(nextTimestamp());
	}
	
	/**
	 * Lädt die {@link LocalDateTime} an der aktuellen Position, gibt ihn in die Setter Funktion und geht eine Position weiter
	 *
	 * @param setter die Funktion, in die der Wert reingegeben werden soll
	 *
	 * @return sich Selbst
	 */
	public Java8RsLoader nextLocalDateTime(Consumer<LocalDateTime> setter) {
		return nextLocalDateTime(setter, Function.identity());
	}
	
	/**
	 * Lädt das {@link LocalDateTime} an der aktuellen Position, gibt ihn in die mapper Funktion und das Ergebnis dann in die Setter Funktion und geht eine Position weiter
	 *
	 * @param setter die Funktion, in die der finale Wert reingegeben werden soll
	 * @param mapper Funktion, die zwischen laden des Wertes und setter steht
	 * @param <T>    Ergebnistyp des mappers und somit Parametertyp des setters
	 *
	 * @return sich Selbst
	 */
	public <T> Java8RsLoader nextLocalDateTime(Consumer<T> setter, Function<LocalDateTime, T> mapper) {
		setter.accept(mapper.apply(nextLocalDateTime()));
		return this;
	}
	
	/**
	 * Gibt die {@link LocalTime} an der aktuellen Position zurück und geht eine Position weiter
	 *
	 * @return die aktuelle {@link LocalDateTime}
	 */
	public LocalTime nextLocalTime() {
		return Java8ConverterUtils.SQLDate.sqlTimeToLocalTime(nextTime());
	}
	
	/**
	 * Lädt die {@link LocalTime} an der aktuellen Position, gibt ihn in die Setter Funktion und geht eine Position weiter
	 *
	 * @param setter die Funktion, in die der Wert reingegeben werden soll
	 *
	 * @return sich Selbst
	 */
	public Java8RsLoader nextLocalTime(Consumer<LocalTime> setter) {
		return nextLocalTime(setter, Function.identity());
	}
	
	/**
	 * Lädt das {@link LocalTime} an der aktuellen Position, gibt ihn in die mapper Funktion und das Ergebnis dann in die Setter Funktion und geht eine Position weiter
	 *
	 * @param setter die Funktion, in die der finale Wert reingegeben werden soll
	 * @param mapper Funktion, die zwischen laden des Wertes und setter steht
	 * @param <T>    Ergebnistyp des mappers und somit Parametertyp des setters
	 *
	 * @return sich Selbst
	 */
	public <T> Java8RsLoader nextLocalTime(Consumer<T> setter, Function<LocalTime, T> mapper) {
		setter.accept(mapper.apply(nextLocalTime()));
		return this;
	}
	
	/**
	 * Gibt das {@link DBEnum} an der aktuellen Position zurück und geht eine Position weiter
	 *
	 * @param getter    getter, der genutzt werden soll um den DBIdentifier zu laden
	 * @param enumClass die Klasse des Enums, welches zurückgegeben werden soll
	 * @param <E>       der Typ des Enums
	 * @param <T>       der Typ des DBIdentifiers des Enums
	 *
	 * @return das {@link DBEnum}
	 */
	public <E extends DBEnum<T>, T> E nextDBEnum(Supplier<T> getter, Class<E> enumClass) {
		return RsUtils.getFromDBIdentifier(getter.get(), enumClass);
	}
	
	/**
	 * Lädt den {@link DBEnum} an der aktuellen Position, gibt ihn in die Setter Funktion und geht eine Position weiter
	 *
	 * @param getter    getter, der genutzt werden soll um den DBIdentifier zu laden
	 * @param enumClass die Klasse des Enums, welches zurückgegeben werden soll
	 * @param setter    die Funktion, in die der Wert reingegeben werden soll
	 * @param <E>       der Typ des Enums
	 * @param <T>       der Typ des DBIdentifiers des Enums
	 *
	 * @return sich Selbst
	 */
	public <E extends DBEnum<T>, T> Java8RsLoader nextDBEnum(Supplier<T> getter, Class<E> enumClass, Consumer<E> setter) {
		return nextDBEnum(getter, enumClass, setter, Function.identity());
	}
	
	/**
	 * Lädt den {@link Boolean} an der aktuellen Position, gibt ihn in die mapper Funktion und das Ergebnis dann in die Setter Funktion und geht eine Position weiter
	 *
	 * @param getter    getter, der genutzt werden soll um den DBIdentifier zu laden
	 * @param enumClass die Klasse des Enums, welches zurückgegeben werden soll
	 * @param setter    die Funktion, in die der finale Wert reingegeben werden soll
	 * @param mapper    Funktion, die zwischen laden des Wertes und setter steht
	 * @param <E>       der Typ des Enums
	 * @param <T>       der Typ des DBIdentifiers des Enums
	 * @param <R>       Ergebnistyp des mappers und somit Parametertyp des setters
	 *
	 * @return sich Selbst
	 */
	public <E extends DBEnum<T>, T, R> Java8RsLoader nextDBEnum(Supplier<T> getter, Class<E> enumClass, Consumer<R> setter, Function<E, R> mapper) {
		setter.accept(mapper.apply(nextDBEnum(getter, enumClass)));
		return this;
	}
	
	/**
	 * Gibt die {@link DBColumn} an der aktuellen Position zurück und geht eine Position weiter
	 *
	 * @param getter   getter, der genutzt werden soll um das Datenbankfeld zu laden
	 * @param instance die bereits existierende Instanz der {@link DBColumn}, in der die Werte gespeichert werden sollen
	 * @param <E>      der Typ der {@link DBColumn}
	 * @param <T>      der Typ des Datenbankwertes der {@link DBColumn}
	 *
	 * @return die {@link DBColumn}
	 */
	@SuppressWarnings("unchecked")
	public <E extends DBColumn<T>, T> E nextDBColumn(Supplier<T> getter, E instance) {
		return (E) instance.fillFromColumn(getter.get());
	}
	
	/**
	 * Lädt die {@link DBColumn} an der aktuellen Position, gibt ihn in die Setter Funktion und geht eine Position weiter
	 *
	 * @param getter   getter, der genutzt werden soll um das Datenbankfeld zu laden
	 * @param instance die bereits existierende Instanz der {@link DBColumn}, in der die Werte gespeichert werden sollen
	 * @param setter   die Funktion, in die der Wert reingegeben werden soll
	 * @param <E>      der Typ der {@link DBColumn}
	 * @param <T>      der Typ des Datenbankwertes der {@link DBColumn}
	 *
	 * @return sich Selbst
	 */
	public <E extends DBColumn<T>, T> Java8RsLoader nextDBColumn(Supplier<T> getter, E instance, Consumer<E> setter) {
		return nextDBColumn(getter, instance, setter, Function.identity());
	}
	
	/**
	 * Lädt die {@link DBColumn} an der aktuellen Position, gibt ihn in die mapper Funktion und das Ergebnis dann in die Setter Funktion und geht eine Position weiter
	 *
	 * @param getter   getter, der genutzt werden soll um das Datenbankfeld zu laden
	 * @param instance die bereits existierende Instanz der {@link DBColumn}, in der die Werte gespeichert werden sollen
	 * @param setter   die Funktion, in die der finale Wert reingegeben werden soll
	 * @param mapper   Funktion, die zwischen laden des Wertes und setter steht
	 * @param <E>      der Typ der {@link DBColumn}
	 * @param <T>      der Typ des Datenbankwertes der {@link DBColumn}
	 * @param <R>      Ergebnistyp des mappers und somit Parametertyp des setters
	 *
	 * @return sich Selbst
	 */
	public <E extends DBColumn<T>, T, R> Java8RsLoader nextDBColumn(Supplier<T> getter, E instance, Consumer<R> setter, Function<E, R> mapper) {
		setter.accept(mapper.apply(nextDBColumn(getter, instance)));
		return this;
	}
}
