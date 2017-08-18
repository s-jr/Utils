package net.sjr.sql.rsloader;

import net.sjr.sql.DAOBase;
import net.sjr.sql.DBObject;
import net.sjr.sql.exceptions.UncheckedSQLException;

import java.math.BigDecimal;
import java.sql.*;

/**
 * Created by Jan Reichl on 14.08.17.
 */
public class RsLoader {
	private final ResultSet rs;
	private int pos = 1;
	private final DBObject[] loadedObjects;

	public RsLoader(ResultSet rs, final DBObject... loadedObjects) {
		this.rs = rs;
		this.loadedObjects = loadedObjects;
	}

	public RsLoader skip() {
		return skip(1);
	}

	public RsLoader skip(int steps) {
		pos += steps;
		return this;
	}

	public String nextString() {
		try {
			return rs.getString(pos++);
		}
		catch (SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}

	public boolean nextBoolean() {
		try {
			return rs.getBoolean(pos++);
		}
		catch (SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}

	public Boolean nextNullBoolean() {
		try {
			return RsUtils.getNullableBoolean(rs, pos++);
		}
		catch (SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}

	public byte nextByte() {
		try {
			return rs.getByte(pos++);
		}
		catch (SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}

	public Byte nextNullByte() {
		try {
			return RsUtils.getNullableByte(rs, pos++);
		}
		catch (SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}

	public short nextShort() {
		try {
			return rs.getShort(pos++);
		}
		catch (SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}

	public Short nextNullShort() {
		try {
			return RsUtils.getNullableShort(rs, pos++);
		}
		catch (SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}

	public int nextInt() {
		try {
			return rs.getInt(pos++);
		}
		catch (SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}

	public Integer nextNullInt() {
		try {
			return RsUtils.getNullableInt(rs, pos++);
		}
		catch (SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}

	public long nextLong() {
		try {
			return rs.getLong(pos++);
		}
		catch (SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}

	public Long nextNullLong() {
		try {
			return RsUtils.getNullableLong(rs, pos++);
		}
		catch (SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}

	public float nextFloat() {
		try {
			return rs.getFloat(pos++);
		}
		catch (SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}

	public Float nextNullFloat() {
		try {
			return RsUtils.getNullableFloat(rs, pos++);
		}
		catch (SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}

	public double nextDouble() {
		try {
			return rs.getFloat(pos++);
		}
		catch (SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}

	public Double nextNullDouble() {
		try {
			return RsUtils.getNullableDouble(rs, pos++);
		}
		catch (SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}

	public BigDecimal nextBigDecimal() {
		try {
			return rs.getBigDecimal(pos++);
		}
		catch (SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}

	public Date nextDate() {
		try {
			return rs.getDate(pos++);
		}
		catch (SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}

	public Time nextTime() {
		try {
			return rs.getTime(pos++);
		}
		catch (SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}

	public Timestamp nextTimestamp() {
		try {
			return rs.getTimestamp(pos++);
		}
		catch (SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}

	public java.util.Date nextUtilDate() {
		Timestamp value = nextTimestamp();
		if (value == null) return null;
		return new java.util.Date(value.getTime());
	}

	public char nextChar() {
		String value = nextString();
		if (value == null || value.isEmpty()) throw new IllegalArgumentException("Der String ist null oder leer!");
		return value.charAt(0);
	}

	public Character nextNullChar() {
		String value = nextString();
		if (value == null || value.isEmpty()) return null;
		return value.charAt(0);
	}

	public <T extends DBObject<P>, P extends Number> T nextDBObject(final DAOBase<T, P> dao) {
		try {
			return RsUtils.loadedObjectsOrNull(pos++, rs, dao, loadedObjects);
		}
		catch (SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}
}
