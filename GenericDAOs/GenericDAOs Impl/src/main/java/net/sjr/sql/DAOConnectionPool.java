package net.sjr.sql;

public class DAOConnectionPool extends DAOConnectionPoolBase<DAOConnection> {
	
	/**
	 * Erstellt einen neuen {@link DAOConnectionPool}
	 *
	 * @param dao die zu benutzende {@link DAO}
	 */
	public DAOConnectionPool(final DAO<?, ?> dao) {
		super(new DAOConnectionPoolFactory(dao), dao);
	}
}
