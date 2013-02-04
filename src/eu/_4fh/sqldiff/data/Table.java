package eu._4fh.sqldiff.data;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Table {
	private String name;
	private String catalog;
	private String schema;
	private Map<String, Column> columns;
	private Index primaryKey;

	public Table(DatabaseMetaData meta, ResultSet tableDefinition)
			throws SQLException {
		name = tableDefinition.getString(3);
		catalog = tableDefinition.getString(1);
		schema = tableDefinition.getString(2);

		columns = new HashMap<String, Column>();

		ResultSet rs = meta.getColumns(catalog, null, name, "");
		while (rs.next()) {
			Column column = new Column(rs);
			columns.put(column.getName(), column);
		}
		rs.close();
		columns = Collections.unmodifiableMap(columns);

		rs = meta.getPrimaryKeys(catalog, null, name);
		List<Column> pkColumns = new LinkedList<Column>();
		String pkName = null;
		while (rs.next()) {
			if (pkName != null) {
				if (!pkName.equals(rs.getString(6))) {
					throw new RuntimeException(
							"Primary Key has different names? oO Old=" + pkName
									+ " New=" + rs.getString(6));
				}
			}
			pkName = rs.getString(6);
			pkColumns.add(columns.get(rs.getString(4)));
		}
		primaryKey = new Index(pkName, pkColumns.toArray(new Column[1]));
	}

	public String getName() {
		return name;
	}

	public String getCatalog() {
		return catalog;
	}

	@Deprecated
	public String getSchema() {
		return schema;
	}

	public Column getColumn(int i) {
		return columns.get(i);
	}

	public Map<String, Column> getColumns() {
		return columns;
	}

	public Index getPrimaryKey() {
		return primaryKey;
	}

	@Override
	public String toString() {
		return "Table [" + catalog + "." + name + "] @Schema=" + schema;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((catalog == null) ? 0 : catalog.hashCode());
		result = prime * result + ((columns == null) ? 0 : columns.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((schema == null) ? 0 : schema.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Table)) {
			return false;
		}
		Table other = (Table) obj;
		if (catalog == null) {
			if (other.catalog != null) {
				return false;
			}
		} else if (!catalog.equals(other.catalog)) {
			return false;
		}
		if (columns == null) {
			if (other.columns != null) {
				return false;
			}
		} else if (!columns.equals(other.columns)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (schema == null) {
			if (other.schema != null) {
				return false;
			}
		} else if (!schema.equals(other.schema)) {
			return false;
		}
		return true;
	}
}
