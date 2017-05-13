package net.sjr.sql.exceptions;

import java.io.Serializable;
import java.sql.SQLException;

public class UncheckedSQLException extends RuntimeException implements Serializable {
	private static final long serialVersionUID = 1L;

	public UncheckedSQLException(SQLException e) {
		super(e);
	}
}
