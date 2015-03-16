package com.se.classcode;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import com.mchange.v2.c3p0.ComboPooledDataSource;

// =======================================================================================
// ********************************* Sherif ELSbaey **************************************
// =======================================EIVA============================================

/**
 * @author sherif_elsbaey
 */
public class Utils {
	private static ComboPooledDataSource dataSource;

	private static void initDS() {
		dataSource = new ComboPooledDataSource();
		try {
			String driver = "oracle.jdbc.driver.OracleDriver";
			String url = "jdbc:oracle:thin:@develop-test:1521:xlp";
			dataSource.setDriverClass(driver);
			dataSource.setJdbcUrl(url);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static Connection getConnection() throws SQLException {
		Connection conn = null;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
			String URL = "jdbc:oracle:thin:@develop-test:1521:xlp";
			String USER = "cm";
			String PASS = "cmpwd";
			conn = DriverManager.getConnection(URL, USER, PASS);
			

		} catch (Exception e) {
		}

		return conn;
	}

	public static Connection connectDatabase() {

		Connection connection = null;

		try {
			connection = getConnection();

		} catch (Exception e) {
			e.printStackTrace();
		} 
		return connection;
	}

	public static String createDirector(String directoryName) {
		File f = new File(directoryName);
		if (!f.isDirectory())
			f.mkdirs();
		return directoryName;

	}

	public static String getProperty(String property) {
		System.out.println("property is " + property);
		String pathOnServer = "";
		InputStream in = null;
		try {
			Properties props = new Properties();
			in = new FileInputStream(
					"C:\\session\\ClassificationTools.properties");
			props.load(in);
			pathOnServer = props.getProperty(property).trim();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return pathOnServer;
	}

	
}