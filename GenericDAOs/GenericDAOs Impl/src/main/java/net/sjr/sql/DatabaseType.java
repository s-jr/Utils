package net.sjr.sql;

import org.jetbrains.annotations.NotNull;

/**
 * Klasse mit Typen von DBMS
 */
public enum DatabaseType {
	MICROSOFT("Microsoft SQL Server"), ORACLE("Oracle"), POSTGRES("PostgreSQL"), MYSQL("MySQL"), HSQLDB("HSQL Database Engine"), DB2("DB2"), H2("H2"), DERBY("Apache Derby"), UNKNOWN;
	private final String identifier;
	
	/**
	 * Erstellt einen Datenbanktyp mit Identifier
	 *
	 * @param identifier der Identifier
	 */
	DatabaseType(final @NotNull String identifier) {
		this.identifier = identifier;
	}
	
	/**
	 * Erstellt einen Datenbanktyp ohne Identifier
	 */
	DatabaseType() {
		identifier = null;
	}
	
	/**
	 * Wählt den {@link DatabaseType} an Hand des Identifiers aus der {@link java.sql.DatabaseMetaData#getDatabaseProductName getDatabaseProductName} Methode
	 * @param identifier der Identifier
	 * @return der {@link DatabaseType}
	 */
	public static @NotNull DatabaseType getFromIdentifier(final @NotNull String identifier) {
		for (final DatabaseType databaseType : DatabaseType.values()) {
			if (databaseType.identifier == null) continue;
			if (identifier.startsWith(databaseType.identifier)) return databaseType;
		}

		return UNKNOWN;
	}
}
