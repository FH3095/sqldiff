package eu._4fh.sqldiff.data;

import java.lang.reflect.Field;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;
import java.util.TreeMap;

public class Column implements Comparable<Column> {
	private static Map<String, Integer> sqlTypes;
	static {
		readSqlTypes();
	}
	private String name;
	private int type;
	private String typeName;
	private int size;
	private int decimalDigits;
	private boolean nullable;
	private String defaultValue;
	private int pos;
	private boolean autoIncrement;

	public Column(ResultSet definition) throws SQLException {
		name = definition.getString(4);
		type = definition.getInt(5);
		typeName = definition.getString(6);
		size = definition.getInt(7);
		decimalDigits = definition.getInt(9);
		nullable = definition.getInt(11) == DatabaseMetaData.columnNullable;
		defaultValue = definition.getString(13);
		pos = definition.getInt(17);
		autoIncrement = definition.getString(23).equalsIgnoreCase("YES");
	}

	@Override
	public String toString() {
		return "Column ["
				+ name
				+ " "
				+ typeName
				+ " ( "
				+ size
				+ " , "
				+ decimalDigits
				+ " ) "
				+ (nullable ? "NULL" : "NOT NULL")
				+ " "
				+ (defaultValue != null ? "DEFAULT '" + defaultValue + "' "
						: "") + (autoIncrement ? "AUTO_INCREMENT" : "")
				+ "] at Pos " + pos;
	}

	public String getName() {
		return name;
	}

	public int getType() {
		return type;
	}

	public String getTypeName() {
		return typeName;
	}

	public int getSize() {
		return size;
	}

	public int getDecimalDigits() {
		return decimalDigits;
	}

	public boolean isNullable() {
		return nullable;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public int getPos() {
		return pos;
	}

	public boolean isAutoIncrement() {
		return autoIncrement;
	}

	private static void readSqlTypes() {
		sqlTypes = new TreeMap<String, Integer>();
		Field[] fields = Types.class.getFields();
		for (Field field : fields) {
			if (!field.getType().getCanonicalName().equals("int")) {
				continue;
			}
			try {
				sqlTypes.put(field.getName(), field.getInt(null));
			} catch (IllegalArgumentException e) {
				// Ignore, we only want static fields anyway.
			} catch (IllegalAccessException e) {
				throw new RuntimeException("Can't access field java.sql.Types."
						+ field.getName(), e);
			}
		}
		if (sqlTypes.isEmpty()) {
			throw new RuntimeException(
					"Can't find any type in java.sql.Types. Has this class changed?");
		}
	}

	@Override
	public int compareTo(Column o) {
		return new Integer(this.getPos()).compareTo(o.getPos());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (autoIncrement ? 1231 : 1237);
		result = prime * result + decimalDigits;
		result = prime * result
				+ ((defaultValue == null) ? 0 : defaultValue.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + (nullable ? 1231 : 1237);
		result = prime * result + pos;
		result = prime * result + size;
		result = prime * result + type;
		result = prime * result
				+ ((typeName == null) ? 0 : typeName.hashCode());
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
		if (!(obj instanceof Column)) {
			return false;
		}
		Column other = (Column) obj;
		if (autoIncrement != other.autoIncrement) {
			return false;
		}
		if (decimalDigits != other.decimalDigits) {
			return false;
		}
		if (defaultValue == null) {
			if (other.defaultValue != null) {
				return false;
			}
		} else if (!defaultValue.equals(other.defaultValue)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (nullable != other.nullable) {
			return false;
		}
		if (pos != other.pos) {
			return false;
		}
		if (size != other.size) {
			return false;
		}
		if (type != other.type) {
			return false;
		}
		if (typeName == null) {
			if (other.typeName != null) {
				return false;
			}
		} else if (!typeName.equals(other.typeName)) {
			return false;
		}
		return true;
	}
}
