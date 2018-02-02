package net.sjr.sql.parametertype;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

/**
 * Zentrale Registry, bei der sich alle Klassen, die als {@link ParameterType} agieren wollen, registrieren müssen
 */
public final class ParameterTypeRegistry {
	public static final List<ParameterType> PARAMETER_TYPES = new LinkedList<>();
	
	/**
	 * Registriert einen neuen {@link ParameterType}
	 *
	 * @param type der {@link ParameterType}
	 */
	public static void registerParameterType(final @NotNull ParameterType type) {
		PARAMETER_TYPES.add(type);
	}

	private ParameterTypeRegistry() {
	}
}
