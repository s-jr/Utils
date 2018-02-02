package net.sjr.sql;


import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;

/**
 * Created by Jan on 06.05.2017.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class TestDAO extends DAO<TestClass, Integer> {
	private static final String FELDER = "s, i, d, test2";
	private static final String PRIMARY = "testID";
	private static final String TABLE = "Test";

	public TestDAO(Connection con) {
		super(con);
	}

	public TestDAO(DAO<? extends DBObject, ? extends Number> dao) {
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
	protected @NotNull ParameterList getPList(@NotNull TestClass v) {
		return new ParameterList(new Parameter(v.getS(), Types.VARCHAR), v.getI(), new Parameter(v.getD(), Types.DATE), new Parameter(v.getTest2(), Types.BIGINT));
	}

	@Override
	protected @NotNull TestClass getFromRS(ResultSet rs, DBObject... loadedObjects) throws SQLException {
		TestClass result = new TestClass();
		fillObject(rs, result, loadedObjects);
		return result;
	}

	@Override
	protected void fillObject(@NotNull ResultSet rs, @NotNull TestClass result, DBObject... loadedObjects) throws SQLException {
		result.setPrimary(rs.getInt(1));
		result.setS(rs.getString(2));
		result.setI(rs.getInt(3));
		result.setD(new Date(rs.getTimestamp(4).getTime()));
		result.setTest2(SQLUtils.loadedObjectsOrNull(5, rs, new Test2DAO(this), loadedObjects));
	}

	public TestClass getOneFromTest2(TestClass2 testClass2) {
		return loadOneFromCol(null, "test2", testClass2, testClass2);
	}
}
