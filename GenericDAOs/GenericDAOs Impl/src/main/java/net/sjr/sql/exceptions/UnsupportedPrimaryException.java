package net.sjr.sql.exceptions;

import org.jetbrains.annotations.Nullable;

/**
 * Created by Jan on 02.05.2017.
 */
public class UnsupportedPrimaryException extends RuntimeException {
	private static final long serialVersionUID = 9144510123364215760L;
	
	public UnsupportedPrimaryException(final @Nullable String columnTypeName) {
		super("Der Spaltentyp " + columnTypeName + " wird als Primary ID nicht unterstützt!");
	}
}
