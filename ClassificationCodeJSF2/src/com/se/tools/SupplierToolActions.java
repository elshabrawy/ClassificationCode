package com.se.tools;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;

import com.se.classcode.Generation;
import com.se.classcode.Utils;

// =======================================================================================
// ********************************* Sherif ELSbaey **************************************
// ======================================EIVA=============================================

/**
 * @author sherif_elsbaey
 */
@ManagedBean
public class SupplierToolActions {

	// static String sch = Utils.getProperty("TablesSchame");
	static String log = Utils.getProperty("log.path");
	Generation gen = new Generation();

	public String updatePNSAction(String fileName,
			ArrayList<ArrayList<String>> txtDataList/* , int id */) {
		String status = "";
		Statement st = null;
		ResultSet rs = null;
		String Url = "";
		String query = "";
		String updateQuery1 = "";
		String updateQuery2 = "";
		Connection con = null;
		PreparedStatement pstmt1 = null;
		PreparedStatement pstmt2 = null;
		HashMap<String, Integer> classIds = null;
		try {
			String pn = "", man = "", supplierClass = "", newSupplierClass = "", oldSupplierClass = "", refUrl = "", className = "", classVer = "",partNumber;
			
			int id, comId = -1;
			long pdfId = -1;
			@SuppressWarnings("unused")
			String directory = Utils.createDirector(log);
			Url = log + "SupplierInfoTools\\" + System.currentTimeMillis()
					+ fileName + ".txt";
			BufferedWriter writeToFile = new BufferedWriter(new FileWriter(log
					+ "Log.txt"));
			con = Utils.connectDatabase();

			writeToFile
					.write("Old Part Number\tNew Part Number\tSupplier Name\tClass Name\tClass Version\tSupplier\tREF_URL\tStatus");
			writeToFile.newLine();

			classIds = getClassIDs(con);

			updateQuery1 = "update cm.PART_CODE set SUP_REF_URL=? , SUP_REF_URL_ID = ?, reason='SUP_REF_URL Change', MODIFY_DATE = sysdate where COM_ID=? and CLAS_ID=?";
			updateQuery2 = "update cm.PART_CODE set SUPPLIER_CLASS=? , SUP_REF_URL=? , SUP_REF_URL_ID = ?, reason='Supplier Code Change', MODIFY_DATE = sysdate where COM_ID=? and CLAS_ID=?";
			System.out.println("Q: " + query);
			pstmt1 = con.prepareStatement(updateQuery1);
			pstmt2 = con.prepareStatement(updateQuery2);
			String comPart = "";
			DD: for (int row = 1; row < txtDataList.size(); row++) {
				try {
					comPart = "";
					comId = -1;
					pdfId = -1;
					pn = txtDataList.get(row).get(0).trim().replaceAll("\"","");
					partNumber=pn.replaceAll("'", "''");
					man = txtDataList.get(row).get(1).trim().replaceAll("\"","");
					className = txtDataList.get(row).get(2).trim();
					classVer = txtDataList.get(row).get(3).trim();
					supplierClass = txtDataList.get(row).get(4).trim();
					newSupplierClass = supplierClass.replaceAll(
							"[\\.\\/\\-\\_\\%\\±\\~\\!\\@\\$\\*\\:\\;]", "");
					refUrl = txtDataList.get(row).get(5).trim();

					id = classIds.get(className + "}" + classVer);

					// check classId
					if (id == -1) {
						writeToFile.write(pn + "\t\t" + man + "\t" + className
								+ "\t" + classVer + "\t" + supplierClass + "\t"
								+ refUrl + "\t" + "Not Enabled Class ID");
						writeToFile.newLine();
						continue DD;
					}

					// check pn & vendor in xlp_se_component table
					query = "select com_id from cm.xlp_se_component where COM_PARTNUM ='"
							+ partNumber + "' and man_id=cm.get_man_id('" + man + "')";
					try {
						st = con.createStatement();
						rs = st.executeQuery(query);
						if (rs.next()) {
							comId = rs.getInt(1);
						}
						if (comId == -1) {
							query = "select com_id,COM_PARTNUM  from cm.xlp_se_component where NAN_PARTNUM =cm.NONALPHANUM('"
									+ partNumber
									+ "') and man_id=cm.get_man_id('"
									+ man + "')";

							st = con.createStatement();
							rs = st.executeQuery(query);
							if (rs.next()) {
								comId = rs.getInt(1);
								comPart = rs.getString(2);
							}
							if (comId == -1) {
								writeToFile.write(pn + "\t\t" + man + "\t"
										+ className + "\t" + classVer + "\t"
										+ supplierClass + "\t" + refUrl + "\t"
										+ "Not SE Part");
								writeToFile.newLine();
								continue DD;
							}
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					} finally {
						st.close();
						rs.close();
					}

					// check pn & class id in part_code table
					query = "SELECT SUPPLIER_CLASS FROM cm.PART_CODE WHERE COM_ID = "
							+ comId + " AND CLAS_ID  = " + id;
					try {
						st = con.createStatement();
						rs = st.executeQuery(query);
						if (rs.next()) {
							oldSupplierClass = (rs.getString(1) == null) ? ""
									: rs.getString(1);
						}
						// if(oldSupplierClass==null){
						// writeToFile.write(pn + "\t" + man + "\t" + className
						// +"\t"+ classVer +"\t"+ supplierClass + "\t" + refUrl
						// + "\t" + "Not Found in Part_Code");
						// writeToFile.newLine();
						// continue DD;
						// }
					} catch (Exception ex) {
						ex.printStackTrace();
					} finally {
						st.close();
						rs.close();
					}

					if (!refUrl.equals("")) {
						st = con.createStatement();
						rs = st.executeQuery("select importer.GET_PDF_ID_By_URL('"
								+ refUrl + "') pdf from dual");
						while (rs.next()) {
							// refUrl =
							// rs.getString("pdf");//==null)?null:rs.getLong("pdf");
							System.out.println("pdf url is " + refUrl);
							pdfId = rs.getLong("pdf");
						}
						st.close();
						rs.close();
						if (pdfId == -1 || pdfId==0) {
							writeToFile.write(pn + "\t" + comPart + "\t" + man
									+ "\t" + className + "\t" + classVer + "\t" + supplierClass + "\t" + refUrl
									+ "\t" + "Please Insert Offline");
							writeToFile.newLine();
							continue DD;
						}
					}

					System.out.println(" id " + id);
					System.out.println("pn >> " + pn + " man >>" + man
							+ " supplierClass >>" + supplierClass
							+ " refUrl >>" + refUrl);

					// check new supplier code with the old one
					// if same, update sup_ref_url and id
					if (newSupplierClass.toLowerCase().equals(
							oldSupplierClass.replaceAll(
									"[\\.\\/\\-\\_\\%\\±\\~\\!\\@\\$\\*\\:\\;]", "").toLowerCase())) {
						pstmt1.setString(1, refUrl);
						pstmt1.setLong(2, pdfId);
						pstmt1.setInt(3, comId);
						pstmt1.setInt(4, id);
						int count = pstmt1.executeUpdate();
						if (count > 0) {
							status = "Update Supplier Ref URL";
						} else {
							status = "Part/ClasID Not Found";
						}
					}
					// if not same update the supplier code
					else if (!supplierClass.equalsIgnoreCase("")) {
						pstmt2.setString(1, supplierClass);
						pstmt2.setString(2, refUrl);
						pstmt2.setLong(3, pdfId);
						pstmt2.setInt(4, comId);
						pstmt2.setInt(5, id);
						int count = pstmt2.executeUpdate();
						if (comPart.equals("")) {
							gen.generateRolesByPN(pn, man, id);
						} else {
							gen.generateRolesByPN(comPart, man, id);
						}
						if (count > 0)
							status = "Updated";
						else
							status = "Part/ClasID Not Found";
					} else {
						status = "Can't Update Supplier By Null";
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					status = e.getMessage();
				}
				writeToFile.write(pn + "\t" + comPart + "\t" + man + "\t"
						+ className + "\t" + classVer + "\t" + supplierClass
						+ "\t" + refUrl + "\t" + status);
				writeToFile.newLine();
			}
			writeToFile.close();
			con.commit();
			pstmt1.close();
			pstmt2.close();
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

	public String updatePLSuppAction(String fileName,
			ArrayList<ArrayList<String>> txtDataList, int id) {
		String status = "";
		String Url = "";
		Statement st = null;
		ResultSet rs = null;
		String query = "";
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			String pl = "", man = "", supplierClass = "", refUrl = "";
			@SuppressWarnings("unused")
			String directory = Utils.createDirector(log);
			Url = log + "SupplierInfoTools\\" + System.currentTimeMillis()
					+ fileName + ".txt";
			BufferedWriter writeToFile = new BufferedWriter(new FileWriter(log
					+ "Log.txt"));
			con = Utils.connectDatabase();
			writeToFile
					.write("Product Line\tSupplier Name\tSupplier\tREF_URL\tStatus");
			writeToFile.newLine();
			query = "update cm.PART_CODE p set SUPPLIER_CLASS=? , SUP_REF_URL=? ,sup_ref_url_id=?, reason='Supplier Code Change' where EXISTS (SELECT com_id FROM cm.xlp_se_component x WHERE x.PL_ID=get_pl_id(?) and x.man_id = get_man_id(?) and x.COM_ID=p.COM_ID) and CLAS_ID=?";
			pstmt = con.prepareStatement(query);
			String runUrlId = "";
			DD: for (int row = 1; row < txtDataList.size(); row++) {
				try {
					pl = txtDataList.get(row).get(0).trim().replaceAll("\"","");
					man = txtDataList.get(row).get(1).trim().replaceAll("\"","");
					supplierClass = txtDataList.get(row).get(2).trim();
					refUrl = txtDataList.get(row).get(3).trim();
					if (!refUrl.equals("")) {
						st = con.createStatement();
						rs = st.executeQuery("select importer.GET_PDF_ID_By_URL('"
								+ refUrl + "') pdf from dual");
						while (rs.next()) {
							runUrlId = rs.getString("pdf");// ==null)?null:rs.getString("pdf").toString();
							System.out.println("pdf url is " + refUrl);
						}
						if (runUrlId == null || runUrlId.equals("")) {
							writeToFile.write(pl + "\t" + man + "\t"
									+ supplierClass + "\t" + refUrl + "\t"
									+ "Please Insert Offline");
							writeToFile.newLine();
							continue DD;
						}
					}
					if (!supplierClass.equalsIgnoreCase("")) {
						pstmt.setString(1, supplierClass);
						pstmt.setString(2, refUrl);
						pstmt.setString(4, pl);
						pstmt.setString(5, man);
						pstmt.setInt(6, id);
						pstmt.setString(3, runUrlId);
						int count = pstmt.executeUpdate();
						gen.generateRolesByPL(pl, id);
						if (count > 0)
							status = "Updated";
						else
							status = "PL/ClasID Not Found";
					} else {
						status = "Can't Update Supplier By Null";
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					status = e.getMessage();
				}
				writeToFile.write(pl + "\t" + man + "\t" + supplierClass + "\t"
						+ refUrl + "\t" + status);
				writeToFile.newLine();
				runUrlId = null;
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

	//
	public String deletePNSAction(String fileName,
			ArrayList<ArrayList<String>> txtDataList, int id) {
		String status = "";
		String Url = "";
		String query = "";
		Connection con = null;
		PreparedStatement pstmt = null;
		Long runUrlId = null;
		try {
			String pn = "", man = "";
			@SuppressWarnings("unused")
			String directory = Utils.createDirector(log);
			Url = log + "SupplierInfoTools\\" + System.currentTimeMillis()
					+ fileName + ".txt";
			BufferedWriter writeToFile = new BufferedWriter(new FileWriter(log
					+ "Log.txt"));
			con = Utils.connectDatabase();
			writeToFile.write("Part Number\tSupplier Name\tStatus");
			writeToFile.newLine();
			query = "update cm.PART_CODE set SUPPLIER_CLASS=null , SUP_REF_URL=null ,sup_ref_url_id=null, reason='Supplier Code Change' where COM_ID=cm.GET_COM_ID(?,CM.GET_MAN_ID(?)) and CLAS_ID=?";
			pstmt = con.prepareStatement(query);
			for (int row = 1; row < txtDataList.size(); row++) {
				try {
					pn = txtDataList.get(row).get(0).trim().replaceAll("\"","");
					man = txtDataList.get(row).get(1).trim().replaceAll("\"","");
					pstmt.setString(1, pn);
					pstmt.setString(2, man);
					pstmt.setInt(3, id);
					int count = pstmt.executeUpdate();
					gen.generateRolesByPN(pn, man, id);
					if (count > 0)
						status = "Deleted";
					else
						status = "Part/ClasID Not Found";
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

	//
	public String updateSuppAction(String fileName,
			ArrayList<ArrayList<String>> txtDataList, int id) {
		String status = "";
		String Url = "";
		String query = "";
		Connection con = null;
		PreparedStatement pstmt = null;
		Statement st = null;
		ResultSet rs = null;
		Long runUrlId = null;
		try {
			String man = "", staticClass = "", refUrl = "";
			@SuppressWarnings("unused")
			String directory = Utils.createDirector(log);
			Url = log + "SupplierInfoTools\\" + System.currentTimeMillis()
					+ fileName + ".txt";
			BufferedWriter writeToFile = new BufferedWriter(new FileWriter(log
					+ "Log.txt"));
			con = Utils.connectDatabase();
			writeToFile.write("Supplier Name\tSupplier\tREF_URL\tStatus");
			writeToFile.newLine();
			query = "update cm.PART_CODE p set SUPPLIER_CLASS=? ,sup_ref_url_id=?, reason='Supplier Code Change', SUP_REF_URL=? where EXISTS (SELECT com_id FROM cm.xlp_se_component x WHERE x.man_id = get_man_id(?) and x.COM_ID=p.COM_ID) and CLAS_ID=?";
			pstmt = con.prepareStatement(query);
			DD: for (int row = 1; row < txtDataList.size(); row++) {
				try {
					man = txtDataList.get(row).get(0).trim().replaceAll("\"", "");
					staticClass = txtDataList.get(row).get(1).trim();
					refUrl = txtDataList.get(row).get(2).trim();
					if (!refUrl.equals("")) {
						st = con.createStatement();
						rs = st.executeQuery("select importer.GET_PDF_ID_By_URL('"
								+ refUrl + "') pdf from dual");
						while (rs.next()) {
							runUrlId = rs.getLong("pdf");// ==null)?null:rs.getString("pdf").toString();
							System.out.println("pdf url is " + refUrl);
						}
						if (runUrlId == null || runUrlId.equals("")) {
							writeToFile.write(man + "\t" + staticClass + "\t"
									+ refUrl + "\t" + "Please Insert Offline");
							writeToFile.newLine();
							continue DD;
						}
					}
					if (!staticClass.equalsIgnoreCase("")) {
						pstmt.setString(1, staticClass);
						pstmt.setString(3, refUrl);
						pstmt.setString(4, man);
						pstmt.setInt(5, id);
						pstmt.setLong(2, runUrlId);
						int count = pstmt.executeUpdate();
						gen.runTruthByMan(man, id);
						if (count > 0)
							status = "Updated";
						else
							status = "Supplier/ClasID Not Found";
					} else {
						status = "Can't Update Supplier By Null";
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					status = e.getMessage();
				}
				writeToFile.write(man + "\t" + staticClass + "\t" + refUrl
						+ "\t" + status);
				writeToFile.newLine();
				runUrlId = null;
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

	//
	public String deleteSuppAction(String fileName,
			ArrayList<ArrayList<String>> txtDataList, int id) {
		String status = "";
		String Url = "";
		String query = "";
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			String man = "";
			@SuppressWarnings("unused")
			String directory = Utils.createDirector(log);
			Url = log + "SupplierInfoTools\\" + System.currentTimeMillis()
					+ fileName + ".txt";
			BufferedWriter writeToFile = new BufferedWriter(new FileWriter(log
					+ "Log.txt"));
			con = Utils.connectDatabase();
			writeToFile.write("Supplier Name\tStatus");
			writeToFile.newLine();
			query = "update cm.PART_CODE p set SUPPLIER_CLASS=null , reason='Supplier Code Change' , SUP_REF_URL=null,SUP_REF_URL_id=null where EXISTS (SELECT com_id FROM  cm.xlp_se_component x WHERE x.man_id = cm.get_man_id(?) and x.COM_ID=p.COM_ID) and CLAS_ID=?";
			pstmt = con.prepareStatement(query);
			for (int row = 1; row < txtDataList.size(); row++) {
				try {
					man = txtDataList.get(row).get(0).trim().replaceAll("\"","");
					pstmt.setString(1, man);
					pstmt.setInt(2, id);
					int count = pstmt.executeUpdate();
					gen.runTruthByMan(man, id);
					if (count > 0)
						status = "Deleted";
					else
						status = "Supplier/ClasID Not Found";
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					status = e.getMessage();
				}
				writeToFile.write(man + "\t" + status);
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

	//
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
			Url = log + "SupplierInfoTools\\" + System.currentTimeMillis()
					+ fileName + ".txt";
			BufferedWriter writeToFile = new BufferedWriter(new FileWriter(log
					+ "Log.txt"));
			con = Utils.connectDatabase();
			writeToFile
					.write("Product Line\tPart Number\tSupplier\tCode Name\tCode Version\tDYN_CLASS\tSTATIC_CLASS\tSUPPLIER_CLASS\tSE_CLASS\tREF\tREF_URL\tCONF_CLASS_LVL\tMANUAL_FLAG\tSUP_REF_URL\tSTATIC_REF_URL\tModify Date");
			writeToFile.newLine();
			query = "select get_pl_name(p.PL_ID), x.COM_PARTNUM, get_man_name(x.MAN_ID), c.CLAS_NAME, c.CLAS_VER, p.DYN_CLASS, p.STATIC_CLASS, p.SUPPLIER_CLASS, p.SE_CLASS, p.REF, p.REF_URL, p.CONF_CLASS_LVL, p.MANUAL_FLAG, p.SUP_REF_URL, p.STATIC_REF_URL, p.MODIFY_DATE from cm.PART_CODE p,cm.XLP_SE_COMPONENT x,importer.CLASSIFICATION_CODE c where p.COM_ID=cm.GET_COM_ID(?,CM.GET_MAN_ID(?)) and p.CLAS_ID=? and p.COM_ID=x.COM_ID and p.CLAS_ID=c.CLAS_ID";
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

	//
	// //written by mohamed El-shabrawy
	//
	public String exportPLSAction(String fileName,
			ArrayList<ArrayList<String>> txtDataList, int id) {
		String Url = "";
		String query = "";
		ResultSet rs = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			String PL = "";
			String supp = "";
			@SuppressWarnings("unused")
			String directory = Utils.createDirector(log);
			Url = log + "SupplierInfoTools\\" + System.currentTimeMillis()
					+ fileName + ".txt";
			BufferedWriter writeToFile = new BufferedWriter(new FileWriter(log
					+ "Log.txt"));
			con = Utils.connectDatabase();
			writeToFile
					.write("Product Line\tPart Number\tSupplier\tCode Name\tCode Version\tDYN_CLASS\tSTATIC_CLASS\tSUPPLIER_CLASS\tSE_CLASS\tREF\tREF_URL\tCONF_CLASS_LVL\tMANUAL_FLAG\tSUP_REF_URL\tSTATIC_REF_URL\tModify Date");
			writeToFile.newLine();
			query = "select get_pl_name(p.PL_ID), x.COM_PARTNUM, get_man_name(x.MAN_ID), c.CLAS_NAME, c.CLAS_VER, p.DYN_CLASS, p.STATIC_CLASS, p.SUPPLIER_CLASS, p.SE_CLASS, p.REF, p.REF_URL, p.CONF_CLASS_LVL, p.MANUAL_FLAG, p.SUP_REF_URL, p.STATIC_REF_URL, p.MODIFY_DATE from cm.PART_CODE p,cm.XLP_SE_COMPONENT x, importer.CLASSIFICATION_CODE c where x.pl_id=cm.get_pl_id(?) and x.man_id=cm.get_man_id(?) and p.CLAS_ID=? and p.COM_ID=x.COM_ID and p.CLAS_ID=c.CLAS_ID";
			pstmt = con.prepareStatement(query);
			for (int row = 1; row < txtDataList.size(); row++) {
				try {
					PL = txtDataList.get(row).get(0).trim().replaceAll("\"","");
					supp = txtDataList.get(row).get(1).trim().replaceAll("\"","");
					pstmt.setString(1, PL);
					pstmt.setString(2, supp);
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

	// //Written by mohamed El-shabrawy
	public String deletePLSAction(String fileName,
			ArrayList<ArrayList<String>> txtDataList, int id) {
		String status = "";
		String Url = "";
		String query = "";
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			String pl = "";
			@SuppressWarnings("unused")
			String directory = Utils.createDirector(log);
			Url = log + "SupplierInfoTools\\" + System.currentTimeMillis()
					+ fileName + ".txt";
			BufferedWriter writeToFile = new BufferedWriter(new FileWriter(log
					+ "Log.txt"));
			con = Utils.connectDatabase();
			writeToFile.write("Supplier Name\tStatus");
			writeToFile.newLine();
			query = "update cm.PART_CODE p set SUPPLIER_CLASS=null , reason='Supplier Code Change' ,SUP_REF_URL=null,SUP_REF_URL_id=null where EXISTS (SELECT com_id FROM  cm.xlp_se_component x WHERE x.pl_id = cm.get_pl_id(?) and x.COM_ID=p.COM_ID) and CLAS_ID=?";
			pstmt = con.prepareStatement(query);
			for (int row = 1; row < txtDataList.size(); row++) {
				try {
					pl = txtDataList.get(row).get(0).trim().replaceAll("\"","");
					pstmt.setString(1, pl);
					pstmt.setInt(2, id);
					int count = pstmt.executeUpdate();
					gen.generateRolesByPL(pl, id);
					if (count > 0)
						status = "Deleted";
					else
						status = "Supplier/ClasID Not Found";
				} catch (Exception e) {
					e.printStackTrace();
					status = e.getMessage();
				}
				writeToFile.write(pl + "\t" + status);
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
			Url = log + "SupplierInfoTools\\" + System.currentTimeMillis()
					+ fileName + ".txt";
			BufferedWriter writeToFile = new BufferedWriter(new FileWriter(log
					+ "Log.txt"));
			con = Utils.connectDatabase();
			writeToFile
					.write("Product Line\tPart Number\tSupplier\tCode Name\tCode Version\tDYN_CLASS\tSTATIC_CLASS\tSUPPLIER_CLASS\tSE_CLASS\tREF\tREF_URL\tCONF_CLASS_LVL\tMANUAL_FLAG\tSUP_REF_URL\tSTATIC_REF_URL\tModify Date");
			writeToFile.newLine();
			query = "select get_pl_name(p.PL_ID), x.COM_PARTNUM, get_man_name(x.MAN_ID), c.CLAS_NAME, c.CLAS_VER, p.DYN_CLASS, p.STATIC_CLASS, p.SUPPLIER_CLASS, p.SE_CLASS, p.REF, p.REF_URL, p.CONF_CLASS_LVL, p.MANUAL_FLAG, p.SUP_REF_URL, p.STATIC_REF_URL, p.MODIFY_DATE from cm.PART_CODE p,cm.XLP_SE_COMPONENT x,importer.CLASSIFICATION_CODE c where x.man_id = get_man_id(?) and x.COM_ID=p.COM_ID and p.CLAS_ID=? and p.CLAS_ID=c.CLAS_ID";
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

	//
	public HashMap<String, Integer> getClassIDs(Connection con) {
		Statement stmt = null;
		ResultSet rs = null;
		int classId;
		String className;
		String classVer;
		HashMap<String, Integer> classes = new HashMap<String, Integer>();
		String allClassQuery = "SELECT CLAS_ID, CLAS_NAME, CLAS_VER FROM importer.CLASSIFICATION_CODE WHERE ENABLE = 1";
		try {
			// get enable class codes form classification code table
			stmt = con.createStatement();
			rs = stmt.executeQuery(allClassQuery);
			while (rs.next()) {
				classId = rs.getInt(1);
				className = rs.getString(2);
				classVer = rs.getString(3);
				classes.put(className + "}" + classVer, classId);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				stmt.close();
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return classes;
	}
}
