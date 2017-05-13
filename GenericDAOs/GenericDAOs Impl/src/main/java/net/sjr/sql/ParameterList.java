package net.sjr.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;

public class ParameterList extends LinkedList<Parameter> {

	/**
	 * Erstellt eine neue Parameterliste.
	 *
	 * @param objects Objekte, die von Anfang an in der Liste vorhanden sein sollen
	 */
	public ParameterList(final Object... objects) {
		addParameter(objects);
	}

	/**
	 * Fügt alle Parameter in der Liste in das PreparedStatement ab der gegebenen Position ein.
	 *
	 * @param pst      das PreparedStatement, in welches eingefügt werden soll
	 * @param position die Position ab der eingefügt werden soll
	 * @return die Position hinter dem zuletzt eingefügten Parameter
	 * @throws SQLException
	 */
	public int setParameter(final PreparedStatement pst, final int position) throws SQLException {
		int newPosition = position;
		for (Parameter param : this) {
			newPosition = param.setParameter(pst, newPosition);
		}
		return newPosition;
	}

	/**
	 * Fügt alle Objekte der Liste an. Wenn ein Objekt nicht vom Typ Parameter ist, wird dieser erstellt und darf nicht null sein.
	 *
	 * @param objects Alle Objekte, die angefügt werden sollen
	 */
	public void addParameter(final Object... objects) {
		for (Object o : objects) {
			if (o instanceof Parameter) {
				add((Parameter) o);
			}
			else {
				addParameter(new Parameter(o));
			}
		}
	}
}
