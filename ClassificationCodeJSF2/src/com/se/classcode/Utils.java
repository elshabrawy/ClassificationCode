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
			// String url = "jdbc:oracle:thin:@develop-test:1521:xlp";
			//
			// //properties for creating connection to Oracle database
			// Properties props = new Properties();
			// props.setProperty("user", "cm");
			// props.setProperty("password", "cmpwd");
			//
			// //creating connection to Oracle database using JDBC
			// conn = DriverManager.getConnection(url,props);

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
		} finally {
			// try {
			// in.close();
			// } catch (Exception e) {
			// e.printStackTrace();
			// }
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

	public static void main(String args[]) {
		Connection con = connectDatabase();
		try {
			Statement st = con.createStatement();
			ResultSet rs = st
					.executeQuery("select * from cm.part_code where COM_ID = 45631987");
			while (rs.next()) {
				System.out.println(rs.getString("com_id"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}