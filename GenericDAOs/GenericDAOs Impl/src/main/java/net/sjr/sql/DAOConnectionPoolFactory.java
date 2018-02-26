package net.sjr.sql;

import java.sql.Connection;

public class DAOConnectionPoolFactory extends DAOConnectionPoolFactoryBase<DAOConnection, DAO<?, ?>> {
	
	/**
	 * Erstellt eine neue {@link DAOConnectionPoolFactory}
	 *
	 * @param dao die zu benutzende {@link DAO}
	 */
	public DAOConnectionPoolFactory(DAO<?, ?> dao) {
		super(dao);
	}
	
	/**
	 * Erstellt eine neue {@link DAOConnection}
	 *
	 * @param connection die zu benutzende {@link Connection}
	 * @param dao        die zu benutzende {@link DAO}
	 * @return die erstellte {@link DAOConnection}
	 */
	@Override
	protected DAOConnection doCreateConnection(Connection connection, DAO<?, ?> dao) {
		return new DAOConnection(connection, dao);
	}
}
