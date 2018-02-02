package net.sjr.sql.spring;

import net.sjr.sql.DBObject;
import net.sjr.sql.SQLUtils;
import net.sjr.sql.exceptions.UnsupportedPrimaryException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.batch.item.ExecutionContext;

@SuppressWarnings("WeakerAccess")
public class SQLUtilsSpring extends SQLUtils {
	@SuppressWarnings("unchecked")
	public static @NotNull <T extends DBObject<P>, P extends Number> P loadLastPrimaryFromContext(final @NotNull PaginationDAO<T, P> dao, final @NotNull ExecutionContext executionContext, final @NotNull String executionContextKey) {
		Class<P> genericClass = getPrimaryClass(dao);
		if (genericClass.equals(Integer.class)) return (P) (Integer) executionContext.getInt(executionContextKey);
		if (genericClass.equals(Long.class)) return (P) (Long) executionContext.getLong(executionContextKey);
		if (genericClass.equals(Byte.class)) return (P) (Byte) (byte) executionContext.getInt(executionContextKey);
		if (genericClass.equals(Short.class)) return (P) (Short) (short) executionContext.getInt(executionContextKey);
		if (genericClass.equals(Double.class)) return (P) (Double) executionContext.getDouble(executionContextKey);
		if (genericClass.equals(Float.class)) return (P) (Float) (float) executionContext.getDouble(executionContextKey);
		throw new UnsupportedPrimaryException(genericClass.getName());
	}
	
	public static <T extends DBObject<P>, P extends Number> void saveLastPrimaryToContext(final @NotNull PaginationDAO<T, P> dao, final @Nullable P lastPrimary, final @NotNull ExecutionContext executionContext, final @NotNull String executionContextKey) {
		if (lastPrimary == null) executionContext.remove(executionContextKey);
		else {
			Class<P> genericClass = getPrimaryClass(dao);
			if (genericClass.equals(Integer.class)) executionContext.putInt(executionContextKey, (Integer) lastPrimary);
			else if (genericClass.equals(Long.class)) executionContext.putLong(executionContextKey, (Long) lastPrimary);
			else if (genericClass.equals(Byte.class)) executionContext.putInt(executionContextKey, (Byte) lastPrimary);
			else if (genericClass.equals(Short.class)) executionContext.putInt(executionContextKey, (Short) lastPrimary);
			else if (genericClass.equals(Double.class)) executionContext.putDouble(executionContextKey, (Double) lastPrimary);
			else if (genericClass.equals(Float.class)) executionContext.putDouble(executionContextKey, (Float) lastPrimary);
			else throw new UnsupportedPrimaryException(genericClass.getName());
		}
	}
}
