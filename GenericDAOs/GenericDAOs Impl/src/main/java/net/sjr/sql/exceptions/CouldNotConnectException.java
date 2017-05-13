package net.sjr.sql.exceptions;

/**
 * Created by Jan Reichl on 09.08.16.
 */
public class CouldNotConnectException extends RuntimeException {
	public CouldNotConnectException(Exception e) {
		super("Could not connect due to:", e);
	}
}
