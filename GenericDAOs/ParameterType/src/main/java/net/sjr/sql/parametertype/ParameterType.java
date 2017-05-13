package net.sjr.sql.parametertype;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Jan Reichl on 11.05.17.
 */
public interface ParameterType {
	int set(PreparedStatement pst, int pos, Object value) throws SQLException;
}
