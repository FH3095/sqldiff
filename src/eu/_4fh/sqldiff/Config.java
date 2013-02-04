package eu._4fh.sqldiff;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {
	private Properties props;

	public Config() {
		props = new Properties();
	}

	private void writeDefaultConfig(File configFile) {
		if (configFile.exists() && configFile.canRead() && configFile.isFile()) {
			return;
		}
		props.put("sqlDriver", "");
		props.put("sql1Url", "jdbc:mysql://localhost/");
		props.put("sql1User", "");
		props.put("sql1Pass", "");
		props.put("sql2Url", "jdbc:mysql://localhost/");
		props.put("sql2User", "");
		props.put("sql2Pass", "");
		try {
			props.store(new FileOutputStream(configFile), "");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void readConfig(File configFile) {
		writeDefaultConfig(configFile);
		try {
			props.load(new FileInputStream(configFile));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Deprecated
	public String getOption(String key) {
		return props.getProperty(key, null);
	}
}
