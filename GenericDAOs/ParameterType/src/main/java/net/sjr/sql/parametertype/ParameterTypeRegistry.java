package net.sjr.sql.parametertype;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Jan Reichl on 11.05.17.
 */
public final class ParameterTypeRegistry {
	public static final List<ParameterType> PARAMETER_TYPES = new LinkedList<>();

	public static void registerParameterType(ParameterType type) {
		PARAMETER_TYPES.add(type);
	}

	private ParameterTypeRegistry() {
	}
}
