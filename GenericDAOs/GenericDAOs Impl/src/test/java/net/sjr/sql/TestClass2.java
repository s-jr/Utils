package net.sjr.sql;

/**
 * Created by Jan Reichl on 12.05.17.
 */
public class TestClass2 extends DBObjectImpl<Long> {
	private Boolean b = null;

	public Boolean getB() {
		return b;
	}

	public void setB(Boolean b) {
		this.b = b;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		TestClass2 that = (TestClass2) o;

		return b != null ? b.equals(that.b) : that.b == null;
	}

	@Override
	public int hashCode() {
		return b != null ? b.hashCode() : 0;
	}

	@Override
	public String toString() {
		return "TestClass2{" +
				"b=" + b +
				'}';
	}


}
