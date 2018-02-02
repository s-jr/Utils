package net.sjr.sql.exceptions;

import net.sjr.sql.ParameterList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

@SuppressWarnings("unused")
public class EntryNotFoundException extends RuntimeException implements Serializable {
	private static final long serialVersionUID = -4613415645497874507L;
	
	public EntryNotFoundException(final @Nullable String where, final @Nullable ParameterList values) {
		super("Der Eintrag mit WHERE '" + where + "' und den Parametern '" + values + " wurde nicht gefunden!");
	}
	
	public EntryNotFoundException(final @NotNull String col, final @Nullable Object val) {
		super("Der Eintrag mit " + col + '=' + val + " wurde nicht gefunden!");
	}
	
	public EntryNotFoundException() {
		super("Der Eintrag wurde nicht gefunden!");
	}
}
