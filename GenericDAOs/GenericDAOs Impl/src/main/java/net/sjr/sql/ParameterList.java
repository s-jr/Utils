package net.sjr.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;

public class ParameterList extends LinkedList<Parameter> {
	private static final long serialVersionUID = -3138589760518409560L;
	
	/**
	 * Erstellt eine neue {@link ParameterList}.
	 *
	 * @param objects Objekte, die von Anfang an in der Liste vorhanden sein sollen
	 */
	public ParameterList(final Object... objects) {
		addParameter(objects);
	}
	
	/**
	 * Fügt alle Parameter in der Liste in das {@link PreparedStatement} ab der gegebenen Position ein.
	 *
	 * @param pst      das {@link PreparedStatement}, in welches eingefügt werden soll
	 * @param position die Position ab der eingefügt werden soll
	 * @return die Position hinter dem zuletzt eingefügten Parameter
	 * @throws SQLException Wenn eine {@link SQLException} aufgetreten ist
	 */
	public int setParameter(final PreparedStatement pst, final int position) throws SQLException {
		int newPosition = position;
		for (Parameter param : this) {
			newPosition = param.setParameter(pst, newPosition);
		}
		return newPosition;
	}
	
	/**
	 * Fügt alle Objekte der Liste an. Wenn ein Objekt nicht vom Typ Parameter ist, wird dieser erstellt und darf nicht {@code null} sein.
	 *
	 * @param objects Alle Objekte, die angefügt werden sollen
	 * @return sich selbst
	 */
	public ParameterList addParameter(final Object... objects) {
		for (Object o : objects) {
			if (o instanceof Parameter) {
				add((Parameter) o);
			}
			else if (o instanceof ParameterList) {
				addAll((ParameterList) o);
			}
			else {
				addParameter(new Parameter(o));
			}
		}
		return this;
	}
}
