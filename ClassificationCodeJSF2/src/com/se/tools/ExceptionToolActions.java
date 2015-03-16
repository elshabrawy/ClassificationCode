package com.se.tools;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.se.classcode.Generation;
import com.se.classcode.Utils;


public class ExceptionToolActions {

	static String sch = Utils.getProperty("TablesSchame");
	static String log = Utils.getProperty("log.path");
	Generation gen = new Generation();

	public String updatePNSAction(String fileName,
			ArrayList<ArrayList<String>> txtDataList, int id) {
		String status = "";
		String Url = "";
		String query = "";
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			String pn = "", man = "", staticClass = "", refUrl = "";
			@SuppressWarnings("unused")
			String directory = Utils.createDirector(log);
			Url = log + "ExceptionPartsTools\\" + System.currentTimeMillis()
					+ fileName + ".txt";
			BufferedWriter writeToFile = new BufferedWriter(new FileWriter(log+"Log.txt"));
			con = Utils.connectDatabase();
			writeToFile
					.write("Part Number\tSupplier Name\tStatic\tREF_URL\tStatus");
			writeToFile.newLine();
			query = "update cm.PART_CODE set STATIC_CLASS=? ,se_class=? , STATIC_REF_URL=? ,ref='Exception', MANUAL_FLAG=1 where COM_ID=cm.GET_COM_ID(?,CM.GET_MAN_ID(?)) and CLAS_ID=?";
			pstmt = con.prepareStatement(query);
			for (int row = 1; row < txtDataList.size(); row++) {
				try {
					pn = txtDataList.get(row).get(0).trim().replaceAll("\"","");
					man = txtDataList.get(row).get(1).trim().replaceAll("\"","");
					staticClass = txtDataList.get(row).get(2).trim();
					refUrl = txtDataList.get(row).get(3).trim();
					if (!pn.equalsIgnoreCase("") && !man.equalsIgnoreCase("")
							&& !staticClass.equalsIgnoreCase("")) {
						pstmt.setString(1, staticClass);
						pstmt.setString(2, staticClass);
						pstmt.setString(3, refUrl);
						pstmt.setString(4, pn);
						pstmt.setString(5, man);
						pstmt.setInt(6, id);
						// gen.generateRolesByPN(pn, man, id);
						int count = pstmt.executeUpdate();
						if (count > 0)
							status = "Updated";
						else
							status = "Part/ClasID Not Found";
					} else {
						status = "Can't Upload Part Number, Supplier, Or Static By Null";
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					status = e.getMessage();
				}
				writeToFile.write(pn + "\t" + man + "\t" + staticClass + "\t"
						+ refUrl + "\t" + status);
				writeToFile.newLine();
			}
			writeToFile.close();
			con.commit();
			pstmt.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				con.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return Url;
	}

	public String deletePNSAction(String fileName,
			ArrayList<ArrayList<String>> txtDataList, int id) {
		String status = "";
		String Url = "";
		String query = "";
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			String pn = "", man = "";
			@SuppressWarnings("unused")
			String directory = Utils.createDirector(log);
			Url = log + "ExceptionPartsTools\\" + System.currentTimeMillis()
					+ fileName + ".txt";
			BufferedWriter writeToFile = new BufferedWriter(new FileWriter(Url+"Log.txt"));
			con = Utils.connectDatabase();
			writeToFile.write("Part Number\tSupplier Name\tStatus");
			writeToFile.newLine();
			query = "update cm.PART_CODE set MANUAL_FLAG=0,STATIC_CLASS='', STATIC_REF_URL ='' where COM_ID=cm.GET_COM_ID(?,CM.GET_MAN_ID(?)) and CLAS_ID=?";
			pstmt = con.prepareStatement(query);
			for (int row = 1; row < txtDataList.size(); row++) {
				try {
					pn = txtDataList.get(row).get(0).trim().replaceAll("\"","");
					man = txtDataList.get(row).get(1).trim().replaceAll("\"","");
					if (!pn.equalsIgnoreCase("") && !man.equalsIgnoreCase("")) {
						pstmt.setString(1, pn);
						pstmt.setString(2, man);
						pstmt.setInt(3, id);
						int count = pstmt.executeUpdate();
						gen.generateRolesByPN(pn, man, id);
						if (count > 0)
							status = "Deleted";
						else
							status = "Part/ClasID Not Found";
					} else {
						status = "Can't Upload Part Number Or Supplier By Null";
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					status = e.getMessage();
				}
				writeToFile.write(pn + "\t" + man + "\t" + status);
				writeToFile.newLine();
			}
			writeToFile.close();
			con.commit();
			pstmt.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				con.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
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
			@SuppressWarnings("unused")
			String directory = Utils.createDirector(log);
			Url = log + "ExceptionPartsTools\\" + System.currentTimeMillis()
					+ fileName + ".txt";
			BufferedWriter writeToFile = new BufferedWriter(new FileWriter(Url+"Log.txt"));
			con = Utils.connectDatabase();
			writeToFile
					.write("Product Line\tPart Number\tSupplier\tCode Name\tCode Version\tDYN_CLASS\tSTATIC_CLASS\tSUPPLIER_CLASS\tSE_CLASS\tREF\tREF_URL\tCONF_CLASS_LVL\tMANUAL_FLAG\tSUP_REF_URL\tSTATIC_REF_URL\tModify Date");
			writeToFile.newLine();
			query = "select get_pl_name(p.PL_ID), x.COM_PARTNUM, get_man_name(x.MAN_ID), c.CLAS_NAME, c.CLAS_VER, p.DYN_CLASS, p.STATIC_CLASS, p.SUPPLIER_CLASS, p.SE_CLASS, p.REF, p.REF_URL, p.CONF_CLASS_LVL, p.MANUAL_FLAG, p.SUP_REF_URL, p.STATIC_REF_URL, p.MODIFY_DATE from cm.PART_CODE p,cm.XLP_SE_COMPONENT x,"
					+ sch
					+ ".CLASSIFICATION_CODE c where x.man_id = get_man_id(?) and x.COM_ID=p.COM_ID and p.CLAS_ID=? and p.CLAS_ID=c.CLAS_ID and MANUAL_FLAG=1";
			pstmt = con.prepareStatement(query);
			for (int row = 1; row < txtDataList.size(); row++) {
				try {
					supp = txtDataList.get(row).get(0).trim().replaceAll("\"","");
					pstmt.setString(1, supp);
					pstmt.setInt(2, id);
					rs = pstmt.executeQuery();
					while (rs.next()) {
						writeToFile.write(((rs.getString(1) != null) ? rs
								.getString(1) : "")
								+ "\t"
								+ ((rs.getString(2) != null) ? rs.getString(2)
										: "")
								+ "\t"
								+ ((rs.getString(3) != null) ? rs.getString(3)
										: "")
								+ "\t"
								+ ((rs.getString(4) != null) ? rs.getString(4)
										: "")
								+ "\t"
								+ ((rs.getString(5) != null) ? rs.getString(5)
										: "")
								+ "\t"
								+ ((rs.getString(6) != null) ? rs.getString(6)
										: "")
								+ "\t"
								+ ((rs.getString(7) != null) ? rs.getString(7)
										: "")
								+ "\t"
								+ ((rs.getString(8) != null) ? rs.getString(8)
										: "")
								+ "\t"
								+ ((rs.getString(9) != null) ? rs.getString(9)
										: "")
								+ "\t"
								+ ((rs.getString(10) != null) ? rs
										.getString(10) : "")
								+ "\t"
								+ ((rs.getString(11) != null) ? rs
										.getString(11) : "")
								+ "\t"
								+ ((rs.getString(12) != null) ? rs
										.getString(12) : "")
								+ "\t"
								+ ((rs.getString(13) != null) ? rs
										.getString(13) : "")
								+ "\t"
								+ ((rs.getString(14) != null) ? rs
										.getString(14) : "")
								+ "\t"
								+ ((rs.getString(15) != null) ? rs
										.getString(15) : "")
								+ "\t"
								+ ((rs.getString(16) != null) ? rs
										.getString(16) : ""));
						writeToFile.newLine();
					}
				} catch (Exception e) {
					// TODO: handle exception
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
				// TODO Auto-generated catch block
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
			String pn = "", man = "";
			@SuppressWarnings("unused")
			String directory = Utils.createDirector(log);
			Url = log + "ExceptionPartsTools\\" + System.currentTimeMillis()
					+ fileName + ".txt";
			BufferedWriter writeToFile = new BufferedWriter(new FileWriter(log+"Log.txt"));
			con = Utils.connectDatabase();
			writeToFile
					.write("Product Line\tPart Number\tSupplier\tCode Name\tCode Version\tDYN_CLASS\tSTATIC_CLASS\tSUPPLIER_CLASS\tSE_CLASS\tREF\tREF_URL\tCONF_CLASS_LVL\tMANUAL_FLAG\tSUP_REF_URL\tSTATIC_REF_URL\tModify Date");
			writeToFile.newLine();
			query = "select get_pl_name(p.PL_ID), x.COM_PARTNUM, get_man_name(x.MAN_ID), c.CLAS_NAME, c.CLAS_VER, p.DYN_CLASS, p.STATIC_CLASS, p.SUPPLIER_CLASS, p.SE_CLASS, p.REF, p.REF_URL, p.CONF_CLASS_LVL, p.MANUAL_FLAG, p.SUP_REF_URL, p.STATIC_REF_URL, p.MODIFY_DATE from cm.PART_CODE p,cm.XLP_SE_COMPONENT x,"
					+ sch
					+ ".CLASSIFICATION_CODE c where p.COM_ID=cm.GET_COM_ID(?,CM.GET_MAN_ID(?)) and p.CLAS_ID=? and p.COM_ID=x.COM_ID and p.CLAS_ID=c.CLAS_ID ";
			pstmt = con.prepareStatement(query);
			for (int row = 1; row < txtDataList.size(); row++) {
				try {
					pn = txtDataList.get(row).get(0).trim().replaceAll("\"","");
					man = txtDataList.get(row).get(1).trim().replaceAll("\"","");
					pstmt.setString(1, pn);
					pstmt.setString(2, man);
					pstmt.setInt(3, id);
					rs = pstmt.executeQuery();
					while (rs.next()) {
						writeToFile.write(((rs.getString(1) != null) ? rs
								.getString(1) : "")
								+ "\t"
								+ ((rs.getString(2) != null) ? rs.getString(2)
										: "")
								+ "\t"
								+ ((rs.getString(3) != null) ? rs.getString(3)
										: "")
								+ "\t"
								+ ((rs.getString(4) != null) ? rs.getString(4)
										: "")
								+ "\t"
								+ ((rs.getString(5) != null) ? rs.getString(5)
										: "")
								+ "\t"
								+ ((rs.getString(6) != null) ? rs.getString(6)
										: "")
								+ "\t"
								+ ((rs.getString(7) != null) ? rs.getString(7)
										: "")
								+ "\t"
								+ ((rs.getString(8) != null) ? rs.getString(8)
										: "")
								+ "\t"
								+ ((rs.getString(9) != null) ? rs.getString(9)
										: "")
								+ "\t"
								+ ((rs.getString(10) != null) ? rs
										.getString(10) : "")
								+ "\t"
								+ ((rs.getString(11) != null) ? rs
										.getString(11) : "")
								+ "\t"
								+ ((rs.getString(12) != null) ? rs
										.getString(12) : "")
								+ "\t"
								+ ((rs.getString(13) != null) ? rs
										.getString(13) : "")
								+ "\t"
								+ ((rs.getString(14) != null) ? rs
										.getString(14) : "")
								+ "\t"
								+ ((rs.getString(15) != null) ? rs
										.getString(15) : "")
								+ "\t"
								+ ((rs.getString(16) != null) ? rs
										.getString(16) : ""));
						writeToFile.newLine();
					}
				} catch (Exception e) {
					// TODO: handle exception
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
				// TODO Auto-generated catch block
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
			@SuppressWarnings("unused")
			String directory = Utils.createDirector(log);
			Url = log + "ExceptionPartsTools\\" + System.currentTimeMillis()
					+ fileName + ".txt";
			BufferedWriter writeToFile = new BufferedWriter(new FileWriter(Url+"Log.txt"));
			con = Utils.connectDatabase();
			writeToFile
					.write("Product Line\tPart Number\tSupplier\tCode Name\tCode Version\tDYN_CLASS\tSTATIC_CLASS\tSUPPLIER_CLASS\tSE_CLASS\tREF\tREF_URL\tCONF_CLASS_LVL\tMANUAL_FLAG\tSUP_REF_URL\tSTATIC_REF_URL\tModify Date");
			writeToFile.newLine();
			query = "select get_pl_name(p.PL_ID), x.COM_PARTNUM, get_man_name(x.MAN_ID), c.CLAS_NAME, c.CLAS_VER, p.DYN_CLASS, p.STATIC_CLASS, p.SUPPLIER_CLASS, p.SE_CLASS, p.REF, p.REF_URL, p.CONF_CLASS_LVL, p.MANUAL_FLAG, p.SUP_REF_URL, p.STATIC_REF_URL, p.MODIFY_DATE from cm.PART_CODE p,cm.XLP_SE_COMPONENT x,"
					+ sch
					+ ".CLASSIFICATION_CODE c where p.PL_ID=get_pl_id(?) and p.CLAS_ID=? and p.COM_ID=x.COM_ID and p.CLAS_ID=c.CLAS_ID and  MANUAL_FLAG=1";
			pstmt = con.prepareStatement(query);
			for (int row = 1; row < txtDataList.size(); row++) {
				try {
					pl = txtDataList.get(row).get(0).trim().replaceAll("\"","");
					pstmt.setString(1, pl);
					pstmt.setInt(2, id);
					rs = pstmt.executeQuery();
					while (rs.next()) {
						writeToFile.write(((rs.getString(1) != null) ? rs
								.getString(1) : "")
								+ "\t"
								+ ((rs.getString(2) != null) ? rs.getString(2)
										: "")
								+ "\t"
								+ ((rs.getString(3) != null) ? rs.getString(3)
										: "")
								+ "\t"
								+ ((rs.getString(4) != null) ? rs.getString(4)
										: "")
								+ "\t"
								+ ((rs.getString(5) != null) ? rs.getString(5)
										: "")
								+ "\t"
								+ ((rs.getString(6) != null) ? rs.getString(6)
										: "")
								+ "\t"
								+ ((rs.getString(7) != null) ? rs.getString(7)
										: "")
								+ "\t"
								+ ((rs.getString(8) != null) ? rs.getString(8)
										: "")
								+ "\t"
								+ ((rs.getString(9) != null) ? rs.getString(9)
										: "")
								+ "\t"
								+ ((rs.getString(10) != null) ? rs
										.getString(10) : "")
								+ "\t"
								+ ((rs.getString(11) != null) ? rs
										.getString(11) : "")
								+ "\t"
								+ ((rs.getString(12) != null) ? rs
										.getString(12) : "")
								+ "\t"
								+ ((rs.getString(13) != null) ? rs
										.getString(13) : "")
								+ "\t"
								+ ((rs.getString(14) != null) ? rs
										.getString(14) : "")
								+ "\t"
								+ ((rs.getString(15) != null) ? rs
										.getString(15) : "")
								+ "\t"
								+ ((rs.getString(16) != null) ? rs
										.getString(16) : ""));
						writeToFile.newLine();
						// System.out.println(((rs.getString(1) != null) ?
						// rs.getString(1) : "") + "\t" + ((rs.getString(2) !=
						// null) ? rs.getString(2) : "") + "\t" +
						// ((rs.getString(3) != null) ? rs.getString(3) : "") +
						// "\t" + ((rs.getString(4) != null) ? rs.getString(4) :
						// "") + "\t" + ((rs.getString(5) != null) ?
						// rs.getString(5) : "") + "\t" + ((rs.getString(6) !=
						// null) ? rs.getString(6) : "") + "\t" +
						// ((rs.getString(7) != null) ? rs.getString(7) : "") +
						// "\t" + ((rs.getString(8) != null) ? rs.getString(8) :
						// "") + "\t" + ((rs.getString(9) != null) ?
						// rs.getString(9) : "") + "\t" + ((rs.getString(10) !=
						// null) ? rs.getString(10) : "") + "\t" +
						// ((rs.getString(11) != null) ? rs.getString(11) : "")
						// + "\t" + ((rs.getString(12) != null) ?
						// rs.getString(12) : "") + "\t" + ((rs.getString(13) !=
						// null) ? rs.getString(13) : "") + "\t" +
						// ((rs.getString(14) != null) ? rs.getString(14) : "")
						// + "\t" + ((rs.getString(15) != null) ?
						// rs.getString(15) : "") + "\t" + ((rs.getString(16) !=
						// null) ? rs.getString(16) : ""));
					}
				} catch (Exception e) {
					// TODO: handle exception
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
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return Url;
	}

	public String exportAllErrorParts() {
		String Url = "";
		String query = "";
		ResultSet rs = null;
		Connection con = null;

		try {
			String pn = "", man = "";
			@SuppressWarnings("unused")
			String directory = Utils.createDirector(log);
			Url = log + "ExceptionPartsTools\\" + System.currentTimeMillis()
					+ "AllExceptionParts.txt";
			BufferedWriter writeToFile = new BufferedWriter(new FileWriter(log+"Log.txt"));
			con = Utils.connectDatabase();
			writeToFile
					.write("Product Line\tPart Number\tSupplier\tCode Name\tCode Version\tDYN_CLASS\tSTATIC_CLASS\tSUPPLIER_CLASS\tSE_CLASS\tREF\tREF_URL\tCONF_CLASS_LVL\tMANUAL_FLAG\tSUP_REF_URL\tSTATIC_REF_URL\tModify Date");
			writeToFile.newLine();
			// query =
			// "select get_pl_name(p.PL_ID), x.COM_PARTNUM, get_man_name(x.MAN_ID), c.CLAS_NAME, c.CLAS_VER, p.DYN_CLASS, p.STATIC_CLASS, p.SUPPLIER_CLASS, p.SE_CLASS, p.REF, p.REF_URL, p.CONF_CLASS_LVL, p.MANUAL_FLAG, p.SUP_REF_URL, p.STATIC_REF_URL, p.MODIFY_DATE from "
			// + sch
			// + ".PART_CODE p,cm.XLP_SE_COMPONENT x,"
			// + sch
			// + ".CLASSIFICATION_CODE c where MANUAL_FLAG =1";
			query = "select get_pl_name(p.PL_ID), x.COM_PARTNUM, get_man_name(x.MAN_ID), c.CLAS_NAME,c.CLAS_VER, p.DYN_CLASS, p.STATIC_CLASS, p.SUPPLIER_CLASS, p.SE_CLASS, p.REF,p.REF_URL, p.CONF_CLASS_LVL, p.MANUAL_FLAG, p.SUP_REF_URL, p.STATIC_REF_URL, p.MODIFY_DATE from cm.PART_CODE p,cm.XLP_SE_COMPONENT x ,"
					+ sch
					+ ".CLASSIFICATION_CODE c where p.com_id=x.com_id and c.CLAS_ID=p.CLAS_ID"
					+ " and MANUAL_FLAG =1";
			System.out.println(query);
			java.sql.Statement st = con.createStatement();
			rs = st.executeQuery(query);
			int count = 0;
			while (rs.next()) {
				writeToFile.write(((rs.getString(1) != null) ? rs.getString(1)
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
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return Url;
	}
}
