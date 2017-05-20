package net.sjr.sql;

import java.sql.Connection;

/**
 * Created by Jan on 15.05.2017.
 */
public class KreuzTestDAO extends Kreuz2DAO<TestClass, Integer, TestClass2, Long> {
	private static final String KREUZ = "Kreuz";
	private static final String COLA = "Test";
	private static final String COLB = "Test2";
	private final TestDAO tdao;
	private final Test2DAO t2dao;

	public KreuzTestDAO(Connection con) {
		tdao = new TestDAO(con);
		t2dao = new Test2DAO(tdao);
	}


	@Override
	protected DAO<TestClass, Integer> getaDAO() {
		return tdao;
	}

	@Override
	protected DAO<TestClass2, Long> getbDAO() {
		return t2dao;
	}

	@Override
	protected String getKreuzTable() {
		return KREUZ;
	}

	@Override
	protected String getKreuzColA() {
		return COLA;
	}

	@Override
	protected String getKreuzColB() {
		return COLB;
	}

	@Override
	public void close() {
		super.close();
		tdao.close();
		t2dao.close();
	}
}
