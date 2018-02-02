package net.sjr.sql;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Jan on 06.05.2017.
 */
public class Test2DAO extends DAO<TestClass2, Long> {
	private static final String FELDER = "b";
	private static final String PRIMARY = "test2ID";
	private static final String TABLE = "Test2";

	public Test2DAO(Connection con) {
		super(con);
	}

	public Test2DAO(DAO<? extends DBObject, ? extends Number> dao) {
		super(dao);
	}

	@Override
	protected @NotNull String getFelder() {
		return FELDER;
	}

	@Override
	protected @NotNull String getTable() {
		return TABLE;
	}

	@Override
	protected @NotNull String getPrimaryCol() {
		return PRIMARY;
	}

	@Override
	protected @NotNull ParameterList getPList(@NotNull TestClass2 v) {
		return new ParameterList(v.getB());
	}

	@Override
	protected @NotNull TestClass2 getFromRS(ResultSet rs, DBObject... loadedObjects) throws SQLException {
		TestClass2 result = new TestClass2();
		fillObject(rs, result, loadedObjects);
		return result;
	}

	@Override
	protected void fillObject(@NotNull ResultSet rs, @NotNull TestClass2 result, DBObject... loadedObjects) throws SQLException {
		result.setPrimary(rs.getLong(1));
		result.setB(rs.getBoolean(2));
	}
}
