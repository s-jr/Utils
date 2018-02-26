package net.sjr.sql;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class DAOConnectionPoolFactoryBase<C extends DAOConnectionBase<?>, D extends DAOBase<? extends DAOConnectionPoolBase<C>, C>> extends BasePooledObjectFactory<C> {
	protected final D dao;
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	/**
	 * Erstellt eine neue {@link DAOConnectionPoolFactoryBase}
	 *
	 * @param dao die zu benutzende {@link DAOBase}
	 */
	public DAOConnectionPoolFactoryBase(D dao) {
		this.dao = dao;
	}
	
	/**
	 * Erstellt eine neue {@link DAOConnectionBase}
	 *
	 * @param connection die zu benutzende {@link Connection}
	 * @param dao        die zu benutzende {@link DAOBase}
	 * @return die erstellte {@link DAOConnectionBase}
	 */
	protected abstract C doCreateConnection(Connection connection, D dao);
	
	@Override
	public C create() throws SQLException {
		if (dao.staticConnection == null) {
			if (dao.dataSource != null) {
				return doCreateConnection(dao.getConnectionFromDataSource(), dao);
			}
			else {
				throw new IllegalStateException("Die DAO hat keine Connection und keine DataSource!");
			}
		}
		return doCreateConnection(dao.staticConnection, dao);
	}
	
	@Override
	public PooledObject<C> wrap(C obj) {
		return new DefaultPooledObject<>(obj);
	}
	
	@Override
	public void destroyObject(PooledObject<C> p) {
		SQLUtils.closeSqlAutocloseable(log, p.getObject());
	}
	
	@Override
	public boolean validateObject(PooledObject<C> p) {
		C daoConnection = p.getObject();
		if (daoConnection == null) return false;
		Connection con = daoConnection.connection;
		try {
			return !con.isClosed();
		}
		catch (SQLException e) {
			log.error("Fehler bei der isClosed Abfrage", e);
		}
		return false;
	}
}
