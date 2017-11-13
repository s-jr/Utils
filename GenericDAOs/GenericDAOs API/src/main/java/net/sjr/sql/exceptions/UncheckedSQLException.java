package net.sjr.sql.exceptions;

import java.io.Serializable;
import java.sql.SQLException;

/**
 * Klasse um {@link SQLException} in {@link RuntimeException} zu kapseln
 */
public class UncheckedSQLException extends RuntimeException implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Erstellt eine neue {@link UncheckedSQLException} mit einer {@link SQLException} als Grund
	 *
	 * @param grund der Grund
	 */
	public UncheckedSQLException(SQLException grund) {
		super(grund);
	}
}
