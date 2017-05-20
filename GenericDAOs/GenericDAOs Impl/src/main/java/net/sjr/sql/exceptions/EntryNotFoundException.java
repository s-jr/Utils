package net.sjr.sql.exceptions;

import java.io.Serializable;

public class EntryNotFoundException extends RuntimeException implements Serializable {
	private static final long serialVersionUID = 1L;

	public EntryNotFoundException(final String col, final Object val) {
		super("Der Eintrag mit " + col + "=" + val + " wurde nicht gefunden!");
	}

	public EntryNotFoundException() {
		super("Der Eintrag wurde nicht gefunden!");
	}
}
