package net.sjr.sql;

import java.io.Serializable;

public interface DBObject<P extends Number> extends Serializable {
	P getPrimary();

	void setPrimary(P primary);
}
