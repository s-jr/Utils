package net.sjr.sql.exceptions;

/**
 * Created by Jan on 02.05.2017.
 */
public class UnsupportedPrimaryException extends RuntimeException {
	public UnsupportedPrimaryException(String columnTypeName) {
		super("Der Spaltentyp " + columnTypeName + " wird als Primary ID nicht unterstützt!");
	}
}
