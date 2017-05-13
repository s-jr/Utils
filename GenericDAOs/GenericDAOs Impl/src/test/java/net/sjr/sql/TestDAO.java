package net.sjr.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Jan on 06.05.2017.
 */
public class TestDAO extends DAO<Test, Integer> {

	public TestDAO(Connection con) {
		super(con);
	}

	public TestDAO(DAO<? extends DBObject, ? extends Number> dao) {
		super(dao);
	}

	@Override
	protected String getFelder() {
		return null;
	}

	@Override
	protected String getTable() {
		return null;
	}

	@Override
	protected String getPrimaryCol() {
		return null;
	}

	@Override
	protected ParameterList getPList(Test v) {
		return null;
	}

	@Override
	protected Test getFromRS(ResultSet rs, DBObject... loadedObjects) throws SQLException {
		Test result = new Test();
		fillObject(rs, result, loadedObjects);
		return result;
	}

	@Override
	protected void fillObject(ResultSet rs, Test result, DBObject... loadedObjects) throws SQLException {
		SQLUtils.loadedObjectsOrNull(1, rs, new TestDAO(this), loadedObjects);
	}
}
