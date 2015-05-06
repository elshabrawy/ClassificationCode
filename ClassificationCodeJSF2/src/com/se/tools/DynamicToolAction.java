package com.se.tools;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.se.classcode.Utils;

public class DynamicToolAction

{

	public String importRuleAction(String fileName,
			ArrayList<ArrayList<String>> txtDataList)

	{
		HashSet<String> pls = new HashSet<String>();
		System.out.println(txtDataList.size());
		String status = "";

		String Url = "";

		String queryIns = "";

		String queryDel = "";

		Connection con = null;

		PreparedStatement pstmtIns = null;

		PreparedStatement pstmtDel = null;
		Statement st = null;
		ResultSet rs = null;
		Statement st2 = null;
		ResultSet rs2 = null;
		Statement insertStatment = null;
		BufferedWriter writeToFile = null;
		ArrayList<String> plChk = new ArrayList<String>();

		try

		{

			con = Utils.connectDatabase();
			insertStatment = con.createStatement();
			String directory = Utils.createDirector(log);

			Url = (new StringBuilder(String.valueOf(log)))
					.append("DynamicGenRulesTools\\")
					.append(System.currentTimeMillis()).append(fileName)
					.append(".txt").toString();

			String plName = "";

			String className = "";

			String classVer = "";

			String executionOrd = "";
			String clasId = "";
			String pl = "";
			String pdfURL = "";
			String rufUrlId = "";

			writeToFile = new BufferedWriter(new FileWriter(fileName ,
					true));

			writeToFile
					.append("SE Product Line\tCode Name\tCode Version\tExecution Order(Exit when fired)\tStatus");

			writeToFile.newLine();

			queryIns = ("insert into importer.CLAS_ROLLS (SE_PRODUCT_LINE,CLASS_NAME,CODE_VERSION,CODE_DESCRIPTION,EXECUTION_ORDER,CODE,SE_PARAMETERS_NAME_1,START_COND_1,START_VALUE_1,START_MULTIPLIER_1,START_UNIT_1,END_COND_1,END_VALUE_1,END_MULTIPLIER_1,END_UNIT_1,SE_PARAMETERS_NAME_2,START_COND_2,START_VALUE_2,START_MULTIPLIER_2,START_UNIT_2,END_COND_2,END_VALUE_2,END_MULTIPLIER_2,END_UNIT_2,SE_PARAMETERS_NAME_3,START_COND_3,START_VALUE_3,START_MULTIPLIER_3,START_UNIT_3,END_COND_3,END_VALUE_3,END_MULTIPLIER_3,END_UNIT_3,SE_PARAMETERS_NAME_4,START_COND_4,START_VALUE_4,START_MULTIPLIER_4,START_UNIT_4,END_COND_4,END_VALUE_4,END_MULTIPLIER_4,END_UNIT_4,SE_PARAMETERS_NAME_5,START_COND_5,START_VALUE_5,START_MULTIPLIER_5,START_UNIT_5,END_COND_5,END_VALUE_5,END_MULTIPLIER_5,END_UNIT_5,ReF_URL,CLAS_ID,pl_id,ruf_url_id) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

			System.out.println(queryIns);

			queryDel = ("delete importer.CLAS_ROLLS where SE_PRODUCT_LINE=? AND CLASS_NAME=? AND CODE_VERSION=? ");

			pstmtIns = con.prepareStatement(queryIns);

			pstmtDel = con.prepareStatement(queryDel);
			txtDataList.remove(0);
			DD: for (ArrayList<String> row : txtDataList)

			{
				writeToFile = new BufferedWriter(new FileWriter(fileName, true));
				clasId = "";
				try

				{

					plName = (row.get(0) == null) ? "" : row.get(0).replaceAll(
							"\"", "");

					className = (row.get(1) == null) ? "" : row.get(1);

					classVer = (row.get(2) == null) ? "" : row.get(2);
					st = con.createStatement();
					String sql = "select CLAS_ID from importer.CLASSIFICATION_CODE where   CLAS_NAME ='"
							+ className + "' and CLAS_VER='" + classVer + "'";
					System.out.println(sql);
					rs = st.executeQuery("select CLAS_ID from importer.CLASSIFICATION_CODE where   CLAS_NAME ='"
							+ className + "' and CLAS_VER='" + classVer + "'");
					while (rs.next()) {
						clasId = rs.getString("CLAS_ID");
						System.out.println("" + clasId);
					}
					st.close();
					rs.close();

					st2 = con.createStatement();
					rs2 = st2.executeQuery("select cm.get_pl_id('" + plName
							+ "') pl from dual");
					while (rs2.next()) {
						pl = rs2.getString("pl");
					}
					st2.close();
					rs2.close();

					// "select cm.get_pl_id('ALU')from dual";

					executionOrd = (row.get(5) == null) ? "" : row.get(5);
					if (clasId.equals("")) {

						status = "Error Code name or version";

					} else if (plName.equalsIgnoreCase(""))

						status = "Can't Insert Product Line By Null";
					else if (pl.equals("-1"))
						status = "Can't Insert Error Product Line";
					else if (className.equalsIgnoreCase("")
							&& classVer.equalsIgnoreCase(""))

					{

						status = "Can't Insert Code Name & Code Version By Null";

					} else {

						if (!plChk.contains(plName + className + classVer))

						{

							pstmtDel.setString(1, plName);

							pstmtDel.setString(2, className);

							pstmtDel.setString(3, classVer);

							pstmtDel.executeUpdate();

							plChk.add(plName + className + classVer);

						}

						System.out.println(plName.replace("\"", ""));
						pstmtIns.setString(1, plName.replace("\"", ""));

						pstmtIns.setString(2, className);

						pstmtIns.setString(3, classVer);

						pstmtIns.setString(4,
								(row.get(3) == null) ? "" : row.get(3));

						pstmtIns.setString(5, executionOrd);
						System.out.println(executionOrd);

						pdfURL = (row.get(4) == null) ? "" : row.get(4);
						if (!pdfURL.equals("")) {
							st = con.createStatement();
							rs = st.executeQuery("select importer.GET_PDF_ID_By_URL('"
									+ pdfURL + "') pdf from dual");
							while (rs.next()) {
								rufUrlId = (rs.getString("pdf") == null) ? null
										: rs.getString("pdf").toString();
								System.out.println("pdf url is " + pdfURL);
							}
							if (rufUrlId == null || rufUrlId.equals("")) {
								writeToFile.append((new StringBuilder(String
										.valueOf(plName))).append("\t")
										.append(className).append("\t")
										.append(classVer).append("\t")
										.append(executionOrd).append("\t")
										.append("Please insert Offline URL")
										.toString());

								writeToFile.newLine();
								continue DD;
							}
						}
						pstmtIns.setString(6,
								(row.get(6) == null) ? "" : row.get(6));

						pstmtIns.setString(7,
								(row.get(7) == null) ? "" : row.get(7));

						pstmtIns.setString(8,
								(row.get(8) == null) ? "" : row.get(8));

						pstmtIns.setString(9,
								(row.get(9) == null) ? "" : row.get(9));

						pstmtIns.setString(10,
								(row.get(10) == null) ? "" : row.get(10));

						pstmtIns.setString(11,
								(row.get(11) == null) ? "" : row.get(11));

						pstmtIns.setString(12,
								(row.get(12) == null) ? "" : row.get(12));

						pstmtIns.setString(13,
								(row.get(13) == null) ? "" : row.get(13));

						pstmtIns.setString(14,
								(row.get(14) == null) ? "" : row.get(14));

						pstmtIns.setString(15,
								(row.get(15) == null) ? "" : row.get(15));

						pstmtIns.setString(16,
								(row.get(16) == null) ? "" : row.get(16));

						pstmtIns.setString(17,
								(row.get(17) == null) ? "" : row.get(17));

						pstmtIns.setString(18,
								(row.get(18) == null) ? "" : row.get(18));

						pstmtIns.setString(19,
								(row.get(19) == null) ? "" : row.get(19));

						pstmtIns.setString(20,
								(row.get(20) == null) ? "" : row.get(20));

						pstmtIns.setString(21,
								(row.get(21) == null) ? "" : row.get(21));

						pstmtIns.setString(22,
								(row.get(22) == null) ? "" : row.get(22));

						pstmtIns.setString(23,
								(row.get(23) == null) ? "" : row.get(23));

						pstmtIns.setString(24,
								(row.get(24) == null) ? "" : row.get(24));

						pstmtIns.setString(25,
								(row.get(25) == null) ? "" : row.get(25));

						pstmtIns.setString(26,
								(row.get(26) == null) ? "" : row.get(26));

						pstmtIns.setString(27,
								(row.get(27) == null) ? "" : row.get(27));

						pstmtIns.setString(28,
								(row.get(28) == null) ? "" : row.get(28));

						pstmtIns.setString(29,
								(row.get(29) == null) ? "" : row.get(29));

						pstmtIns.setString(30,
								(row.get(30) == null) ? "" : row.get(30));

						pstmtIns.setString(31,
								(row.get(31) == null) ? "" : row.get(31));

						pstmtIns.setString(32,
								(row.get(32) == null) ? "" : row.get(32));

						pstmtIns.setString(33,
								(row.get(33) == null) ? "" : row.get(33));

						pstmtIns.setString(34,
								(row.get(34) == null) ? "" : row.get(34));

						pstmtIns.setString(35,
								(row.get(35) == null) ? "" : row.get(35));

						pstmtIns.setString(36,
								(row.get(36) == null) ? "" : row.get(36));

						pstmtIns.setString(37,
								(row.get(37) == null) ? "" : row.get(37));

						pstmtIns.setString(38,
								(row.get(38) == null) ? "" : row.get(38));

						pstmtIns.setString(39,
								(row.get(39) == null) ? "" : row.get(39));

						pstmtIns.setString(40,
								(row.get(40) == null) ? "" : row.get(40));

						pstmtIns.setString(41,
								(row.get(41) == null) ? "" : row.get(41));

						pstmtIns.setString(42,
								(row.get(42) == null) ? "" : row.get(42));

						pstmtIns.setString(43,
								(row.get(43) == null) ? "" : row.get(43));

						pstmtIns.setString(44,
								(row.get(44) == null) ? "" : row.get(44));

						pstmtIns.setString(45,
								(row.get(45) == null) ? "" : row.get(45));

						pstmtIns.setString(46,
								(row.get(46) == null) ? "" : row.get(46));

						pstmtIns.setString(47,
								(row.get(47) == null) ? "" : row.get(47));

						pstmtIns.setString(48,
								(row.get(48) == null) ? "" : row.get(48));

						pstmtIns.setString(49,
								(row.get(49) == null) ? "" : row.get(49));

						pstmtIns.setString(50,
								(row.get(50) == null) ? "" : row.get(50));

						pstmtIns.setString(51,
								(row.get(6) == null) ? "" : row.get(6));
						pstmtIns.setString(52,
								(row.get(4) == null) ? "" : row.get(4));
						pstmtIns.setString(53, (clasId));
						pstmtIns.setString(54, (pl));
						pstmtIns.setString(55, (rufUrlId));
						pstmtIns.executeUpdate();

						status = "Inserted";

					}

				}

				catch (Exception e)

				{

					e.printStackTrace();

				}
				if (pls.add(pl + "," + clasId) && !clasId.equals("")) {
					insertStatment
							.executeUpdate("insert into importer.generated_pl(pl_id,status,class_id) values ("
									+ pl + ",0," + clasId + ")");
				}
				writeToFile.append((new StringBuilder(String.valueOf(plName)))
						.append("\t").append(className).append("\t")
						.append(classVer).append("\t").append(executionOrd)
						.append("\t").append(status).toString());

				writeToFile.newLine();
				writeToFile.close();
			}

			con.commit();

			pstmtIns.close();

			pstmtDel.close();

			con.close();

		}

		catch (Exception e)

		{

			e.printStackTrace();

			try

			{

				con.close();

			}

			catch (SQLException e1)

			{

				e1.printStackTrace();

			}

		} finally {
			try {
				writeToFile.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return Url;

	}

	public String deleteRadRuleAction() {
		String Url = "";
		Connection con = Utils.connectDatabase();
		Statement deleteStatement;

		try {
			deleteStatement = con.createStatement();
			deleteStatement.executeUpdate("delete from importer.rad_rolls");
			deleteStatement.close();
			con.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Done";
	}

	public String exportRuleAction(String fileName,
			ArrayList<ArrayList<String>> txtDataList)

	{
		String Url = "";
		String query = "";
		Connection con = null;
		ResultSet rSet = null;
		PreparedStatement pstmt = null;
		Statement st = null;
		try {
			String className = "";
			String codeVer = "";
			String plName = "";
			String directory = Utils.createDirector(log);
			Url = (new StringBuilder(String.valueOf(log)))
					.append("DynamicGenRulesTools\\")
					.append(System.currentTimeMillis()).append(fileName)
					.append(".txt").toString();
			BufferedWriter writeToFile = new BufferedWriter(new FileWriter(fileName, true));

			con = Utils.connectDatabase();
			st = con.createStatement();
			writeToFile
					.append("SE Product Line\tCode Name\tCode Version\tCode Description\tREF_URL\tExecution Order(Exit when fired)\tCode\tSE Parameters Name 1\tStart Cond1\tStart Value1\tStart Multiplier1\tStart Unit1\tEnd Cond1\tEnd Value1\tEnd Multiplier1\tEnd Unit1\tSE Parameters Name 2\tStart Cond2\tStart Value2\tStart Multiplier2\tStart Unit2\tEnd Cond2\tEnd Value2\tEnd Multiplier2\tEnd Unit2\tSE Parameters Name 3\tStart Cond3\tStart Value3\tStart Multiplier3\tStart Unit3\tEnd Cond3\tEnd Value3\tEnd Multiplier3\tEnd Unit3\tSE Parameters Name 4\tStart Cond4\tStart Value4\tStart Multiplier4\tStart Unit4\tEnd Cond4\tEnd Value4\tEnd Multiplier4\tEnd Unit4\tSE Parameters Name 5\tStart Cond5\tStart Value5\tStart Multiplier5\tStart Unit5\tEnd Cond5\tEnd Value5\tEnd Multiplier5\tEnd Unit5");

			writeToFile.newLine();

			// query =
			// ("select SE_PRODUCT_LINE,CLASS_NAME,CODE_VERSION,CODE_DESCRIPTION,REF_URL,EXECUTION_ORDER,CODE,SE_PARAMETERS_NAME_1,START_COND_1,START_VALUE_1,START_MULTIPLIER_1,START_UNIT_1,END_COND_1,END_VALUE_1,END_MULTIPLIER_1,END_UNIT_1,SE_PARAMETERS_NAME_2,START_COND_2,START_VALUE_2,START_MULTIPLIER_2,START_UNIT_2,END_COND_2,END_VALUE_2,END_MULTIPLIER_2,END_UNIT_2,SE_PARAMETERS_NAME_3,START_COND_3,START_VALUE_3,START_MULTIPLIER_3,START_UNIT_3,END_COND_3,END_VALUE_3,END_MULTIPLIER_3,END_UNIT_3,SE_PARAMETERS_NAME_4,START_COND_4,START_VALUE_4,START_MULTIPLIER_4,START_UNIT_4,END_COND_4,END_VALUE_4,END_MULTIPLIER_4,END_UNIT_4,SE_PARAMETERS_NAME_5,START_COND_5,START_VALUE_5,START_MULTIPLIER_5,START_UNIT_5,END_COND_5,END_VALUE_5,END_MULTIPLIER_5,END_UNIT_5 from importer.CLAS_ROLLS where CLASS_NAME=? and CODE_VERSION=? order by SE_PRODUCT_LINE,EXECUTION_ORDER");

			// pstmt = con.prepareStatement(query);

			for (int row = 1; row < txtDataList.size(); row++)

				try

				{
					writeToFile = new BufferedWriter(new FileWriter(fileName, true));
					plName = ((String) ((List) txtDataList.get(row)).get(0))
							.trim().replaceAll("\"", "");

					className = ((String) ((List) txtDataList.get(row)).get(1))
							.trim();

					codeVer = ((String) ((List) txtDataList.get(row)).get(2))
							.trim();
					query = ("select SE_PRODUCT_LINE,CLASS_NAME,CODE_VERSION,CODE_DESCRIPTION,REF_URL,EXECUTION_ORDER,CODE,SE_PARAMETERS_NAME_1,START_COND_1,START_VALUE_1,START_MULTIPLIER_1,START_UNIT_1,END_COND_1,END_VALUE_1,END_MULTIPLIER_1,END_UNIT_1,SE_PARAMETERS_NAME_2,START_COND_2,START_VALUE_2,START_MULTIPLIER_2,START_UNIT_2,END_COND_2,END_VALUE_2,END_MULTIPLIER_2,END_UNIT_2,SE_PARAMETERS_NAME_3,START_COND_3,START_VALUE_3,START_MULTIPLIER_3,START_UNIT_3,END_COND_3,END_VALUE_3,END_MULTIPLIER_3,END_UNIT_3,SE_PARAMETERS_NAME_4,START_COND_4,START_VALUE_4,START_MULTIPLIER_4,START_UNIT_4,END_COND_4,END_VALUE_4,END_MULTIPLIER_4,END_UNIT_4,SE_PARAMETERS_NAME_5,START_COND_5,START_VALUE_5,START_MULTIPLIER_5,START_UNIT_5,END_COND_5,END_VALUE_5,END_MULTIPLIER_5,END_UNIT_5 from importer.CLAS_ROLLS where CLASS_NAME='"
							+ className
							+ "' and CODE_VERSION='"
							+ codeVer
							+ "' and SE_PRODUCT_LINE='" + plName + "' order by SE_PRODUCT_LINE,EXECUTION_ORDER");
					System.out.println(query);
					// pstmt.setString(1, className);

					// pstmt.setString(2, codeVer);
					rSet = st.executeQuery(query);

					while (rSet.next()) {
						// for (rSet = pstmt.executeQuery(); rSet.next();
						// writeToFile
						// .newLine())

						writeToFile.append((new StringBuilder(String
								.valueOf(rSet.getString(1) == null ? ""
										: ((Object) (rSet.getString(1))))))
								.append("\t")
								.append(rSet.getString(2) == null ? "" : rSet
										.getString(2))
								.append("\t")
								.append(rSet.getString(3) == null ? "" : rSet
										.getString(3))
								.append("\t")
								.append(rSet.getString(4) == null ? "" : rSet
										.getString(4))
								.append("\t")
								.append(rSet.getString(5) == null ? "" : rSet
										.getString(5))
								.append("\t")
								.append(rSet.getString(6) == null ? "" : rSet
										.getString(6))
								.append("\t")
								.append(rSet.getString(7) == null ? "" : rSet
										.getString(7))
								.append("\t")
								.append(rSet.getString(8) == null ? "" : rSet
										.getString(8))
								.append("\t")
								.append(rSet.getString(9) == null ? "" : rSet
										.getString(9))
								.append("\t")
								.append(rSet.getString(10) == null ? "" : rSet
										.getString(10))
								.append("\t")
								.append(rSet.getString(11) == null ? "" : rSet
										.getString(11))
								.append("\t")
								.append(rSet.getString(12) == null ? "" : rSet
										.getString(12))
								.append("\t")
								.append(rSet.getString(13) == null ? "" : rSet
										.getString(13))
								.append("\t")
								.append(rSet.getString(14) == null ? "" : rSet
										.getString(14))
								.append("\t")
								.append(rSet.getString(15) == null ? "" : rSet
										.getString(15))
								.append("\t")
								.append(rSet.getString(16) == null ? "" : rSet
										.getString(16))
								.append("\t")
								.append(rSet.getString(17) == null ? "" : rSet
										.getString(17))
								.append("\t")
								.append(rSet.getString(18) == null ? "" : rSet
										.getString(18))
								.append("\t")
								.append(rSet.getString(19) == null ? "" : rSet
										.getString(19))
								.append("\t")
								.append(rSet.getString(20) == null ? "" : rSet
										.getString(20))
								.append("\t")
								.append(rSet.getString(21) == null ? "" : rSet
										.getString(21))
								.append("\t")
								.append(rSet.getString(22) == null ? "" : rSet
										.getString(22))
								.append("\t")
								.append(rSet.getString(23) == null ? "" : rSet
										.getString(23))
								.append("\t")
								.append(rSet.getString(24) == null ? "" : rSet
										.getString(24))
								.append("\t")
								.append(rSet.getString(25) == null ? "" : rSet
										.getString(25))
								.append("\t")
								.append(rSet.getString(26) == null ? "" : rSet
										.getString(26))
								.append("\t")
								.append(rSet.getString(27) == null ? "" : rSet
										.getString(27))
								.append("\t")
								.append(rSet.getString(28) == null ? "" : rSet
										.getString(28))
								.append("\t")
								.append(rSet.getString(29) == null ? "" : rSet
										.getString(29))
								.append("\t")
								.append(rSet.getString(30) == null ? "" : rSet
										.getString(30))
								.append("\t")
								.append(rSet.getString(31) == null ? "" : rSet
										.getString(31))
								.append("\t")
								.append(rSet.getString(32) == null ? "" : rSet
										.getString(32))
								.append("\t")
								.append(rSet.getString(33) == null ? "" : rSet
										.getString(33))
								.append("\t")
								.append(rSet.getString(34) == null ? "" : rSet
										.getString(34))
								.append("\t")
								.append(rSet.getString(35) == null ? "" : rSet
										.getString(35))
								.append("\t")
								.append(rSet.getString(36) == null ? "" : rSet
										.getString(36))
								.append("\t")
								.append(rSet.getString(37) == null ? "" : rSet
										.getString(37))
								.append("\t")
								.append(rSet.getString(38) == null ? "" : rSet
										.getString(38))
								.append("\t")
								.append(rSet.getString(39) == null ? "" : rSet
										.getString(39))
								.append("\t")
								.append(rSet.getString(40) == null ? "" : rSet
										.getString(40))
								.append("\t")
								.append(rSet.getString(41) == null ? "" : rSet
										.getString(41))
								.append("\t")
								.append(rSet.getString(42) == null ? "" : rSet
										.getString(42))
								.append("\t")
								.append(rSet.getString(43) == null ? "" : rSet
										.getString(43))
								.append("\t")
								.append(rSet.getString(44) == null ? "" : rSet
										.getString(44))
								.append("\t")
								.append(rSet.getString(45) == null ? "" : rSet
										.getString(45))
								.append("\t")
								.append(rSet.getString(46) == null ? "" : rSet
										.getString(46))
								.append("\t")
								.append(rSet.getString(47) == null ? "" : rSet
										.getString(47))
								.append("\t")
								.append(rSet.getString(48) == null ? "" : rSet
										.getString(48))
								.append("\t")
								.append(rSet.getString(49) == null ? "" : rSet
										.getString(49))
								.append("\t")
								.append(rSet.getString(50) == null ? "" : rSet
										.getString(50))
								.append("\t")
								.append(rSet.getString(51) == null ? "" : rSet
										.getString(51)).toString());
						writeToFile.newLine();
					}
					rSet.close();
					writeToFile.close();
				}

				catch (Exception e)

				{

					rSet.close();

					e.printStackTrace();

				}

			// pstmt.close();

			con.close();

		}

		catch (Exception e)

		{

			e.printStackTrace();

			try

			{

				con.close();

			}

			catch (SQLException e1)

			{

				e1.printStackTrace();

			}

		}

		return Url;

	}

	public String exportRulePLAction(String fileName,
			ArrayList<ArrayList<String>> txtDataList)

	{
		String Url = "";
		String query = "";
		String pl = "";
		Connection con = null;
		ResultSet rSet = null;
		PreparedStatement pstmt = null;
		try {
			String className = "";
			String codeVer = "";
			String directory = Utils.createDirector(log);

			Url = (new StringBuilder(String.valueOf(log)))
					.append("DynamicGenRulesTools\\")
					.append(System.currentTimeMillis()).append(fileName)
					.append(".txt").toString();

			BufferedWriter writeToFile = new BufferedWriter(new FileWriter(fileName, true));

			con = Utils.connectDatabase();

			writeToFile
					.append("SE Product Line\tCode Name\tCode Version\tCode Description\tREF_URL\tExecution Order(Exit when fired)\tCode\tSE Parameters Name 1\tStart Cond1\tStart Value1\tStart Multiplier1\tStart Unit1\tEnd Cond1\tEnd Value1\tEnd Multiplier1\tEnd Unit1\tSE Parameters Name 2\tStart Cond2\tStart Value2\tStart Multiplier2\tStart Unit2\tEnd Cond2\tEnd Value2\tEnd Multiplier2\tEnd Unit2\tSE Parameters Name 3\tStart Cond3\tStart Value3\tStart Multiplier3\tStart Unit3\tEnd Cond3\tEnd Value3\tEnd Multiplier3\tEnd Unit3\tSE Parameters Name 4\tStart Cond4\tStart Value4\tStart Multiplier4\tStart Unit4\tEnd Cond4\tEnd Value4\tEnd Multiplier4\tEnd Unit4\tSE Parameters Name 5\tStart Cond5\tStart Value5\tStart Multiplier5\tStart Unit5\tEnd Cond5\tEnd Value5\tEnd Multiplier5\tEnd Unit5");

			writeToFile.newLine();

			query = (new StringBuilder(
					"select SE_PRODUCT_LINE,CLASS_NAME,CODE_VERSION,CODE_DESCRIPTION,REF_URL,EXECUTION_ORDER,CODE,SE_PARAMETERS_NAME_1,START_COND_1,START_VALUE_1,START_MULTIPLIER_1,START_UNIT_1,END_COND_1,END_VALUE_1,END_MULTIPLIER_1,END_UNIT_1,SE_PARAMETERS_NAME_2,START_COND_2,START_VALUE_2,START_MULTIPLIER_2,START_UNIT_2,END_COND_2,END_VALUE_2,END_MULTIPLIER_2,END_UNIT_2,SE_PARAMETERS_NAME_3,START_COND_3,START_VALUE_3,START_MULTIPLIER_3,START_UNIT_3,END_COND_3,END_VALUE_3,END_MULTIPLIER_3,END_UNIT_3,SE_PARAMETERS_NAME_4,START_COND_4,START_VALUE_4,START_MULTIPLIER_4,START_UNIT_4,END_COND_4,END_VALUE_4,END_MULTIPLIER_4,END_UNIT_4,SE_PARAMETERS_NAME_5,START_COND_5,START_VALUE_5,START_MULTIPLIER_5,START_UNIT_5,END_COND_5,END_VALUE_5,END_MULTIPLIER_5,END_UNIT_5 from "))
					.append(sch)
					.append(".CLAS_ROLLS where SE_PRODUCT_LINE=? and CLASS_NAME=? and CODE_VERSION=? order by SE_PRODUCT_LINE,EXECUTION_ORDER")
					.toString();

			pstmt = con.prepareStatement(query);

			for (int row = 1; row < txtDataList.size(); row++) {
				try

				{
					writeToFile = new BufferedWriter(new FileWriter(fileName, true));
					pl = ((String) ((List) txtDataList.get(row)).get(0)).trim()
							.replaceAll("\"", "");

					className = ((String) ((List) txtDataList.get(row)).get(1))
							.trim();

					codeVer = ((String) ((List) txtDataList.get(row)).get(2))
							.trim();

					pstmt.setString(1, pl);

					pstmt.setString(2, className);

					pstmt.setString(3, codeVer);

					for (rSet = pstmt.executeQuery(); rSet.next(); writeToFile
							.newLine())

						writeToFile.append((new StringBuilder(String
								.valueOf(rSet.getString(1) == null ? ""
										: ((Object) (rSet.getString(1))))))
								.append("\t")
								.append(rSet.getString(2) == null ? "" : rSet
										.getString(2))
								.append("\t")
								.append(rSet.getString(3) == null ? "" : rSet
										.getString(3))
								.append("\t")
								.append(rSet.getString(4) == null ? "" : rSet
										.getString(4))
								.append("\t")
								.append(rSet.getString(5) == null ? "" : rSet
										.getString(5))
								.append("\t")
								.append(rSet.getString(6) == null ? "" : rSet
										.getString(6))
								.append("\t")
								.append(rSet.getString(7) == null ? "" : rSet
										.getString(7))
								.append("\t")
								.append(rSet.getString(8) == null ? "" : rSet
										.getString(8))
								.append("\t")
								.append(rSet.getString(9) == null ? "" : rSet
										.getString(9))
								.append("\t")
								.append(rSet.getString(10) == null ? "" : rSet
										.getString(10))
								.append("\t")
								.append(rSet.getString(11) == null ? "" : rSet
										.getString(11))
								.append("\t")
								.append(rSet.getString(12) == null ? "" : rSet
										.getString(12))
								.append("\t")
								.append(rSet.getString(13) == null ? "" : rSet
										.getString(13))
								.append("\t")
								.append(rSet.getString(14) == null ? "" : rSet
										.getString(14))
								.append("\t")
								.append(rSet.getString(15) == null ? "" : rSet
										.getString(15))
								.append("\t")
								.append(rSet.getString(16) == null ? "" : rSet
										.getString(16))
								.append("\t")
								.append(rSet.getString(17) == null ? "" : rSet
										.getString(17))
								.append("\t")
								.append(rSet.getString(18) == null ? "" : rSet
										.getString(18))
								.append("\t")
								.append(rSet.getString(19) == null ? "" : rSet
										.getString(19))
								.append("\t")
								.append(rSet.getString(20) == null ? "" : rSet
										.getString(20))
								.append("\t")
								.append(rSet.getString(21) == null ? "" : rSet
										.getString(21))
								.append("\t")
								.append(rSet.getString(22) == null ? "" : rSet
										.getString(22))
								.append("\t")
								.append(rSet.getString(23) == null ? "" : rSet
										.getString(23))
								.append("\t")
								.append(rSet.getString(24) == null ? "" : rSet
										.getString(24))
								.append("\t")
								.append(rSet.getString(25) == null ? "" : rSet
										.getString(25))
								.append("\t")
								.append(rSet.getString(26) == null ? "" : rSet
										.getString(26))
								.append("\t")
								.append(rSet.getString(27) == null ? "" : rSet
										.getString(27))
								.append("\t")
								.append(rSet.getString(28) == null ? "" : rSet
										.getString(28))
								.append("\t")
								.append(rSet.getString(29) == null ? "" : rSet
										.getString(29))
								.append("\t")
								.append(rSet.getString(30) == null ? "" : rSet
										.getString(30))
								.append("\t")
								.append(rSet.getString(31) == null ? "" : rSet
										.getString(31))
								.append("\t")
								.append(rSet.getString(32) == null ? "" : rSet
										.getString(32))
								.append("\t")
								.append(rSet.getString(33) == null ? "" : rSet
										.getString(33))
								.append("\t")
								.append(rSet.getString(34) == null ? "" : rSet
										.getString(34))
								.append("\t")
								.append(rSet.getString(35) == null ? "" : rSet
										.getString(35))
								.append("\t")
								.append(rSet.getString(36) == null ? "" : rSet
										.getString(36))
								.append("\t")
								.append(rSet.getString(37) == null ? "" : rSet
										.getString(37))
								.append("\t")
								.append(rSet.getString(38) == null ? "" : rSet
										.getString(38))
								.append("\t")
								.append(rSet.getString(39) == null ? "" : rSet
										.getString(39))
								.append("\t")
								.append(rSet.getString(40) == null ? "" : rSet
										.getString(40))
								.append("\t")
								.append(rSet.getString(41) == null ? "" : rSet
										.getString(41))
								.append("\t")
								.append(rSet.getString(42) == null ? "" : rSet
										.getString(42))
								.append("\t")
								.append(rSet.getString(43) == null ? "" : rSet
										.getString(43))
								.append("\t")
								.append(rSet.getString(44) == null ? "" : rSet
										.getString(44))
								.append("\t")
								.append(rSet.getString(45) == null ? "" : rSet
										.getString(45))
								.append("\t")
								.append(rSet.getString(46) == null ? "" : rSet
										.getString(46))
								.append("\t")
								.append(rSet.getString(47) == null ? "" : rSet
										.getString(47))
								.append("\t")
								.append(rSet.getString(48) == null ? "" : rSet
										.getString(48))
								.append("\t")
								.append(rSet.getString(49) == null ? "" : rSet
										.getString(49))
								.append("\t")
								.append(rSet.getString(50) == null ? "" : rSet
										.getString(50))
								.append("\t")
								.append(rSet.getString(51) == null ? "" : rSet
										.getString(51))
								.append("\t")
								.append(rSet.getString(52) == null ? "" : rSet
										.getString(52)).toString());

					rSet.close();

				}

				catch (Exception e)

				{

					rSet.close();

					e.printStackTrace();

				}
				writeToFile.close();
			}

			pstmt.close();

			con.close();

		}

		catch (Exception e)

		{

			e.printStackTrace();

			try

			{

				con.close();

			}

			catch (SQLException e1)

			{

				e1.printStackTrace();

			}

		}

		return Url;

	}

	public String importRadRuleAction(String fileName,
			ArrayList<ArrayList<String>> txtDataList) {
		String status = "";
		String Url = "";
		String queryIns = "";
		String queryDel = "";
		Connection con = null;
		PreparedStatement pstmtIns = null;
		PreparedStatement pstmtDel = null;
		Statement st = null;
		ResultSet rs = null;
		Statement st2 = null;
		ResultSet rs2 = null;
		Statement insertStatment = null;
		List plChk = new ArrayList();
		try {
			con = Utils.connectDatabase();
			String directory = Utils.createDirector(log);

			Url = (new StringBuilder(String.valueOf(log)))
					.append("DynamicGenRulesTools\\")
					.append(System.currentTimeMillis()).append(fileName)
					.append(".txt").toString();
			String plName = "";
			String className = "";
			String classVer = "";
			String executionOrd = "";
			String clasId = "";
			String pl = "";
			BufferedWriter writeToFile = new BufferedWriter(new FileWriter(fileName, true));
			writeToFile
					.append("SE Product Line\tCode Name\tCode Version\tExecution Order(Exit when fired)\tStatus");
			writeToFile.newLine();
			queryIns = (new StringBuilder("insert into "))
					.append(sch)
					.append(".Rad_rolls (SE_PRODUCT_LINE,CLASS_NAME,CODE_VERSION,CODE_DESCRIPTION,EXECUTION_ORDER,CODE,SE_PARAMETERS_NAME_1,START_COND_1,START_VALUE_1,START_MULTIPLIER_1,START_UNIT_1,END_COND_1,END_VALUE_1,END_MULTIPLIER_1,END_UNIT_1,SE_PARAMETERS_NAME_2,START_COND_2,START_VALUE_2,START_MULTIPLIER_2,START_UNIT_2,END_COND_2,END_VALUE_2,END_MULTIPLIER_2,END_UNIT_2,SE_PARAMETERS_NAME_3,START_COND_3,START_VALUE_3,START_MULTIPLIER_3,START_UNIT_3,END_COND_3,END_VALUE_3,END_MULTIPLIER_3,END_UNIT_3,SE_PARAMETERS_NAME_4,START_COND_4,START_VALUE_4,START_MULTIPLIER_4,START_UNIT_4,END_COND_4,END_VALUE_4,END_MULTIPLIER_4,END_UNIT_4,SE_PARAMETERS_NAME_5,START_COND_5,START_VALUE_5,START_MULTIPLIER_5,START_UNIT_5,END_COND_5,END_VALUE_5,END_MULTIPLIER_5,END_UNIT_5,ReF_URL,CLAS_ID,pl_id) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)")
					.toString();
			System.out.println(queryIns);
			queryDel = (new StringBuilder("delete "))
					.append(sch)
					.append(".Rad_rolls where SE_PRODUCT_LINE=? AND CLASS_NAME=? AND CODE_VERSION=? ")
					.toString();
			pstmtIns = con.prepareStatement(queryIns);
			pstmtDel = con.prepareStatement(queryDel);
			for (int row = 1; row < txtDataList.size(); row++) {
				writeToFile = new BufferedWriter(new FileWriter(fileName, true));
				try {

					plName = ((String) ((List) txtDataList.get(row)).get(0))
							.trim().replaceAll("\"", "");
					className = ((String) ((List) txtDataList.get(row)).get(1))
							.trim();
					classVer = ((String) ((List) txtDataList.get(row)).get(2))
							.trim();
					st = con.createStatement();
					rs = st.executeQuery("select CLAS_ID from importer.CLASSIFICATION_CODE where   CLAS_NAME ='"
							+ className + "' and CLAS_VER='" + classVer + "'");
					while (rs.next()) {
						clasId = rs.getString("CLAS_ID").toString();
					}
					st2 = con.createStatement();
					rs2 = st.executeQuery("select cm.get_pl_id('" + plName
							+ "') pl from dual");
					while (rs2.next()) {
						pl = rs2.getString("pl").toString();
					}
					executionOrd = ((String) ((List) txtDataList.get(row))
							.get(5)).trim();
					if (plName.equalsIgnoreCase(""))
						status = "Can't Insert Product Line By Null";
					else if (className.equalsIgnoreCase("")
							&& classVer.equalsIgnoreCase("")) {
						status = "Can't Insert Code Name & Code Version By Null";
					} else {
						if (!plChk.contains((new StringBuilder(String
								.valueOf(plName))).append(className)
								.append(classVer).toString())) {
							pstmtDel.setString(1, plName);
							pstmtDel.setString(2, className);
							pstmtDel.setString(3, classVer);
							pstmtDel.executeUpdate();
							plChk.add((new StringBuilder(String.valueOf(plName)))
									.append(className).append(classVer)
									.toString());

						}
						System.out.println(txtDataList.get(row));
						System.out.println(plName.replace("\"", ""));
						pstmtIns.setString(1, plName.replace("\"", ""));
						pstmtIns.setString(2, className);
						pstmtIns.setString(3, classVer);
						pstmtIns.setString(4, ((String) ((List) txtDataList
								.get(row)).get(3)).trim());
						System.out.println(((String) ((List) txtDataList
								.get(row)).get(3)).trim());
						pstmtIns.setString(5, executionOrd);
						System.out.println(executionOrd);
						pstmtIns.setString(6, ((String) ((List) txtDataList
								.get(row)).get(6)).trim());
						pstmtIns.setString(7, ((String) ((List) txtDataList
								.get(row)).get(7)).trim());
						pstmtIns.setString(8, ((String) ((List) txtDataList
								.get(row)).get(8)).trim());
						pstmtIns.setString(9, ((String) ((List) txtDataList
								.get(row)).get(9)).trim());
						pstmtIns.setString(10, ((String) ((List) txtDataList
								.get(row)).get(10)).trim());
						pstmtIns.setString(11, ((String) ((List) txtDataList
								.get(row)).get(11)).trim());
						pstmtIns.setString(12, ((String) ((List) txtDataList
								.get(row)).get(12)).trim());
						pstmtIns.setString(13, ((String) ((List) txtDataList
								.get(row)).get(13)).trim());
						pstmtIns.setString(14, ((String) ((List) txtDataList
								.get(row)).get(14)).trim());
						pstmtIns.setString(15, ((String) ((List) txtDataList
								.get(row)).get(15)).trim());
						pstmtIns.setString(16, ((String) ((List) txtDataList
								.get(row)).get(16)).trim());
						pstmtIns.setString(17, ((String) ((List) txtDataList
								.get(row)).get(17)).trim());
						pstmtIns.setString(18, ((String) ((List) txtDataList
								.get(row)).get(18)).trim());
						pstmtIns.setString(19, ((String) ((List) txtDataList
								.get(row)).get(19)).trim());
						pstmtIns.setString(20, ((String) ((List) txtDataList
								.get(row)).get(20)).trim());
						pstmtIns.setString(21, ((String) ((List) txtDataList
								.get(row)).get(21)).trim());
						pstmtIns.setString(22, ((String) ((List) txtDataList
								.get(row)).get(22)).trim());
						pstmtIns.setString(23, ((String) ((List) txtDataList
								.get(row)).get(23)).trim());
						pstmtIns.setString(24, ((String) ((List) txtDataList
								.get(row)).get(24)).trim());
						pstmtIns.setString(25, ((String) ((List) txtDataList
								.get(row)).get(25)).trim());
						pstmtIns.setString(26, ((String) ((List) txtDataList
								.get(row)).get(26)).trim());
						pstmtIns.setString(27, ((String) ((List) txtDataList
								.get(row)).get(27)).trim());
						pstmtIns.setString(28, ((String) ((List) txtDataList
								.get(row)).get(28)).trim());
						pstmtIns.setString(29, ((String) ((List) txtDataList
								.get(row)).get(29)).trim());
						pstmtIns.setString(30, ((String) ((List) txtDataList
								.get(row)).get(30)).trim());
						pstmtIns.setString(31, ((String) ((List) txtDataList
								.get(row)).get(31)).trim());
						pstmtIns.setString(32, ((String) ((List) txtDataList
								.get(row)).get(32)).trim());
						pstmtIns.setString(33, ((String) ((List) txtDataList
								.get(row)).get(33)).trim());
						pstmtIns.setString(34, ((String) ((List) txtDataList
								.get(row)).get(34)).trim());
						pstmtIns.setString(35, ((String) ((List) txtDataList
								.get(row)).get(35)).trim());
						pstmtIns.setString(36, ((String) ((List) txtDataList
								.get(row)).get(36)).trim());
						pstmtIns.setString(37, ((String) ((List) txtDataList
								.get(row)).get(37)).trim());
						pstmtIns.setString(38, ((String) ((List) txtDataList
								.get(row)).get(38)).trim());
						pstmtIns.setString(39, ((String) ((List) txtDataList
								.get(row)).get(39)).trim());
						pstmtIns.setString(40, ((String) ((List) txtDataList
								.get(row)).get(40)).trim());
						pstmtIns.setString(41, ((String) ((List) txtDataList
								.get(row)).get(41)).trim());
						pstmtIns.setString(42, ((String) ((List) txtDataList
								.get(row)).get(42)).trim());
						pstmtIns.setString(43, ((String) ((List) txtDataList
								.get(row)).get(43)).trim());
						pstmtIns.setString(44, ((String) ((List) txtDataList
								.get(row)).get(44)).trim());
						pstmtIns.setString(45, ((String) ((List) txtDataList
								.get(row)).get(45)).trim());
						pstmtIns.setString(46, ((String) ((List) txtDataList
								.get(row)).get(46)).trim());
						pstmtIns.setString(47, ((String) ((List) txtDataList
								.get(row)).get(47)).trim());
						pstmtIns.setString(48, ((String) ((List) txtDataList
								.get(row)).get(48)).trim());
						pstmtIns.setString(49, ((String) ((List) txtDataList
								.get(row)).get(49)).trim());
						pstmtIns.setString(50, ((String) ((List) txtDataList
								.get(row)).get(50)).trim());
						pstmtIns.setString(51, ((String) ((List) txtDataList
								.get(row)).get(51)).trim());
						pstmtIns.setString(52, ((String) ((List) txtDataList
								.get(row)).get(4)).trim());
						pstmtIns.setString(53, (clasId));
						pstmtIns.setString(54, (pl));
						pstmtIns.executeUpdate();
						status = "Inserted";
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				// insertStatment
				// .executeUpdate("insert into importer.generated_pl(pl_id,status) values ("
				// + pl + ",0)");
				writeToFile.append((new StringBuilder(String.valueOf(plName)))
						.append("\t").append(className).append("\t")
						.append(classVer).append("\t").append(executionOrd)
						.append("\t").append(status).toString());
				writeToFile.newLine();
				writeToFile.close();
			}
			// writeToFile.close();
			con.commit();
			pstmtIns.close();
			pstmtDel.close();
			con.close();
		}

		catch (Exception e) {
			e.printStackTrace();
			try {
				con.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		return Url;
	}

	//
	public String exportRadRuleAction(String fileName,
			ArrayList<ArrayList<String>> txtDataList) {
		String status = "";
		String Url = "";
		String queryIns = "";
		String queryDel = "";
		Connection con = null;
		PreparedStatement pstmtIns = null;
		PreparedStatement pstmtDel = null;
		Statement st = null;
		ResultSet rs = null;
		Statement st2 = null;
		ResultSet rs2 = null;
		List plChk = new ArrayList();
		try {
			con = Utils.connectDatabase();
			String directory = Utils.createDirector(log);

			Url = (new StringBuilder(String.valueOf(log)))
					.append("DynamicGenRulesTools\\")
					.append(System.currentTimeMillis()).append(fileName)
					.append(".txt").toString();
			String plName = "";
			String className = "";
			String classVer = "";
			String executionOrd = "";
			String clasId = "";
			String pl = "";
			BufferedWriter writeToFile = new BufferedWriter(new FileWriter(fileName, true));
			writeToFile
					.append("SE_PRODUCT_LINE\t CLASS_NAME\t CODE_VERSION\t CODE_DESCRIPTION\t REF_URL\t EXECUTION_ORDER\t CODE\t SE_PARAMETERS_NAME_1\t START_COND_1\t START_VALUE_1\t START_MULTIPLIER_1\t START_UNIT_1\t END_COND_1\t END_VALUE_1\t END_MULTIPLIER_1\t END_UNIT_1\t SE_PARAMETERS_NAME_2\t START_COND_2\t START_VALUE_2\t START_MULTIPLIER_2\t START_UNIT_2\t END_COND_2\t END_VALUE_2\t END_MULTIPLIER_2\t END_UNIT_2\t SE_PARAMETERS_NAME_3\t START_COND_3\t START_VALUE_3\t START_MULTIPLIER_3\t START_UNIT_3\t END_COND_3\t END_VALUE_3\t END_MULTIPLIER_3\t END_UNIT_3\t SE_PARAMETERS_NAME_4\t START_COND_4\t START_VALUE_4\t START_MULTIPLIER_4\t START_UNIT_4\t END_COND_4\t END_VALUE_4\t END_MULTIPLIER_4\t END_UNIT_4\t SE_PARAMETERS_NAME_5\t START_COND_5\t START_VALUE_5\t START_MULTIPLIER_5\t START_UNIT_5\t END_COND_5\t END_VALUE_5\t END_MULTIPLIER_5\t END_UNIT_5\t FEATURE_ID_1\t FEATURE_ID_2\t FEATURE_ID_3\t FEATURE_ID_4\t FEATURE_ID_5\t PL_ID\t CLAS_ID");
			writeToFile.newLine();

			// pstmtIns = con.prepareStatement(queryIns);
			// pstmtDel = con.prepareStatement(queryDel);
			for (int row = 1; row < txtDataList.size(); row++) {
				writeToFile = new BufferedWriter(new FileWriter(fileName, true));
				className = ((String) ((List) txtDataList.get(row)).get(0))
						.trim();
				classVer = ((String) ((List) txtDataList.get(row)).get(1))
						.trim();
				st = con.createStatement();
				rs = st.executeQuery("select * from importer.Rad_rolls where CLASS_NAME ='"
						+ className + "' and CODE_VERSION='" + classVer + "'");
				String line = "";
				int count = 1;
				while (rs.next()) {
					line = "";
					for (int c = 1; c < 60; c++) {
						line += rs.getString(c) + "\t";
					}
					line += "\n";
					writeToFile.append(line);
				}
				rs.close();
				st.close();
				writeToFile.close();
			}

			con.close();
		}

		catch (Exception e) {
			e.printStackTrace();
			try {
				con.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		return Url;
	}

	//
	public String deleteRadRuleAction(String fileName,
			ArrayList<ArrayList<String>> txtDataList) {
		String Url = "";

		String query = "";

		String pl = "";

		Connection con = null;

		ResultSet rSet = null;

		PreparedStatement pstmt = null;
		Statement insertStatment = null;

		try

		{

			String className = "";

			String codeVer = "";

			String directory = Utils.createDirector(log);

			Url = (new StringBuilder(String.valueOf(log)))
					.append("DynamicGenRulesTools\\")
					.append(System.currentTimeMillis()).append(fileName)
					.append(".txt").toString();

			BufferedWriter writeToFile = new BufferedWriter(new FileWriter(fileName, true));

			con = Utils.connectDatabase();

			writeToFile.append("Class Name\tCode Version\tStatus");

			writeToFile.newLine();

			query = "delete from importer.Rad_ROLLS where CLASS_NAME=? and CODE_VERSION=?";

			pstmt = con.prepareStatement(query);

			for (int row = 1; row < txtDataList.size(); row++)

			{
				writeToFile = new BufferedWriter(new FileWriter(fileName, true));
				try

				{

					className = ((String) ((List) txtDataList.get(row)).get(0))
							.trim();

					codeVer = ((String) ((List) txtDataList.get(row)).get(1))
							.trim();

					pstmt.setString(1, className);

					pstmt.setString(2, codeVer);
					pstmt.executeUpdate();
					// insertStatment
					// .executeUpdate("insert into importer.generated_pl(pl_id,status) values ("
					// + pl + ",0)");
					writeToFile
							.append(className + "\t" + codeVer + "\tDeleted");
					writeToFile.newLine();

				}

				catch (Exception e)

				{

					rSet.close();

					e.printStackTrace();

				}
				writeToFile.close();
			}

			pstmt.close();

			con.close();

		}

		catch (Exception e)

		{

			e.printStackTrace();

			try

			{

				con.close();

			}

			catch (SQLException e1)

			{

				e1.printStackTrace();

			}

		}

		return Url;

	}

	public String deleteRulePLAction(String fileName,
			ArrayList<ArrayList<String>> txtDataList)

	{

		String Url = "";

		String query = "";

		String pl = "";

		Connection con = null;

		ResultSet rSet = null;

		Statement deleteStatment = null;
		Statement insertStatment = null;
		Statement selectStatment = null;
		Long codeId = null;
		try

		{

			String className = "";

			String codeVer = "";

			String directory = Utils.createDirector(log);

			Url = (new StringBuilder(String.valueOf(log)))
					.append("DynamicGenRulesTools\\")
					.append(System.currentTimeMillis()).append(fileName)
					.append(".txt").toString();

			BufferedWriter writeToFile = new BufferedWriter(new FileWriter(fileName, true));

			con = Utils.connectDatabase();

			writeToFile.append("Product Line\tStatus");

			writeToFile.newLine();

			deleteStatment = con.createStatement();
			insertStatment = con.createStatement();
			selectStatment = con.createStatement();
			for (int row = 1; row < txtDataList.size(); row++) {
				writeToFile = new BufferedWriter(new FileWriter(fileName, true));
				codeId = null;
				try

				{

					pl = ((String) ((List) txtDataList.get(row)).get(0)).trim()
							.replaceAll("\"", "");

					className = ((String) ((List) txtDataList.get(row)).get(1))
							.trim();

					codeVer = ((String) ((List) txtDataList.get(row)).get(2))
							.trim();
					query = "delete from importer.CLAS_ROLLS where SE_PRODUCT_LINE='"
							+ pl
							+ "' and  CLASS_NAME='"
							+ className
							+ "' and CODE_VERSION='" + codeVer + "'";
					// pstmt.setString(1, pl);
					//
					// pstmt.setString(2, className);
					//
					// pstmt.setString(3, codeVer);
					deleteStatment.executeUpdate(query);

					rSet = selectStatment
							.executeQuery("select clas_id from classification_code where clas_name='"
									+ className
									+ "' and clas_ver='"
									+ codeVer
									+ "'");
					while (rSet.next()) {
						codeId = rSet.getLong("clas_id");
					}
					insertStatment
							.executeUpdate("insert into importer.generated_pl(pl_id,status,CLASS_ID) values (cm.get_pl_id('"
									+ pl + "'),0," + codeId + ")");
					writeToFile.append(pl + "\tDeleted");
					writeToFile.newLine();

					// for (rSet = pstmt.executeQuery(); rSet.next();
					// writeToFile.newLine())
					//
					// writeToFile.write((new StringBuilder(String
					// .valueOf(rSet.getString(1) == null ? ""
					// : ((Object) (rSet.getString(1))))))
					// .append("\t")
					// .append(rSet.getString(2) == null ? "" : rSet
					// .getString(2))
					// .append("\t")
					// .append(rSet.getString(3) == null ? "" : rSet
					// .getString(3))
					// .append("\t")
					// .append(rSet.getString(4) == null ? "" : rSet
					// .getString(4))
					// .append("\t")
					// .append(rSet.getString(5) == null ? "" : rSet
					// .getString(5))
					// .append("\t")
					// .append(rSet.getString(6) == null ? "" : rSet
					// .getString(6))
					// .append("\t")
					// .append(rSet.getString(7) == null ? "" : rSet
					// .getString(7))
					// .append("\t")
					// .append(rSet.getString(8) == null ? "" : rSet
					// .getString(8))
					// .append("\t")
					// .append(rSet.getString(9) == null ? "" : rSet
					// .getString(9))
					// .append("\t")
					// .append(rSet.getString(10) == null ? "" : rSet
					// .getString(10))
					// .append("\t")
					// .append(rSet.getString(11) == null ? "" : rSet
					// .getString(11))
					// .append("\t")
					// .append(rSet.getString(12) == null ? "" : rSet
					// .getString(12))
					// .append("\t")
					// .append(rSet.getString(13) == null ? "" : rSet
					// .getString(13))
					// .append("\t")
					// .append(rSet.getString(14) == null ? "" : rSet
					// .getString(14))
					// .append("\t")
					// .append(rSet.getString(15) == null ? "" : rSet
					// .getString(15))
					// .append("\t")
					// .append(rSet.getString(16) == null ? "" : rSet
					// .getString(16))
					// .append("\t")
					// .append(rSet.getString(17) == null ? "" : rSet
					// .getString(17))
					// .append("\t")
					// .append(rSet.getString(18) == null ? "" : rSet
					// .getString(18))
					// .append("\t")
					// .append(rSet.getString(19) == null ? "" : rSet
					// .getString(19))
					// .append("\t")
					// .append(rSet.getString(20) == null ? "" : rSet
					// .getString(20))
					// .append("\t")
					// .append(rSet.getString(21) == null ? "" : rSet
					// .getString(21))
					// .append("\t")
					// .append(rSet.getString(22) == null ? "" : rSet
					// .getString(22))
					// .append("\t")
					// .append(rSet.getString(23) == null ? "" : rSet
					// .getString(23))
					// .append("\t")
					// .append(rSet.getString(24) == null ? "" : rSet
					// .getString(24))
					// .append("\t")
					// .append(rSet.getString(25) == null ? "" : rSet
					// .getString(25))
					// .append("\t")
					// .append(rSet.getString(26) == null ? "" : rSet
					// .getString(26))
					// .append("\t")
					// .append(rSet.getString(27) == null ? "" : rSet
					// .getString(27))
					// .append("\t")
					// .append(rSet.getString(28) == null ? "" : rSet
					// .getString(28))
					// .append("\t")
					// .append(rSet.getString(29) == null ? "" : rSet
					// .getString(29))
					// .append("\t")
					// .append(rSet.getString(30) == null ? "" : rSet
					// .getString(30))
					// .append("\t")
					// .append(rSet.getString(31) == null ? "" : rSet
					// .getString(31))
					// .append("\t")
					// .append(rSet.getString(32) == null ? "" : rSet
					// .getString(32))
					// .append("\t")
					// .append(rSet.getString(33) == null ? "" : rSet
					// .getString(33))
					// .append("\t")
					// .append(rSet.getString(34) == null ? "" : rSet
					// .getString(34))
					// .append("\t")
					// .append(rSet.getString(35) == null ? "" : rSet
					// .getString(35))
					// .append("\t")
					// .append(rSet.getString(36) == null ? "" : rSet
					// .getString(36))
					// .append("\t")
					// .append(rSet.getString(37) == null ? "" : rSet
					// .getString(37))
					// .append("\t")
					// .append(rSet.getString(38) == null ? "" : rSet
					// .getString(38))
					// .append("\t")
					// .append(rSet.getString(39) == null ? "" : rSet
					// .getString(39))
					// .append("\t")
					// .append(rSet.getString(40) == null ? "" : rSet
					// .getString(40))
					// .append("\t")
					// .append(rSet.getString(41) == null ? "" : rSet
					// .getString(41))
					// .append("\t")
					// .append(rSet.getString(42) == null ? "" : rSet
					// .getString(42))
					// .append("\t")
					// .append(rSet.getString(43) == null ? "" : rSet
					// .getString(43))
					// .append("\t")
					// .append(rSet.getString(44) == null ? "" : rSet
					// .getString(44))
					// .append("\t")
					// .append(rSet.getString(45) == null ? "" : rSet
					// .getString(45))
					// .append("\t")
					// .append(rSet.getString(46) == null ? "" : rSet
					// .getString(46))
					// .append("\t")
					// .append(rSet.getString(47) == null ? "" : rSet
					// .getString(47))
					// .append("\t")
					// .append(rSet.getString(48) == null ? "" : rSet
					// .getString(48))
					// .append("\t")
					// .append(rSet.getString(49) == null ? "" : rSet
					// .getString(49))
					// .append("\t")
					// .append(rSet.getString(50) == null ? "" : rSet
					// .getString(50))
					// .append("\t")
					// .append(rSet.getString(51) == null ? "" : rSet
					// .getString(51))
					// .append("\t")
					// .append(rSet.getString(52) == null ? "" : rSet
					// .getString(52)).toString());
					//
					// rSet.close();

				}

				catch (Exception e)

				{

					e.printStackTrace();

				}
				writeToFile.close();
			}

			deleteStatment.close();
			insertStatment.close();
			con.close();

		}

		catch (Exception e)

		{

			e.printStackTrace();

			try

			{

				con.close();

			}

			catch (SQLException e1)

			{

				e1.printStackTrace();

			}

		}

		return Url;

	}

	static String sch = Utils.getProperty("TablesSchame");

	static String log = "D:\\FilesTemp\\ClassificationTools\\";

}
