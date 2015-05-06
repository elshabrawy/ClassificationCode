package com.se.tools;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.ArrayList;
import com.se.classcode.Generation;
import com.se.classcode.Utils;



public class StatisticsTooActions {
	static String log = Utils.getProperty("log.path");
	Generation gen=new Generation();

	public  String exportBlankParts( int id) {
		String status = "";
		String Url = "";
		String query = "";
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			String pn = "", man = "", staticClass = "", refUrl = "";
			@SuppressWarnings("unused")
			String directory = Utils.createDirector(log
					+ "\\ExceptionPartsTools");
			Url = log + "ExceptionPartsTools\\" + System.currentTimeMillis()
					+ ".txt";
			BufferedWriter writeToFile = new BufferedWriter(new FileWriter(Url));
			con = Utils.connectDatabase();
			writeToFile
					.write("Product Line\tPart Number\tSupplier\tCode Name\tCode Version\tDYN_CLASS\tSTATIC_CLASS\tSUPPLIER_CLASS\tSE_CLASS\tREF\tREF_URL\tCONF_CLASS_LVL\tMANUAL_FLAG\tSUP_REF_URL\tSTATIC_REF_URL\tModify Date");
			writeToFile.newLine();
			query = "select get_pl_name(p.PL_ID), x.COM_PARTNUM, get_man_name(x.MAN_ID), c.CLAS_NAME,c.CLAS_VER, p.DYN_CLASS, p.STATIC_CLASS, p.SUPPLIER_CLASS, p.SE_CLASS, p.REF,p.REF_URL, p.CONF_CLASS_LVL, p.MANUAL_FLAG, p.SUP_REF_URL, p.STATIC_REF_URL, p.MODIFY_DATE from  cm.PART_CODE p,cm.XLP_SE_COMPONENT x ,  importer.CLASSIFICATION_CODE c where p.com_id=x.com_id(+)and c.CLAS_ID=p.CLAS_ID and SE_CLASS is null and c.CLAS_ID= "
					+ id;
			System.out.println(query);
			st = con.createStatement();
			rs = st.executeQuery(query);
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

	public  String exportPartCodes(ArrayList<ArrayList<String>> txtDataList) {
		String Url = "";
		String query = "";
		ResultSet rs = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			String pn = "", man = "";
			@SuppressWarnings("unused")
			String directory = Utils.createDirector(log + "\\Statisstics");
			Url = log + "Statisstics\\" + System.currentTimeMillis() + ".txt";
			BufferedWriter writeToFile = new BufferedWriter(new FileWriter(Url));
			con = Utils.connectDatabase();
			writeToFile
					.write("Product Line\tPart Number\tSupplier\tCode Name\tCode Version\tDYN_CLASS\tSTATIC_CLASS\tSUPPLIER_CLASS\tSE_CLASS\tREF\tREF_URL\tCONF_CLASS_LVL\tMANUAL_FLAG\tSUP_REF_URL\tSTATIC_REF_URL\tModify Date");
			writeToFile.newLine();
			query = "select get_pl_name(p.PL_ID), x.COM_PARTNUM, get_man_name(x.MAN_ID), c.CLAS_NAME, c.CLAS_VER, p.DYN_CLASS, p.STATIC_CLASS, p.SUPPLIER_CLASS, p.SE_CLASS, p.REF, p.REF_URL, p.CONF_CLASS_LVL, p.MANUAL_FLAG, p.SUP_REF_URL, p.STATIC_REF_URL, p.MODIFY_DATE from cm.PART_CODE p,cm.XLP_SE_COMPONENT x,importer.CLASSIFICATION_CODE c where p.COM_ID=cm.GET_COM_ID(?,CM.GET_MAN_ID(?)) and p.COM_ID=x.COM_ID and P.CLAS_ID = C.CLAS_ID and c.DISP_FLAG = 1";
			pstmt = con.prepareStatement(query);
			for (int row = 0; row < txtDataList.size(); row++) {
				try {
					pn = txtDataList.get(row).get(0).trim();
					man = txtDataList.get(row).get(1).trim();
					pstmt.setString(1, pn);
					pstmt.setString(2, man);
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

	public String exportTBLParts( int id) {
		String status = "";
		String Url = "";
		String query = "";
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			String pn = "", man = "", staticClass = "", refUrl = "";
			@SuppressWarnings("unused")
			String directory = Utils.createDirector(log
					+ "\\ExceptionPartsTools");
			Url = log + "ExceptionPartsTools\\" + System.currentTimeMillis()
					+ ".txt";
			BufferedWriter writeToFile = new BufferedWriter(new FileWriter(Url));
			con = Utils.connectDatabase();
			writeToFile
					.write("Product Line\tPart Number\tSupplier\tCode Name\tCode Version\tDYN_CLASS\tSTATIC_CLASS\tSUPPLIER_CLASS\tSE_CLASS\tREF\tREF_URL\tCONF_CLASS_LVL\tMANUAL_FLAG\tSUP_REF_URL\tSTATIC_REF_URL\tModify Date");
			writeToFile.newLine();
			query = "select get_pl_name(p.PL_ID), x.COM_PARTNUM, get_man_name(x.MAN_ID), c.CLAS_NAME,c.CLAS_VER, p.DYN_CLASS, p.STATIC_CLASS, p.SUPPLIER_CLASS, p.SE_CLASS, p.REF,p.REF_URL, p.CONF_CLASS_LVL, p.MANUAL_FLAG, p.SUP_REF_URL, p.STATIC_REF_URL, p.MODIFY_DATE from  cm.PART_CODE p,cm.XLP_SE_COMPONENT x , importer.CLASSIFICATION_CODE c where p.com_id=x.com_id and c.CLAS_ID=p.CLAS_ID and DYN_CLASS ='TBD' and c.CLAS_ID = "
					+ id;
			System.out.println(query);
			// select com_id,count(CLAS_ID ) from part_code group by com_id
			// having count(clas_id)>1
			st = con.createStatement();
			rs = st.executeQuery(query);
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

	public  String exportPLandPN(int id) {
		String status = "";
		String Url = "";
		String query = "";
		String Query2 = "";
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		Statement st2 = null;
		ResultSet rs2 = null;
		Statement st3 = null;
		ResultSet rs3 = null;
		Statement st4 = null;
		ResultSet rs4 = null;
		int count = 0;
		try {
			String pn = "", man = "", staticClass = "", refUrl = "";
			@SuppressWarnings("unused")
			String directory = Utils.createDirector(log
					+ "\\ExceptionPartsTools");
			Url = log + "ExceptionPartsTools\\" + System.currentTimeMillis()
					+ ".txt";
			BufferedWriter writeToFile = new BufferedWriter(new FileWriter(Url));
			con = Utils.connectDatabase();
			writeToFile.write("Product Line\tCodeName\tCodeVersion\tNumber of Parts");
			writeToFile.newLine();
			Query2 = "select distinct pl_id from cm.part_code ";
			st2 = con.createStatement();
			rs2 = st2.executeQuery(Query2);
			while (rs2.next()) {
				query = "select * from cm.PART_CODE where se_class is not null and PL_ID ="
						+ rs2.getString("PL_ID")+ " and CLAS_ID = "+id ;
				System.out.println(query);
				st = con.createStatement();
				rs = st.executeQuery(query);
				while (rs.next()) {
					count++;
				}
				rs.close();
				st.close();
				st3 = con.createStatement();
				rs3 = st3.executeQuery("select GET_PL_NAME("+rs2.getString("PL_ID")+") PL_NAME from dual");
				String plName="";
				while (rs3.next()){
					plName=rs3.getString("PL_NAME");
				}
				rs3.close();
				st3.close();
				st4 = con.createStatement();
				rs4 = st4.executeQuery("select CLAS_NAME,CLAS_VER from CLASSIFICATION_CODE where CLAS_ID="+id);
				
				rs4.next();
				writeToFile.write(plName+"\t"+rs4.getString("CLAS_NAME")+"\t"+rs4.getString("CLAS_VER")+"\t" + count);
				count=0;
				writeToFile.newLine();
				rs4.close();
				st4.close();
			}
			rs2.close();
			st2.close();
			writeToFile.close();

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

	public  String exportPL(int id) {
		String status = "";
		String Url = "";
		String query = "";
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		Statement st1 = null;
		ResultSet rs1 = null;
		try {
			String pn = "", man = "", staticClass = "", refUrl = "";
			@SuppressWarnings("unused")
			String directory = Utils.createDirector(log
					+ "\\ExceptionPartsTools");
			Url = log + "ExceptionPartsTools\\" + System.currentTimeMillis()
					+ ".txt";
			BufferedWriter writeToFile = new BufferedWriter(new FileWriter(Url));
			con = Utils.connectDatabase();
			writeToFile.write("Code Name"+"\t"+"Code Version"+"\t"+"Number of parts that take Code");
			writeToFile.newLine();
			query = "select * from cm.PART_CODE where se_class is not null  and CLAS_ID =" +id;

			System.out.println(query);
			st = con.createStatement();
			rs = st.executeQuery(query);
			int count = 0;
			while (rs.next()) {
				count++;
			}

			rs.close();
			st1 = con.createStatement();
			rs1 = st1.executeQuery("select CLAS_NAME,CLAS_VER from CLASSIFICATION_CODE where CLAS_ID="+id);
			rs1.next();
			writeToFile.write(""  +rs1.getString("CLAS_NAME")+"\t"+rs1.getString("CLAS_VER")+"\t"+ count);
			writeToFile.close();
			rs1.next();
			rs1.close();
			st1.close();
			
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
