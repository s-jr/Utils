package net.sjr.sql.exceptions;

import java.io.Serializable;

public class UnsupportedValueException extends RuntimeException implements Serializable {
	private static final long serialVersionUID = 1L;

	public UnsupportedValueException(final Class<?> clas) {
		super("Die Klasse " + clas.getName() + " wird nicht nicht unterstützt");
	}
}
