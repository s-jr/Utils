package net.sjr.sql;

public class KreuzDAOConnectionPool extends DAOConnectionPoolBase<KreuzDAOConnection> {
	
	/**
	 * Erstellt einen neuen {@link KreuzDAOConnectionPool}
	 *
	 * @param dao die zu benutzende {@link KreuzDAOBase}
	 */
	public KreuzDAOConnectionPool(final KreuzDAOBase<?, ?, ?, ?, ?> dao) {
		super(new KreuzDAOConnectionPoolFactory(dao), dao);
	}
}
