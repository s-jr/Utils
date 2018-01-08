package net.sjr.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Jan on 06.05.2017.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class UnsupportedDAO extends DAO<UnsupportedClass, Integer> {
	private static final String FELDER = "b";
	private static final String PRIMARY = "test2ID";
	private static final String TABLE = "Test2";

	public UnsupportedDAO(Connection con) {
		super(con);
	}

	public UnsupportedDAO(DAO<? extends DBObject, ? extends Number> dao) {
		super(dao);
	}

	@Override
	protected String getFelder() {
		return FELDER;
	}

	@Override
	protected String getTable() {
		return TABLE;
	}

	@Override
	protected String getPrimaryCol() {
		return PRIMARY;
	}

	@Override
	protected ParameterList getPList(UnsupportedClass v) {
		return new ParameterList(v.getO());
	}

	@Override
	protected UnsupportedClass getFromRS(ResultSet rs, DBObject... loadedObjects) throws SQLException {
		UnsupportedClass result = new UnsupportedClass();
		fillObject(rs, result, loadedObjects);
		return result;
	}

	@Override
	protected void fillObject(ResultSet rs, UnsupportedClass result, DBObject... loadedObjects) throws SQLException {
		result.setPrimary(rs.getInt(1));
		result.setO(rs.getObject(2));
	}
}
