package net.sjr.sql.exceptions;

import org.jetbrains.annotations.NotNull;

public class UnsupportedValueException extends RuntimeException {
	private static final long serialVersionUID = 2072033282205932367L;
	
	public UnsupportedValueException(final @NotNull Class<?> clas) {
		super("Die Klasse " + clas.getName() + " wird nicht nicht unterstützt");
	}
}
