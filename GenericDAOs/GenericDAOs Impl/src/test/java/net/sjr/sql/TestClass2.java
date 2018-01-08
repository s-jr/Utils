package net.sjr.sql;

import java.util.Objects;

/**
 * Created by Jan Reichl on 12.05.17.
 */
public class TestClass2 extends DBObjectImpl<Long> {
	private static final long serialVersionUID = 3041904659160854878L;
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
		return Objects.equals(b, that.b);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(b);
	}
	
	@Override
	public String toString() {
		return "TestClass2{" +
				"b=" + b +
				'}';
	}


}
