package net.sjr.sql;

import net.sjr.sql.exceptions.NoNullTypeException;
import net.sjr.sql.exceptions.UnsupportedValueException;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by Jan on 13.05.2017.
 */
public class TestRunner {
	private TestClass testClass;
	private TestClass2 testClass2 = new TestClass2();

	private Connection con;

	@BeforeMethod
	public void connect() throws SQLException, ParseException {
		con = DriverManager.getConnection("jdbc:h2:mem:;INIT=RUNSCRIPT FROM 'classpath:ddl.sql'", "sa", "");

		testClass2 = new TestClass2();
		testClass2.setPrimary(1L);
		testClass2.setB(false);

		testClass = new TestClass();
		testClass.setPrimary(1);
		testClass.setS("s");
		testClass.setI(1);
		testClass.setD(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2017-05-13 16:42:43"));

		testClass.setTest2(testClass2);
	}

	@AfterMethod
	public void disconnect() throws SQLException {
		con.close();
	}

	@Test
	public void testLoadAll() {
		List<TestClass> expected = Arrays.asList(testClass);
		try (TestDAO tdao = new TestDAO(con)) {
			List<TestClass> actual = tdao.loadAll();
			Assert.assertEquals(actual, expected);
		}
	}

	@Test
	public void testLoadOne() {
		try (TestDAO tdao = new TestDAO(con)) {
			TestClass actual = tdao.loadFromID(testClass.getPrimary());
			Assert.assertEquals(actual, testClass);
		}
	}

	@Test(dependsOnMethods = {"testLoadAll", "testLoadOne"})
	public void testUpdate() {
		try (TestDAO tdao = new TestDAO(con)) {
			testClass.setS("s");
			tdao.updateIntoDB(testClass);
			TestClass actual = tdao.loadFromID(1);
			Assert.assertEquals(actual, testClass);
		}
	}

	@Test(dependsOnMethods = {"testLoadAll", "testLoadOne"})
	public void testInsert() {
		try (TestDAO tdao = new TestDAO(con)) {
			TestClass neu = new TestClass();
			neu.setS(null);
			neu.setI(3);
			neu.setD(new Date());
			neu.setTest2(testClass2);
			tdao.insertIntoDB(neu);
			Assert.assertNotNull(neu.getPrimary());
			TestClass actual = tdao.loadFromID(neu.getPrimary());
			Assert.assertEquals(actual, neu);
		}
	}

	@Test(dependsOnMethods = {"testLoadAll", "testLoadOne"})
	public void testDeleteFromPool() {
		try (TestDAO tdao = new TestDAO(con)) {
			tdao.deleteFromDB(testClass);
			Assert.assertNull(testClass.getPrimary());

			Assert.assertEquals(tdao.loadAll().size(), 0);
		}
	}

	@Test(dependsOnMethods = {"testInsert"}, expectedExceptions = {NoNullTypeException.class})
	public void noNullType() {
		try (Test2DAO tdao = new Test2DAO(con)) {
			TestClass2 neu = new TestClass2();
			tdao.insertIntoDB(neu);
		}
	}

	@Test(dependsOnMethods = {"testInsert"}, expectedExceptions = {UnsupportedValueException.class})
	public void unsupportedValue() {
		try (UnsupportedDAO udao = new UnsupportedDAO(con)) {
			UnsupportedClass neu = new UnsupportedClass();
			neu.setO(new Object());
			udao.insertIntoDB(neu);
		}
	}

	@Test(expectedExceptions = {IllegalStateException.class})
	public void changeExistingPrimary() {
		testClass.setPrimary(2);
	}

	@Test(dependsOnMethods = {"testLoadAll", "testLoadOne"})
	public void loadOneFromCol() {
		try (TestDAO tdao = new TestDAO(con)) {
			TestClass actual = tdao.getOneFromTest2(testClass2);
			Assert.assertEquals(actual, testClass);
		}
	}
}
