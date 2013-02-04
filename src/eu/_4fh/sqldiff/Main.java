package eu._4fh.sqldiff;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import eu._4fh.sqldiff.struct.CompareStruct;

public class Main {
	public static final String nl = "\n";
	private static Main myInstance;
	private Config config;

	public static void main(String[] args) throws SQLException {
		myInstance = new Main();
		Main.instance().run(args);
	}

	private Main() {
		config = new Config();
	}

	private void run(String[] args) throws SQLException {
		config.readConfig(new File("props.conf"));
		Connection con1 = DriverManager.getConnection(
				config.getOption("sql1Url"), config.getOption("sql1User"),
				config.getOption("sql1Pass"));
		Connection con2 = DriverManager.getConnection(
				config.getOption("sql2Url"), config.getOption("sql2User"),
				config.getOption("sql2Pass"));
		new CompareStruct().compare(con1, con2);
	}

	public static Main instance() {
		return myInstance;
	}
}
