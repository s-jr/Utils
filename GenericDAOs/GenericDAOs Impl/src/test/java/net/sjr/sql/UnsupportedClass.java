package net.sjr.sql;

/**
 * Created by Jan on 06.05.2017.
 */
public class UnsupportedClass extends DBObjectImpl<Integer> {
	private Object o = null;

	public Object getO() {
		return o;
	}

	public void setO(Object o) {
		this.o = o;
	}
}
