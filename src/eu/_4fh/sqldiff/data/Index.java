package eu._4fh.sqldiff.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Index {
	private String name;
	private List<Column> columns;

	public Index(String name, Column[] columns) {
		this.name = name;
		this.columns = Collections.unmodifiableList(new ArrayList<Column>(
				Arrays.asList(columns)));
	}

	public String getName() {
		return name;
	}

	public List<Column> getColumns() {
		return columns;
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer(128);
		buf.append("Index [").append(name).append(" (");
		boolean first = true;
		for (Column column : columns) {
			if (!first) {
				buf.append(" , ");
			}
			buf.append(column.getName());
			first = false;
		}
		buf.append(")]");
		return buf.toString();
	}
}
