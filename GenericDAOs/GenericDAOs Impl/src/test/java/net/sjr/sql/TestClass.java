package net.sjr.sql;

import java.util.Date;
import java.util.Objects;

/**
 * Created by Jan on 06.05.2017.
 */
public class TestClass extends DBObjectImpl<Integer> {
	private static final long serialVersionUID = -7366625836519728088L;
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
		return i == testClass.i &&
				Objects.equals(s, testClass.s) &&
				Objects.equals(d, testClass.d) &&
				Objects.equals(test2, testClass.test2);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(s, i, d, test2);
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
