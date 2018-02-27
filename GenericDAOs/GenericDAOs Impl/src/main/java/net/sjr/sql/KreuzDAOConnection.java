package net.sjr.sql;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class KreuzDAOConnection extends DAOConnectionBase<KreuzDAOBase<?, ?, ?, ?, ?>> {
	
	/**
	 * Erstellt eine neue {@link KreuzDAOConnection}
	 *
	 * @param connection die zu benutzende {@link Connection}
	 * @param dao        die zu benutzende {@link KreuzDAOBase}
	 */
	public KreuzDAOConnection(Connection connection, KreuzDAOBase<?, ?, ?, ?, ?> dao) {
		super(connection, dao);
	}
	
	/**
	 * Erstellt ein {@link PreparedStatement} zum Einfügen einer neuen Verbindung oder lädt es aus dem Cache
	 *
	 * @return das {@link PreparedStatement}
	 * @throws SQLException Wenn eine {@link SQLException} aufgetreten ist
	 */
	protected @NotNull PreparedStatement createKreuzPst() throws SQLException {
		PreparedStatement result = dao.shouldCloseAlways() ? null : pstCache.get("createKreuz");
		if (result == null) {
			result = prepareStatement("INSERT INTO " + dao.getTable() + " (" + dao.getAllKreuzCols() + ") VALUES (" + SQLUtils.getFragezeichenInsert(dao
					.getAllKreuzCols()) + ')');
			if (!dao.shouldCloseAlways()) pstCache.put("createKreuz", result);
		}
		return result;
	}
	
	/**
	 * Erstellt ein {@link PreparedStatement} zum Löschen einer Verbindung oder lädt es aus dem Cache
	 *
	 * @return das {@link PreparedStatement}
	 * @throws SQLException Wenn eine {@link SQLException} aufgetreten ist
	 */
	protected @NotNull PreparedStatement deleteKreuzPst() throws SQLException {
		PreparedStatement result = dao.shouldCloseAlways() ? null : pstCache.get("deleteKreuz");
		if (result == null) {
			result = prepareStatement("DELETE FROM " + dao.getTable() + " WHERE " + SQLUtils.getFragezeichenSelect(dao.getAllKreuzCols(), " AND ", "="));
			if (!dao.shouldCloseAlways()) pstCache.put("deleteKreuz", result);
		}
		return result;
	}
}
