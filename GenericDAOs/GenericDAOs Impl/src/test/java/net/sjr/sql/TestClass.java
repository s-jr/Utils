package net.sjr.sql;

import java.util.Date;

/**
 * Created by Jan on 06.05.2017.
 */
public class TestClass extends DBObjectImpl<Integer> {
	private String s = null;
	private int i = 1;
	private Date d = null;
	private TestClass2 test2 = null;

	public String getS() {
		return s;
	}

	public void setS(String s) {
		this.s = s;
	}

	public int getI() {
		return i;
	}

	public void setI(int i) {
		this.i = i;
	}

	public Date getD() {
		return d;
	}

	public void setD(Date d) {
		this.d = d;
	}

	public TestClass2 getTest2() {
		return test2;
	}

	public void setTest2(TestClass2 test2) {
		this.test2 = test2;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		TestClass testClass = (TestClass) o;

		if (i != testClass.i) return false;
		if (s != null ? !s.equals(testClass.s) : testClass.s != null) return false;
		if (d != null ? !d.equals(testClass.d) : testClass.d != null) return false;
		return test2 != null ? test2.equals(testClass.test2) : testClass.test2 == null;
	}

	@Override
	public int hashCode() {
		int result = s != null ? s.hashCode() : 0;
		result = 31 * result + i;
		result = 31 * result + (d != null ? d.hashCode() : 0);
		result = 31 * result + (test2 != null ? test2.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "TestClass{" +
				"s='" + s + '\'' +
				", i=" + i +
				", d=" + d +
				", test2=" + test2 +
				'}';
	}


}
