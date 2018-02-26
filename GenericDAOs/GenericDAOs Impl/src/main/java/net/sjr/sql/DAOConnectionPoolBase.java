package net.sjr.sql;

import org.apache.commons.pool2.impl.GenericObjectPool;

public class DAOConnectionPoolBase<C extends DAOConnectionBase<?>> extends GenericObjectPool<C> {
	protected final DAOBase<?, C> dao;
	
	/**
	 * Erstellt einen neuen {@link DAOConnectionPoolBase}
	 *
	 * @param factory die zu benutzende {@link DAOConnectionPoolFactoryBase}
	 * @param dao     die zu benutzende {@link DAOBase}
	 */
	public DAOConnectionPoolBase(DAOConnectionPoolFactoryBase<C, ?> factory, final DAOBase<?, C> dao) {
		super(factory);
		this.dao = dao;
	}
	
	@Override
	public C borrowObject(long borrowMaxWaitMillis) throws Exception {
		if (dao.getPoolConfig() != null) setConfig(dao.getPoolConfig());
		return super.borrowObject(borrowMaxWaitMillis);
	}
	
	@Override
	public void returnObject(C obj) {
		if (dao.getPoolConfig() != null) setConfig(dao.getPoolConfig());
		super.returnObject(obj);
	}
}
