package net.sjr.sql;

import java.sql.Connection;

public class KreuzDAOConnectionPoolFactory extends DAOConnectionPoolFactoryBase<KreuzDAOConnection, KreuzDAOBase<?, ?, ?, ?, ?>> {
	
	public KreuzDAOConnectionPoolFactory(KreuzDAOBase<?, ?, ?, ?, ?> dao) {
		super(dao);
	}
	
	/**
	 * Erstellt eine neue {@link KreuzDAOConnection}
	 *
	 * @param connection die zu benutzende {@link Connection}
	 * @param dao        die zu benutzende {@link KreuzDAOBase}
	 * @return die erstellte {@link KreuzDAOConnection}
	 */
	@Override
	protected KreuzDAOConnection doCreateConnection(Connection connection, KreuzDAOBase<?, ?, ?, ?, ?> dao) {
		return new KreuzDAOConnection(connection, dao);
	}
}
