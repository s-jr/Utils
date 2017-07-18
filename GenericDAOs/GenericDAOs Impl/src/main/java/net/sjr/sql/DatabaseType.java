package net.sjr.sql;

/**
 * Created by Jan on 18.07.2017.
 */
public enum DatabaseType {
	MICROSOFT("Microsoft SQL Server"), ORACLE("Oracle"), POSTGRES("PostgreSQL"), MYSQL("MySQL"), HSQLDB("HSQL Database Engine"), DB2("DB2"), H2("H2"), DERBY("Apache Derby"), UNKNOWN;
	private final String identifier;

	DatabaseType(String identifier) {
		this.identifier = identifier;
	}

	DatabaseType() {
		identifier = null;
	}

	public static DatabaseType getFromIdentifier(String identifier) {
		for (DatabaseType databaseType : DatabaseType.values()) {
			if (databaseType.identifier == null) continue;
			if (identifier.startsWith(databaseType.identifier)) return databaseType;
		}

		return UNKNOWN;
	}
}
