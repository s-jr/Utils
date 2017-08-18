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
import java.util.function.Supplier;

/**
 * Created by Jan Reichl on 18.08.17.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class Java8RsLoader extends RsLoader {

	@Override
	public Java8RsLoader skip() {
		return (Java8RsLoader) super.skip();
	}

	@Override
	public Java8RsLoader skip(final int steps) {
		return (Java8RsLoader) super.skip(steps);
	}

	public <T> Java8RsLoader skip(Supplier<T> getter, Consumer<T> setter) {
		setter.accept(getter.get());
		return skip();
	}

	public <T> Java8RsLoader skip(Supplier<T> getter, Consumer<T> setter, final int steps) {
		setter.accept(getter.get());
		return skip(steps);
	}

	public Java8RsLoader(final ResultSet rs, final DBObject... loadedObjects) {
		super(rs, loadedObjects);
	}

	public Java8RsLoader nextString(Consumer<String> setter) {
		setter.accept(nextString());
		return this;
	}

	public Java8RsLoader nextBoolean(Consumer<Boolean> setter) {
		setter.accept(nextBoolean());
		return this;
	}

	public Java8RsLoader nextNullBoolean(Consumer<Boolean> setter) {
		setter.accept(nextBoolean());
		return this;
	}

	public Java8RsLoader nextByte(Consumer<Byte> setter) {
		setter.accept(nextByte());
		return this;
	}

	public Java8RsLoader nextNullByte(Consumer<Byte> setter) {
		setter.accept(nextByte());
		return this;
	}

	public Java8RsLoader nextShort(Consumer<Short> setter) {
		setter.accept(nextShort());
		return this;
	}

	public Java8RsLoader nextNullShort(Consumer<Short> setter) {
		setter.accept(nextShort());
		return this;
	}

	public Java8RsLoader nextInt(Consumer<Integer> setter) {
		setter.accept(nextInt());
		return this;
	}

	public Java8RsLoader nextNullInt(Consumer<Integer> setter) {
		setter.accept(nextInt());
		return this;
	}

	public Java8RsLoader nextLong(Consumer<Long> setter) {
		setter.accept(nextLong());
		return this;
	}

	public Java8RsLoader nextNullLong(Consumer<Long> setter) {
		setter.accept(nextLong());
		return this;
	}

	public Java8RsLoader nextFloat(Consumer<Float> setter) {
		setter.accept(nextFloat());
		return this;
	}

	public Java8RsLoader nextNullFloat(Consumer<Float> setter) {
		setter.accept(nextFloat());
		return this;
	}

	public Java8RsLoader nextDouble(Consumer<Double> setter) {
		setter.accept(nextDouble());
		return this;
	}

	public Java8RsLoader nextNullDouble(Consumer<Double> setter) {
		setter.accept(nextDouble());
		return this;
	}

	public Java8RsLoader nextBigDecimal(Consumer<BigDecimal> setter) {
		setter.accept(nextBigDecimal());
		return this;
	}

	public Java8RsLoader nextDate(Consumer<Date> setter) {
		setter.accept(nextDate());
		return this;
	}

	public Java8RsLoader nextTime(Consumer<Time> setter) {
		setter.accept(nextTime());
		return this;
	}

	public Java8RsLoader nextTimestamp(Consumer<Timestamp> setter) {
		setter.accept(nextTimestamp());
		return this;
	}

	public Java8RsLoader nextUtilDate(Consumer<java.util.Date> setter) {
		setter.accept(nextUtilDate());
		return this;
	}

	public Java8RsLoader nextChar(Consumer<Character> setter) {
		setter.accept(nextChar());
		return this;
	}

	public Java8RsLoader nextNullChar(Consumer<Character> setter) {
		setter.accept(nextNullChar());
		return this;
	}

	public <T extends DBObject<P>, P extends Number> Java8RsLoader nextDBObject(final DAOBase<T, P> dao, Consumer<T> setter) {
		setter.accept(nextDBObject(dao));
		return this;
	}

	public LocalDate nextLocalDate() {
		return Java8ConverterUtils.SQLDate.sqlDateToLocalDate(nextDate());
	}

	public Java8RsLoader nextLocalDate(Consumer<LocalDate> setter) {
		setter.accept(nextLocalDate());
		return this;
	}

	public LocalDateTime nextLocalDateTime() {
		return Java8ConverterUtils.SQLDate.timestampToLocalDateTime(nextTimestamp());
	}

	public Java8RsLoader nextLocalDateTime(Consumer<LocalDateTime> setter) {
		setter.accept(nextLocalDateTime());
		return this;
	}

	public LocalTime nextLocalTime() {
		return Java8ConverterUtils.SQLDate.sqlTimeToLocalTime(nextTime());
	}

	public Java8RsLoader nextLocalTime(Consumer<LocalTime> setter) {
		setter.accept(nextLocalTime());
		return this;
	}

	public <E extends DBEnum<T>, T> E nextDBEnum(Supplier<T> getter, Class<E> enumClass) {
		return RsUtils.getFromDBIdentifier(getter.get(), enumClass);
	}

	public <E extends DBEnum<T>, T> Java8RsLoader nextDBEnum(Supplier<T> getter, Class<E> enumClass, Consumer<E> setter) {
		setter.accept(nextDBEnum(getter, enumClass));
		return this;
	}

	@SuppressWarnings("unchecked")
	public <E extends DBColumn<T>, T> E nextDBColumn(Supplier<T> getter, E instance) {
		return (E) instance.fillFromColumn(getter.get());
	}

	public <E extends DBColumn<T>, T> Java8RsLoader nextDBColumn(Supplier<T> getter, E instance, Consumer<E> setter) {
		setter.accept(nextDBColumn(getter, instance));
		return this;
	}
}
