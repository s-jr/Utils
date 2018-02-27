package net.sjr.sql;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DAOConnection extends DAOConnectionBase<DAO<?, ?>> {
	
	/**
	 * Erstellt eine neue {@link DAOConnection}
	 *
	 * @param connection die zu benutzende {@link Connection}
	 * @param dao        die zu benutzende {@link DAO}
	 */
	public DAOConnection(Connection connection, DAO<?, ?> dao) {
		super(connection, dao);
	}
	
	/**
	 * Erstellt ein {@link PreparedStatement} zum Einfügen eines Objektes oder lädt es aus dem Cache
	 *
	 * @return das {@link PreparedStatement}
	 * @throws SQLException Wenn eine {@link SQLException} aufgetreten ist
	 */
	protected @NotNull PreparedStatement insertPst() throws SQLException {
		PreparedStatement result = dao.shouldCloseAlways() ? null : pstCache.get("insert");
		if (result == null) {
			String felder = (dao.getDtype() == null ? "" : "DType, ") + dao.getFelder();
			if (getDatabaseType() == DatabaseType.ORACLE) {
				result = prepareStatement("INSERT INTO " + dao.getTable() + " (" + felder + ") VALUES (" + SQLUtils.getFragezeichenInsert(felder) + ')', new String[] {dao.getPrimaryCol()});
			}
			else {
				result = prepareStatement("INSERT INTO " + dao.getTable() + " (" + felder + ") VALUES (" + SQLUtils.getFragezeichenInsert(felder) + ')', Statement.RETURN_GENERATED_KEYS);
			}
			if (!dao.shouldCloseAlways()) pstCache.put("insert", result);
		}
		return result;
	}
	
	/**
	 * Erstellt ein {@link PreparedStatement} zum Updaten eines Objektes oder lädt es aus dem Cache
	 *
	 * @return das {@link PreparedStatement}
	 * @throws SQLException Wenn eine {@link SQLException} aufgetreten ist
	 */
	protected @NotNull PreparedStatement updatePst() throws SQLException {
		PreparedStatement result = dao.shouldCloseAlways() ? null : pstCache.get("update");
		if (result == null) {
			String felder = (dao.getDtype() == null ? "" : "DType, ") + dao.getFelder();
			result = prepareStatement("UPDATE " + dao.getTable() + " SET " + SQLUtils.getFragezeichenUpdate(felder) + " WHERE " + dao.getPrimaryCol() + "=?");
			if (!dao.shouldCloseAlways()) pstCache.put("update", result);
		}
		return result;
	}
	
	/**
	 * Erstellt ein {@link PreparedStatement} zum Löschen eines Objektes oder lädt es aus dem Cache
	 *
	 * @return das {@link PreparedStatement}
	 * @throws SQLException Wenn eine {@link SQLException} aufgetreten ist
	 */
	protected @NotNull PreparedStatement deletePst() throws SQLException {
		PreparedStatement result = dao.shouldCloseAlways() ? null : pstCache.get("delete");
		if (result == null) {
			result = prepareStatement("DELETE FROM " + dao.getTable() + " WHERE " + dao.getPrimaryCol() + "=?" + (dao.getDtype() != null ? " AND DType=?" : ""));
			if (!dao.shouldCloseAlways()) pstCache.put("delete", result);
		}
		return result;
	}
	
}
