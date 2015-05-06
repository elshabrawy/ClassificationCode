package com.se.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.se.classcode.Utils;

public class ErrorToolActions {
	public String importAllErrorCode() {
		Connection con = null;
		Statement updateST = null;
		try {
			con = Utils.connectDatabase();
			updateST = con.createStatement();
			updateST.executeUpdate("merge INTO(SELECT * FROM cm.part_code WHERE com_id IN (SELECT com_id FROM importer.part_code_error)) x USING importer.part_code_error y ON (x.com_id=y.com_id AND x.clas_id=y.clas_id)WHEN matched THEN UPDATE SET x.DYN_CLASS =y.DYN_CLASS,x.STATIC_CLASS =y.STATIC_CLASS,x.SUPPLIER_CLASS=y.SUPPLIER_CLASS, x.SE_CLASS=y.SE_CLASS,x.REF =y.REF,x.REF_URL=y.REF_URL,x.CONF_CLASS_LVL=y.CONF_CLASS_LVL,x.VER_CLAS=y.VER_CLAS, x.MANUAL_FLAG =y.MANUAL_FLAG, x.SUP_REF_URL=y.SUP_REF_URL, x.STATIC_REF_URL=y.STATIC_REF_URL,x.MODIFY_DATE =y.MODIFY_DATE,x.DISP_FLAG=y.DISP_FLAG,x.ROUND =y.ROUND");
			updateST.executeUpdate("delete  from importer.part_code_error ");

		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		} finally {
			try {
				updateST.close();
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return "Done";
	}

	public String deleteAllErrorCode() {
		Connection con = null;
		Statement updateST = null;
		try {
			con = Utils.connectDatabase();
			updateST = con.createStatement();
			updateST.executeUpdate("delete  from importer.part_code_error ");

		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		} finally {
			try {
				updateST.close();
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return "Done";
	}

	public String exportAllErrorCode(String fileName) {
		String Url = "";
		String query = "";
		ResultSet rs = null;
		Connection con = null;

		try {
			String pn = "", man = "";
			@SuppressWarnings("unused")
			// String directory = Utils.createDirector(log +
			// "\\ExceptionPartsTools");
			// Url = log + "ExceptionPartsTools\\" + System.currentTimeMillis()+
			// "AllExceptionParts.txt";
			BufferedWriter writeToFile = new BufferedWriter(new FileWriter(fileName,true));
			con = Utils.connectDatabase();
			writeToFile
					.append("Product Line\tPart Number\tSupplier\tCode Name\tCode Version\tDYN_CLASS\tSTATIC_CLASS\tSUPPLIER_CLASS\tSE_CLASS\tREF\tREF_URL\tCONF_CLASS_LVL\tMANUAL_FLAG\tSUP_REF_URL\tSTATIC_REF_URL\tModify Date");
			writeToFile.newLine();

			query = "select get_pl_name(p.PL_ID), x.COM_PARTNUM, get_man_name(x.MAN_ID), c.CLAS_NAME,c.CLAS_VER, p.DYN_CLASS, p.STATIC_CLASS, p.SUPPLIER_CLASS, p.SE_CLASS, p.REF,p.REF_URL, p.CONF_CLASS_LVL, p.MANUAL_FLAG, p.SUP_REF_URL, p.STATIC_REF_URL, p.MODIFY_DATE from importer.PART_CODE_error p,cm.XLP_SE_COMPONENT x ,importer.CLASSIFICATION_CODE c where p.com_id=x.com_id and c.CLAS_ID=p.CLAS_ID"
					+ " and MANUAL_FLAG =1";
			System.out.println(query);
			java.sql.Statement st = con.createStatement();
			rs = st.executeQuery(query);
			int count = 0;
			while (rs.next()) {
				writeToFile.append(((rs.getString(1) != null) ? rs.getString(1)
						: "")
						+ "\t"
						+ ((rs.getString(2) != null) ? rs.getString(2) : "")
						+ "\t"
						+ ((rs.getString(3) != null) ? rs.getString(3) : "")
						+ "\t"
						+ ((rs.getString(4) != null) ? rs.getString(4) : "")
						+ "\t"
						+ ((rs.getString(5) != null) ? rs.getString(5) : "")
						+ "\t"
						+ ((rs.getString(6) != null) ? rs.getString(6) : "")
						+ "\t"
						+ ((rs.getString(7) != null) ? rs.getString(7) : "")
						+ "\t"
						+ ((rs.getString(8) != null) ? rs.getString(8) : "")
						+ "\t"
						+ ((rs.getString(9) != null) ? rs.getString(9) : "")
						+ "\t"
						+ ((rs.getString(10) != null) ? rs.getString(10) : "")
						+ "\t"
						+ ((rs.getString(11) != null) ? rs.getString(11) : "")
						+ "\t"
						+ ((rs.getString(12) != null) ? rs.getString(12) : "")
						+ "\t"
						+ ((rs.getString(13) != null) ? rs.getString(13) : "")
						+ "\t"
						+ ((rs.getString(14) != null) ? rs.getString(14) : "")
						+ "\t"
						+ ((rs.getString(15) != null) ? rs.getString(15) : "")
						+ "\t"
						+ ((rs.getString(16) != null) ? rs.getString(16) : ""));
				writeToFile.newLine();
				count++;
				System.out.println("here " + count);
			}

			rs.close();

			writeToFile.close();
			st.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				con.close();
			} catch (SQLException e1) {

				e1.printStackTrace();
			}
		}
		return Url;

	}

	public String importByPNSAction(String fileName,
			ArrayList<ArrayList<String>> txtDataList, int clasID) {
		Connection con = null;
		CallableStatement pstmt = null;
		List logData = new ArrayList();
		String part = "";
		String man = "";
		String action = "";
		String Url = "";
		Statement st = null;
		Statement updateST = null;
		try {
			Url = "DynamicGenDataTools\\" + System.currentTimeMillis()
					+ fileName + ".txt";
			File file = new File(Url);
			con = Utils.connectDatabase();
			updateST = con.createStatement();
			logData.add("Part Number\tSupplier Name\tAction\tStatus");

			for (int row = 0; row < txtDataList.size(); row++) {
				part = ((String) ((List) txtDataList.get(row)).get(0)).trim()
						.replace("\"", "");
				man = ((String) ((List) txtDataList.get(row)).get(1)).trim()
						.replaceAll("\"", "");
				action = ((String) ((List) txtDataList.get(row)).get(2)).trim()
						.replaceAll("\"", "");
				st = con.createStatement();
				String sq = "select cm.GET_COM_ID('" + part
						+ "',CM.GET_MAN_ID('" + man + "')) com from dual";
				System.out.println(sq);
				ResultSet rs = st.executeQuery(sq);
				while (rs.next()) {
					String comString = rs.getString("com");
					Long com = Long.valueOf(Long.parseLong(comString));
					System.out.println("com_id is " + com);
					try {
						if (com.longValue() != -1L) {
							if (action.equalsIgnoreCase("Update")) {
								updateST.executeUpdate("merge INTO (SELECT * FROM cm.part_code WHERE com_id = "
										+ com
										+ " AND clas_id="
										+ clasID
										+ ") x USING (select * from importer.part_code_error  WHERE com_id = "
										+ com
										+ " AND clas_id="
										+ clasID
										+ ") y ON (x.com_id=y.com_id AND x.clas_id=y.clas_id)WHEN matched THEN  UPDATE set x.DYN_CLASS=y.DYN_CLASS,x.STATIC_CLASS=y.STATIC_CLASS,x.SUPPLIER_CLASS=y.SUPPLIER_CLASS,x.SE_CLASS=y.SE_CLASS,x.REF=y.REF,x.REF_URL=y.REF_URL,x.CONF_CLASS_LVL=y.CONF_CLASS_LVL,x.VER_CLAS=y.VER_CLAS,x.MANUAL_FLAG=y.MANUAL_FLAG,x.SUP_REF_URL=y.SUP_REF_URL,x.STATIC_REF_URL=y.STATIC_REF_URL,x.MODIFY_DATE=y.MODIFY_DATE,x.DISP_FLAG=y.DISP_FLAG,x.ROUND=y.ROUND");
								logData.add(part + "\t" + man + "\t" + "\t"
										+ "Updated");
								System.out.println(part + "\t" + man + "\t"
										+ "Updated");
							} else {

								logData.add(part + "\t" + man + "\t" + "\t"
										+ "Deleted");
								System.out.println(part + "\t" + man + "\t"
										+ "Deleted");
							}

							updateST.executeUpdate("delete  from importer.part_code_error  WHERE com_id = "
									+ com + " AND clas_id=" + clasID);
						} else {
							logData.add(part + "\t" + man + "\t"
									+ "Invalid Part or Man");
						}
					} catch (Exception ex) {
						logData.add(part + "\t" + man + "\t" + ex.getMessage());
					}
				}

				rs.close();
				st.close();
			}
			updateST.close();
			con.close();
			FileUtils.writeLines(file, logData);
		} catch (Exception e) {
			e.printStackTrace();
			logData.add(e.getMessage());
		}

		return Url;
	}

	public String importByPLAction(String fileName,
			ArrayList<ArrayList<String>> txtDataList, int clasID) {
		String Url = "";
		String pl = "";
		String action = "";
		Connection con = null;
		CallableStatement pstmt = null;
		List logData = new ArrayList();
		Statement st = null;
		String sql = "";
		Statement newST = null;
		ResultSet newRS = null;
		Statement updateST = null;
		try {
			String directory = Utils.createDirector("\\DynamicGenDataTools");
			Url = "DynamicGenDataTools\\" + System.currentTimeMillis()
					+ fileName + ".txt";
			File file = new File(Url);
			con = Utils.connectDatabase();
			logData.add("SE Product Line\tAction\tStatus");
			updateST = con.createStatement();
			for (int row = 0; row < txtDataList.size(); row++) {

				st = con.createStatement();
				pl = ((String) ((List) txtDataList.get(row)).get(0)).trim()
						.replace("\"", "");
				action = ((String) ((List) txtDataList.get(row)).get(1)).trim()
						.replace("\"", "");
				sql = "select cm.get_PL_ID('" + pl + "')p from dual";
				ResultSet rs = st.executeQuery(sql);
				while (rs.next()) {
					int comm = Integer.parseInt(rs.getString("p"));
					if (comm != -1)
						try {
							if (action.equalsIgnoreCase("Update")) {

								updateST.executeUpdate("merge INTO (SELECT * FROM cm.part_code WHERE pl_id = cm.get_pl_id('"
										+ pl
										+ "') AND clas_id="
										+ clasID
										+ ") x USING (select * from importer.part_code_error  WHERE pl_id = cm.get_pl_id('"
										+ pl
										+ "') AND clas_id="
										+ clasID
										+ ") y ON (x.com_id=y.com_id AND x.clas_id=y.clas_id)WHEN matched THEN  UPDATE set x.DYN_CLASS=y.DYN_CLASS,x.STATIC_CLASS=y.STATIC_CLASS,x.SUPPLIER_CLASS=y.SUPPLIER_CLASS,x.SE_CLASS=y.SE_CLASS,x.REF=y.REF,x.REF_URL=y.REF_URL,x.CONF_CLASS_LVL=y.CONF_CLASS_LVL,x.VER_CLAS=y.VER_CLAS,x.MANUAL_FLAG=y.MANUAL_FLAG,x.SUP_REF_URL=y.SUP_REF_URL,x.STATIC_REF_URL=y.STATIC_REF_URL,x.MODIFY_DATE=y.MODIFY_DATE,x.DISP_FLAG=y.DISP_FLAG,x.ROUND=y.ROUND");
								logData.add(pl + "\t" + "Updated");
								System.out.println(pl + "\t" + action + "\t"
										+ "Updated");
							} else {

								logData.add(pl + "\t" + action + "\t"
										+ "Deleted");
								System.out.println(pl + "\t" + "Deleted");
							}

							updateST.executeUpdate("delete  from importer.part_code_error  WHERE pl_id = cm.get_pl_id('"
									+ pl + "') AND clas_id=" + clasID);

							// plGeneration(pl, clasID, null);

							// logData.add(pl + "\t" + "Updated");
						} catch (Exception ex) {
							logData.add(pl + "\t" + "Not Updated");
						}
					else {
						logData.add(pl + "\t" + action + "\t" + "Invalid PL");
					}
				}
				rs.close();
				st.close();
			}
			updateST.close();
			con.close();
			FileUtils.writeLines(file, logData);
		} catch (Exception e) {
			e.printStackTrace();
			Url = "The Error Occurred:\n\t" + e.getMessage();
		}
		return Url;
	}

	public String importBySuppAction(String fileName,
			ArrayList<ArrayList<String>> txtDataList, int clasID) {
		String Url = "";
		String man = "";
		String action = "";
		Statement updateST = null;
		Connection con = null;
		CallableStatement pstmt = null;
		List logData = new ArrayList();
		Statement st = null;

		String sql = "";
		int t1 = 0;
		int t2 = 0;
		try {
			String directory = Utils.createDirector("\\DynamicGenDataTools");
			Url = "DynamicGenDataTools\\" + System.currentTimeMillis()
					+ fileName + ".txt";
			File file = new File(Url);
			con = Utils.connectDatabase();
			logData.add("Supplier Name\tAction\tStatus");

			for (int row = 0; row < txtDataList.size(); row++) {
				try {
					man = ((String) ((List) txtDataList.get(row)).get(0))
							.trim().replaceAll("\"", "");
					action = ((String) ((List) txtDataList.get(row)).get(1))
							.trim().replaceAll("\"", "");

					sql = "select CM.GET_MAN_ID('" + man + "') m from dual";
					st = con.createStatement();
					ResultSet rs = st.executeQuery(sql);
					while (rs.next()) {
						t1 = Integer.parseInt(rs.getString("m"));
					}
					if (t1 != -1)
						try {
							if (action.equalsIgnoreCase("Update")) {

								updateST.executeUpdate("merge INTO (SELECT * FROM cm.part_codeWHERE com_id in(select com_id from cm.xlp_se_component where man_id=cm.get_man_id('"
										+ man
										+ "')) AND clas_id="
										+ clasID
										+ ") x USING (select * from importer.part_code_error  WHERE com_id in(select com_id from cm.xlp_se_component where man_id=cm.get_man_id('"
										+ man
										+ "')) AND clas_id="
										+ clasID
										+ ") y ON (x.com_id=y.com_id AND x.clas_id=y.clas_id)WHEN matched THEN  UPDATE set x.DYN_CLASS=y.DYN_CLASS,x.STATIC_CLASS=y.STATIC_CLASS,x.SUPPLIER_CLASS=y.SUPPLIER_CLASS,x.SE_CLASS=y.SE_CLASS,x.REF=y.REF,x.REF_URL=y.REF_URL,x.CONF_CLASS_LVL=y.CONF_CLASS_LVL,x.VER_CLAS=y.VER_CLAS,x.MANUAL_FLAG=y.MANUAL_FLAG,x.SUP_REF_URL=y.SUP_REF_URL,x.STATIC_REF_URL=y.STATIC_REF_URL,x.MODIFY_DATE=y.MODIFY_DATE,x.DISP_FLAG=y.DISP_FLAG,x.ROUND=y.ROUND");

								logData.add(man + "\t" + action + "\t"
										+ "Updated");
								System.out.println(man + "\t" + action + "\t"
										+ "Updated");
							} else {

								logData.add(man + "\t" + action + "\t"
										+ "Deleted");
								System.out.println(man + "\t" + "Deleted");
							}

							updateST.executeUpdate("delete  from importer.part_code_error  WHERE com_id in (select com_id from cm.xlp_se_component where man_id=cm.get_man_id('"
									+ man + "')) AND clas_id=" + clasID);

						} catch (Exception e) {
							e.printStackTrace();
							logData.add(man + "\t" + action + "\t"
									+ e.getMessage());
						}

				} catch (Exception e) {
					e.printStackTrace();
					logData.add(man + "\t" + action + "\t" + e.getMessage());
				}
			}

			con.close();
			FileUtils.writeLines(file, logData);
		} catch (Exception e) {
			e.printStackTrace();
			Url = "The Error Occurred:\n\t" + e.getMessage();
			try {
				con.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		return Url;
	}

	public String exportPNSAction(String fileName,
			ArrayList<ArrayList<String>> txtDataList, int id) {
		String Url = "";
		String query = "";
		ResultSet rs = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			String pn = "";
			String man = "";

			String directory = Utils.createDirector("\\DynamicGenDataTools");
			Url = "DynamicGenDataTools\\" + System.currentTimeMillis()
					+ fileName + ".txt";
			BufferedWriter writeToFile = new BufferedWriter(new FileWriter(Url));
			con = Utils.connectDatabase();
			writeToFile
					.write("Product Line\tPart Number\tSupplier\tCode Name\tCode Version\tDYN_CLASS\tSTATIC_CLASS\tSUPPLIER_CLASS\tSE_CLASS\tREF\tREF_URL\tCONF_CLASS_LVL\tmanual_flag\tSUP_REF_URL\tSTATIC_REF_URL\tModify Date");
			writeToFile.newLine();
			query = "select get_pl_name(p.PL_ID), x.COM_PARTNUM, get_man_name(x.MAN_ID), c.CLAS_NAME, c.CLAS_VER, p.DYN_CLASS, p.STATIC_CLASS, p.SUPPLIER_CLASS, p.SE_CLASS, p.REF, p.REF_URL, p.CONF_CLASS_LVL, p.manual_flag, p.SUP_REF_URL, p.STATIC_REF_URL, p.MODIFY_DATE from importer.PART_CODE_error p,cm.XLP_SE_COMPONENT x,importer.CLASSIFICATION_CODE c where p.COM_ID=cm.GET_COM_ID(?,CM.GET_MAN_ID(?)) and p.CLAS_ID=? and p.COM_ID=x.COM_ID and p.CLAS_ID=c.CLAS_ID and x.NAN_PARTNUM not like ')$.@(%'";
			pstmt = con.prepareStatement(query);
			for (int row = 0; row < txtDataList.size(); row++) {
				try {
					pn = ((String) ((List) txtDataList.get(row)).get(0)).trim()
							.replaceAll("\"", "");
					man = ((String) ((List) txtDataList.get(row)).get(1))
							.trim().replaceAll("\"", "");
					pstmt.setString(1, pn);
					pstmt.setString(2, man);
					pstmt.setInt(3, id);
					rs = pstmt.executeQuery();
					while (rs.next()) {
						writeToFile.write((rs.getString(1) != null ? rs
								.getString(1) : "")
								+ "\t"
								+ (rs.getString(2) != null ? rs.getString(2)
										: "")
								+ "\t"
								+ (rs.getString(3) != null ? rs.getString(3)
										: "")
								+ "\t"
								+ (rs.getString(4) != null ? rs.getString(4)
										: "")
								+ "\t"
								+ (rs.getString(5) != null ? rs.getString(5)
										: "")
								+ "\t"
								+ (rs.getString(6) != null ? rs.getString(6)
										: "")
								+ "\t"
								+ (rs.getString(7) != null ? rs.getString(7)
										: "")
								+ "\t"
								+ (rs.getString(8) != null ? rs.getString(8)
										: "")
								+ "\t"
								+ (rs.getString(9) != null ? rs.getString(9)
										: "")
								+ "\t"
								+ (rs.getString(10) != null ? rs.getString(10)
										: "")
								+ "\t"
								+ (rs.getString(11) != null ? rs.getString(11)
										: "")
								+ "\t"
								+ (rs.getString(12) != null ? rs.getString(12)
										: "")
								+ "\t"
								+ (rs.getString(13) != null ? rs.getString(13)
										: "")
								+ "\t"
								+ (rs.getString(14) != null ? rs.getString(14)
										: "")
								+ "\t"
								+ (rs.getString(15) != null ? rs.getString(15)
										: "")
								+ "\t"
								+ (rs.getString(16) != null ? rs.getString(16)
										: ""));
						writeToFile.newLine();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				rs.close();
			}
			writeToFile.close();
			pstmt.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				con.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		return Url;
	}

	public String exportSuppAction(String fileName,
			ArrayList<ArrayList<String>> txtDataList, int id) {
		String Url = "";
		String query = "";
		ResultSet rs = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			String supp = "";

			String directory = Utils.createDirector("\\DynamicGenDataTools");
			Url = "DynamicGenDataTools\\" + System.currentTimeMillis()
					+ fileName + ".txt";
			BufferedWriter writeToFile = new BufferedWriter(new FileWriter(Url));
			con = Utils.connectDatabase();
			writeToFile
					.write("Product Line\tPart Number\tSupplier\tCode Name\tCode Version\tDYN_CLASS\tSTATIC_CLASS\tSUPPLIER_CLASS\tSE_CLASS\tREF\tREF_URL\tCONF_CLASS_LVL\tmanual_flag\tSUP_REF_URL\tSTATIC_REF_URL\tModify Date");
			writeToFile.newLine();
			query = "select get_pl_name(p.PL_ID), x.COM_PARTNUM, get_man_name(x.MAN_ID), c.CLAS_NAME, c.CLAS_VER, p.DYN_CLASS, p.STATIC_CLASS, p.SUPPLIER_CLASS, p.SE_CLASS, p.REF, p.REF_URL, p.CONF_CLASS_LVL, p.manual_flag, p.SUP_REF_URL, p.STATIC_REF_URL, p.MODIFY_DATE from importer.PART_CODE_error p,cm.XLP_SE_COMPONENT x,importer.CLASSIFICATION_CODE c where x.man_id = get_man_id(?) and x.COM_ID=p.COM_ID and p.CLAS_ID=? and p.CLAS_ID=c.CLAS_ID and x.NAN_PARTNUM not like ')$.@(%'";
			pstmt = con.prepareStatement(query);
			for (int row = 0; row < txtDataList.size(); row++) {
				try {
					supp = ((String) ((List) txtDataList.get(row)).get(0))
							.trim().replaceAll("\"", "");
					pstmt.setString(1, supp);
					pstmt.setInt(2, id);
					rs = pstmt.executeQuery();
					while (rs.next()) {
						writeToFile.write((rs.getString(1) != null ? rs
								.getString(1) : "")
								+ "\t"
								+ (rs.getString(2) != null ? rs.getString(2)
										: "")
								+ "\t"
								+ (rs.getString(3) != null ? rs.getString(3)
										: "")
								+ "\t"
								+ (rs.getString(4) != null ? rs.getString(4)
										: "")
								+ "\t"
								+ (rs.getString(5) != null ? rs.getString(5)
										: "")
								+ "\t"
								+ (rs.getString(6) != null ? rs.getString(6)
										: "")
								+ "\t"
								+ (rs.getString(7) != null ? rs.getString(7)
										: "")
								+ "\t"
								+ (rs.getString(8) != null ? rs.getString(8)
										: "")
								+ "\t"
								+ (rs.getString(9) != null ? rs.getString(9)
										: "")
								+ "\t"
								+ (rs.getString(10) != null ? rs.getString(10)
										: "")
								+ "\t"
								+ (rs.getString(11) != null ? rs.getString(11)
										: "")
								+ "\t"
								+ (rs.getString(12) != null ? rs.getString(12)
										: "")
								+ "\t"
								+ (rs.getString(13) != null ? rs.getString(13)
										: "")
								+ "\t"
								+ (rs.getString(14) != null ? rs.getString(14)
										: "")
								+ "\t"
								+ (rs.getString(15) != null ? rs.getString(15)
										: "")
								+ "\t"
								+ (rs.getString(16) != null ? rs.getString(16)
										: ""));
						writeToFile.newLine();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				rs.close();
			}
			writeToFile.close();
			pstmt.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				con.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		return Url;
	}

	public String exportPLAction(String fileName,
			ArrayList<ArrayList<String>> txtDataList, int id) {
		String Url = "";
		String query = "";
		ResultSet rs = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			String pl = "";

			String directory = Utils.createDirector("\\DynamicGenDataTools");
			Url = "DynamicGenDataTools\\" + System.currentTimeMillis()
					+ fileName + ".txt";
			BufferedWriter writeToFile = new BufferedWriter(new FileWriter(Url));
			con = Utils.connectDatabase();
			writeToFile
					.write("Product Line\tPart Number\tSupplier\tCode Name\tCode Version\tDYN_CLASS\tSTATIC_CLASS\tSUPPLIER_CLASS\tSE_CLASS\tREF\tREF_URL\tCONF_CLASS_LVL\tmanual_flag\tSUP_REF_URL\tSTATIC_REF_URL\tModify Date");
			writeToFile.newLine();
			query = "select get_pl_name(p.PL_ID), x.COM_PARTNUM, get_man_name(x.MAN_ID), c.CLAS_NAME, c.CLAS_VER, p.DYN_CLASS, p.STATIC_CLASS, p.SUPPLIER_CLASS, p.SE_CLASS, p.REF, p.REF_URL, p.CONF_CLASS_LVL, p.manual_flag, p.SUP_REF_URL, p.STATIC_REF_URL, p.MODIFY_DATE from importer.PART_CODE_error p,cm.XLP_SE_COMPONENT x,importer.CLASSIFICATION_CODE c where p.PL_ID=get_pl_id(?) and p.CLAS_ID=? and p.COM_ID=x.COM_ID and p.CLAS_ID=c.CLAS_ID and x.NAN_PARTNUM not like ')$.@(%'";
			pstmt = con.prepareStatement(query);
			for (int row = 0; row < txtDataList.size(); row++) {
				try {
					pl = ((String) ((List) txtDataList.get(row)).get(0)).trim()
							.replaceAll("\"", "");
					pstmt.setString(1, pl);
					pstmt.setInt(2, id);
					rs = pstmt.executeQuery();
					while (rs.next()) {
						writeToFile.write((rs.getString(1) != null ? rs
								.getString(1) : "")
								+ "\t"
								+ (rs.getString(2) != null ? rs.getString(2)
										: "")
								+ "\t"
								+ (rs.getString(3) != null ? rs.getString(3)
										: "")
								+ "\t"
								+ (rs.getString(4) != null ? rs.getString(4)
										: "")
								+ "\t"
								+ (rs.getString(5) != null ? rs.getString(5)
										: "")
								+ "\t"
								+ (rs.getString(6) != null ? rs.getString(6)
										: "")
								+ "\t"
								+ (rs.getString(7) != null ? rs.getString(7)
										: "")
								+ "\t"
								+ (rs.getString(8) != null ? rs.getString(8)
										: "")
								+ "\t"
								+ (rs.getString(9) != null ? rs.getString(9)
										: "")
								+ "\t"
								+ (rs.getString(10) != null ? rs.getString(10)
										: "")
								+ "\t"
								+ (rs.getString(11) != null ? rs.getString(11)
										: "")
								+ "\t"
								+ (rs.getString(12) != null ? rs.getString(12)
										: "")
								+ "\t"
								+ (rs.getString(13) != null ? rs.getString(13)
										: "")
								+ "\t"
								+ (rs.getString(14) != null ? rs.getString(14)
										: "")
								+ "\t"
								+ (rs.getString(15) != null ? rs.getString(15)
										: "")
								+ "\t"
								+ (rs.getString(16) != null ? rs.getString(16)
										: ""));
						writeToFile.newLine();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				rs.close();
			}
			writeToFile.close();
			pstmt.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				con.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		return Url;
	}

	public String addToDaily(String fileName, ArrayList<ArrayList<String>> txtDataList) {
		String Url = "";
		String man = "";
		String part = "";
		Statement updateST = null;
		Connection con = null;
		CallableStatement pstmt = null;
		List logData = new ArrayList();
		Statement st = null;

		String sql = "";
		int comId = 0;
		int plId = 0;
		try {
			String directory = Utils.createDirector("\\DynamicGenDataTools");
			Url = "DynamicGenDataTools\\" + System.currentTimeMillis()
					+ fileName + ".txt";
			File file = new File(Url);
			con = Utils.connectDatabase();
			logData.add("Part Number\tSupplier Name\tStatus");

			for (int row = 0; row < txtDataList.size(); row++) {
				try {
					part = ((String) ((List) txtDataList.get(row)).get(0))
							.trim().replaceAll("\"", "");
					man = ((String) ((List) txtDataList.get(row)).get(1))
							.trim().replaceAll("\"", "");

					sql = "select com_id,pl_id from cm.xlp_se_component where COM_PARTNUM='"
							+ part
							+ "' and man_id= cm.get_man_id('"
							+ man
							+ "')";
					st = con.createStatement();
					ResultSet rs = st.executeQuery(sql);
					while (rs.next()) {
						comId = Integer.parseInt(rs.getString("com_id"));
						plId = Integer.parseInt(rs.getString("PL_id"));
						if (comId != -1) {
							try {

								updateST.executeUpdate("insert into clas_change_part (PL_ID,COM_ID,INSERTION_DATE,PRIORITY) values ("
										+ plId + "," + comId + ",sysdate,1)");

								logData.add(part + "\t" + man + "\t"
										+ "Inserted");

							} catch (Exception e) {
								e.printStackTrace();
								logData.add(part + "\t" + man + "\t"
										+ e.getMessage());
							}
						} else {
							logData.add(part + "\t" + man + "\t"
									+ "Part or Supplier is Error");
							System.out.println(part + "t+" + man + "\t"
									+ "Error Part");
						}

					}

				} catch (Exception e) {
					e.printStackTrace();
					logData.add(part + "\t" + man + "\t" + e.getMessage());
				}
			}

			con.close();
			FileUtils.writeLines(file, logData);
		} catch (Exception e) {
			e.printStackTrace();
			Url = "The Error Occurred:\n\t" + e.getMessage();
			try {
				con.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		return Url;
	}
}
