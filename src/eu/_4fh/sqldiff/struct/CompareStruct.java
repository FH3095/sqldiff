package eu._4fh.sqldiff.struct;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import eu._4fh.sqldiff.Main;
import eu._4fh.sqldiff.data.Column;
import eu._4fh.sqldiff.data.SqlType;
import eu._4fh.sqldiff.data.Table;

public class CompareStruct {
	private StringBuffer sqlChange;

	public CompareStruct() {
		sqlChange = new StringBuffer(512);
	}

	private Map<String, Table> getTables(DatabaseMetaData meta)
			throws SQLException {
		Map<String, Table> tables = new HashMap<String, Table>();
		ResultSet rs = meta.getTables("", null, "", new String[] { "TABLE" });
		while (rs.next()) {
			Table table = new Table(meta, rs);
			if (tables.containsKey(table.getName())) {
				throw new RuntimeException(
						"Found same table twice (different schema/catalog?). Old="
								+ tables.get(table.getName()).toString()
								+ " New=" + table.toString());
			}
			tables.put(table.getName(), table);
		}
		rs.close();

		return tables;
	}

	public void compare(Connection con1, Connection con2) throws SQLException {
		DatabaseMetaData meta1 = con1.getMetaData();
		DatabaseMetaData meta2 = con2.getMetaData();

		Map<String, Table> tables1 = getTables(meta1);
		Map<String, Table> tables2 = getTables(meta2);

		Iterator<Table> it = tables1.values().iterator();
		while (it.hasNext()) {
			Table table = it.next();
			if (tables2.containsKey(table.getName())) {
				compareTables(table, tables2.get(table.getName()));
			} else {
				createTable(table);
			}
		}

		it = tables2.values().iterator();
		while (it.hasNext()) {
			Table table = it.next();
			if (!tables1.containsKey(table.getName())) {
				dropTable(table);
			}
		}
		System.out.println(sqlChange.toString());
	}

	private void dropTable(Table table) {
		sqlChange.append("DROP TABLE ").append(table.getName()).append(';')
				.append(Main.nl);
	}

	private void createTable(Table table) {
		sqlChange.append("CREATE TABLE ").append(table.getName()).append(" (")
				.append(Main.nl);
		List<Column> columns = table.getColumns();
		for (Column column : columns) {
			sqlChange.append(column.getName()).append(" ")
					.append(column.getTypeName()).append(" ");

			SqlType type = SqlType.findByJavaSqlType(column.getType());
			if (type != null && type.needSize) {
				sqlChange.append("( ").append(column.getSize());
				if (type.needDecimalSize) {
					sqlChange.append(" , ").append(column.getDecimalDigits());
				}
				sqlChange.append(" ) ");
			}

			sqlChange
					.append(column.isNullable() ? "NULL" : "NOT NULL")
					.append(" ")
					.append(column.isAutoIncrement() ? "AUTO_INCREMENT" : "")
					.append(column.getDefaultValue() != null ? "DEFAULT '"
							+ column.getDefaultValue() + "'" : "").append(" ");
			sqlChange.append(Main.nl);
		}
		sqlChange.append(");").append(Main.nl);

		// Primary Key
		if (table.getPrimaryKey() != null) {
			sqlChange.append("ALTER TABLE ").append(table.getName())
					.append(" ADD PRIMARY KEY (");
			boolean first = true;
			for (Column column : table.getPrimaryKey().getColumns()) {
				if (!first) {
					sqlChange.append(" , ");
				}
				first = false;
				sqlChange.append(column.getName());
			}
			sqlChange.append(");").append(Main.nl);
		}

		sqlChange.append(Main.nl);
	}

	private void compareTables(Table table, Table table2) {
		// TODO Auto-generated method stub
	}
}
