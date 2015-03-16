package com.se.tools;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.se.classcode.Generation;
import com.se.classcode.Utils;

public class StaticToolActions {

	
	static String log = Utils.getProperty("log.path");
	Generation gen=new Generation();
	public  String updatePNSAction(String fileName, ArrayList<ArrayList<String>> txtDataList, int id) {
		Statement st = null;
		ResultSet rs = null;
		String status = "";
		String Url = "";
		String query = "";
		Connection con = null;
		PreparedStatement pstmt = null;
		String pdfId="";
		try{
			String pn = "", man = "", staticClass = "", refUrl = "";
			@SuppressWarnings("unused")
			String directory = Utils.createDirector(log);
			Url = log + "StaticInfoTools\\" + System.currentTimeMillis() + fileName + ".txt";
			BufferedWriter writeToFile = new BufferedWriter(new FileWriter(log+"Log.txt"));
			con = Utils.connectDatabase();
			writeToFile.write("Part Number\tSupplier Name\tStatic\tREF_URL\tStatus");
			writeToFile.newLine();
			query = "update cm.PART_CODE set STATIC_CLASS=? , STATIC_REF_URL=? ,reason='Static Code Change' where COM_ID=cm.GET_COM_ID(?,CM.GET_MAN_ID(?)) and CLAS_ID=? and MANUAL_FLAG =0";
			pstmt = con.prepareStatement(query);
			DD:
			for(int row = 1; row < txtDataList.size(); row++){
				try{
					pn = txtDataList.get(row).get(0).trim().replaceAll("\"","");
					man = txtDataList.get(row).get(1).trim().replaceAll("\"","");
					staticClass = txtDataList.get(row).get(2).trim();
					refUrl = txtDataList.get(row).get(3).trim();
					
					if(!refUrl.equals("") )
					{
						st = con.createStatement();
						rs = st.executeQuery("select importer.GET_PDF_ID_By_URL('" + refUrl + "') pdf from dual");
						while(rs.next())
						{
							pdfId = (rs.getString("pdf")==null)?null:rs.getString("pdf").toString();
							System.out.println("pdf url is " + pdfId);
						}
						rs.close();
						st.close();
						if(pdfId == null || pdfId.equals(""))
						{
							writeToFile.write(pn + "\t" + man + "\t" + staticClass + "\t" + refUrl + "\t" + "Please Insert Offline");
							writeToFile.newLine();
							continue DD;
						}							
					}
					if (!staticClass.equalsIgnoreCase("")){
						pstmt.setString(1, staticClass);
						pstmt.setString(2, refUrl);
						pstmt.setString(3, pn);
						pstmt.setString(4, man);
						pstmt.setInt(5, id);
						int count = pstmt.executeUpdate();
						gen.generateRolesByPN(pn, man, id);
						if (count > 0)
							status = "Updated";
						else
							status = "Part/ClasID Not Found";
					}
					else{
						status = "Can't Update Static By Null";
					}
				} catch (Exception e){
					// TODO: handle exception
					e.printStackTrace();
					status = e.getMessage();
				}
				writeToFile.write(pn + "\t" + man + "\t" + staticClass + "\t" + refUrl + "\t" + status);
				writeToFile.newLine();
			}
			writeToFile.close();
			con.commit();
			pstmt.close();
			con.close();
		} catch (Exception e){
			e.printStackTrace();
			try{
				con.close();
			} catch (SQLException e1){
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return Url;
	}
	public  String deletePNSAction(String fileName, ArrayList<ArrayList<String>> txtDataList, int id) {
		String status = "";
		String Url = "";
		String query = "";
		Connection con = null;
		PreparedStatement pstmt = null;
		try{
			String pn = "", man = "";
			@SuppressWarnings("unused")
			String directory = Utils.createDirector(log );
			Url = log + "StaticInfoTools\\" + System.currentTimeMillis() + fileName + ".txt";
			BufferedWriter writeToFile = new BufferedWriter(new FileWriter(log+"Log.txt"));
			con = Utils.connectDatabase();
			writeToFile.write("Part Number\tSupplier Name\tStatus");
			writeToFile.newLine();
			query = "update cm.PART_CODE set STATIC_CLASS=null ,reason='Static Code Change' , STATIC_REF_URL=null where COM_ID=cm.GET_COM_ID(?,CM.GET_MAN_ID(?)) and CLAS_ID=? and MANUAL_FLAG =0";
			pstmt = con.prepareStatement(query);
			for(int row = 1; row < txtDataList.size(); row++){
				try{
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
				} catch (Exception e){
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
		} catch (Exception e){
			e.printStackTrace();
			try{
				con.close();
			} catch (SQLException e1){
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		return Url;
	}

	public  String updatePLAction(String fileName, ArrayList<ArrayList<String>> txtDataList, int id) {
		Statement st = null;
		ResultSet rs = null;
		String status = "";
		String Url = "";
		String query = "";
		Connection con = null;
		PreparedStatement pstmt = null;
		try{
			String pl = "", staticClass = "", refUrl = "";
			@SuppressWarnings("unused")
			String directory = Utils.createDirector(log);
			Url = log + "StaticInfoTools\\" + System.currentTimeMillis() + fileName + ".txt";
			BufferedWriter writeToFile = new BufferedWriter(new FileWriter(log+"Log.txt"));
			con = Utils.connectDatabase();
			writeToFile.write("Product Line\tStatic\tREF_URL\tStatus");
			writeToFile.newLine();
			query = "update cm.PART_CODE set STATIC_CLASS=? ,reason='Static Code Change', STATIC_REF_URL=? where PL_ID=get_pl_id(?) and CLAS_ID=? and MANUAL_FLAG =0";
			pstmt = con.prepareStatement(query);
			String pdfId="";
			DD:
			for(int row = 1; row < txtDataList.size(); row++){
				try{
					pl = txtDataList.get(row).get(0).trim().replaceAll("\"","");
					staticClass = txtDataList.get(row).get(1).trim().replaceAll("\"","");
					refUrl = txtDataList.get(row).get(2).trim();
					if(!refUrl.equals("") )
					{
						st = con.createStatement();
						rs = st.executeQuery("select importer.GET_PDF_ID_By_URL('" + refUrl + "') pdf from dual");
						while(rs.next())
						{
							pdfId = (rs.getString("pdf")==null)?null:rs.getString("pdf").toString();
							System.out.println("pdf url is " + refUrl);
						}
						if(pdfId == null || pdfId.equals(""))
						{
							writeToFile.write(pl + "\t" + staticClass + "\t" + refUrl + "\t" + "Please Insert Offline");
							writeToFile.newLine();
							continue DD;
						}							
					}
					if (!staticClass.equalsIgnoreCase("")){
						pstmt.setString(1, staticClass);
						pstmt.setString(2, refUrl);
						pstmt.setString(3, pl);
						pstmt.setInt(4, id);
						int count = pstmt.executeUpdate();
						gen.generateRolesByPL(pl, id);
						if (count > 0)
							status = "Updated";
						else
							status = "PL/ClasID Not Found";
					}
					else{
						status = "Can't Update Static By Null";
					}
				} catch (Exception e){
					// TODO: handle exception
					e.printStackTrace();
					status = e.getMessage();
				}
				writeToFile.write(pl + "\t" + staticClass + "\t" + refUrl + "\t" + status);
				writeToFile.newLine();
			}
			writeToFile.close();
			con.commit();
			pstmt.close();
			con.close();
		} catch (Exception e){
			e.printStackTrace();
			try{
				con.close();
			} catch (SQLException e1){
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		return Url;
	}

	public  String deletePLAction(String fileName, ArrayList<ArrayList<String>> txtDataList, int id) {
		String status = "";
		String Url = "";
		String query = "";
		Connection con = null;
		PreparedStatement pstmt = null;
		try{
			String pl = "";
			@SuppressWarnings("unused")
			String directory = Utils.createDirector(log );
			Url = log + "StaticInfoTools\\" + System.currentTimeMillis() + fileName + ".txt";
			BufferedWriter writeToFile = new BufferedWriter(new FileWriter(log+"Log.txt"));
			con = Utils.connectDatabase();
			writeToFile.write("Product Line\tStatus");
			writeToFile.newLine();
			query = "update cm.PART_CODE set STATIC_CLASS=null ,reason='Static Code Change', STATIC_REF_URL=null where PL_ID=get_pl_id(?) and CLAS_ID=? and MANUAL_FLAG =0";
			pstmt = con.prepareStatement(query);
			for(int row = 1; row < txtDataList.size(); row++){
				try{
					pl = txtDataList.get(row).get(0).trim().replaceAll("\"","");
					pstmt.setString(1, pl);
					pstmt.setInt(2, id);
					int count = pstmt.executeUpdate();
					gen.generateRolesByPL(pl,id);
					if (count > 0)
						status = "Deleted";
					else
						status = "PL/ClasID Not Found";
				} catch (Exception e){
					// TODO: handle exception
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
		} catch (Exception e){
			e.printStackTrace();
			try{
				con.close();
			} catch (SQLException e1){
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		return Url;
	}

	public String exportPNSAction(String fileName, ArrayList<ArrayList<String>> txtDataList, int id) {
		String Url = "";
		String query = "";
		ResultSet rs = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		try{
			String pn = "", man = "";
			@SuppressWarnings("unused")
			String directory = Utils.createDirector(log );
			Url = log + "StaticInfoTools\\" + System.currentTimeMillis() + fileName + ".txt";
			BufferedWriter writeToFile = new BufferedWriter(new FileWriter(log+"Log.txt"));
			con = Utils.connectDatabase();
			writeToFile.write("Product Line\tPart Number\tSupplier\tCode Name\tCode Version\tDYN_CLASS\tSTATIC_CLASS\tSUPPLIER_CLASS\tSE_CLASS\tREF\tREF_URL\tCONF_CLASS_LVL\tMANUAL_FLAG\tSUP_REF_URL\tSTATIC_REF_URL\tModify Date");
			writeToFile.newLine();
			query = "select get_pl_name(p.PL_ID), x.COM_PARTNUM, get_man_name(x.MAN_ID), c.CLAS_NAME, c.CLAS_VER, p.DYN_CLASS, p.STATIC_CLASS, p.SUPPLIER_CLASS, p.SE_CLASS, p.REF, p.REF_URL, p.CONF_CLASS_LVL, p.MANUAL_FLAG, p.SUP_REF_URL, p.STATIC_REF_URL, p.MODIFY_DATE from cm.PART_CODE p,cm.XLP_SE_COMPONENT x,importer.CLASSIFICATION_CODE c where p.COM_ID=cm.GET_COM_ID(?,CM.GET_MAN_ID(?)) and p.CLAS_ID=? and p.COM_ID=x.COM_ID and p.CLAS_ID=c.CLAS_ID";
			pstmt = con.prepareStatement(query);
			for(int row = 1; row < txtDataList.size(); row++){
				try{
					pn = txtDataList.get(row).get(0).trim().replaceAll("\"","");
					man = txtDataList.get(row).get(1).trim().replaceAll("\"","");
					pstmt.setString(1, pn);
					pstmt.setString(2, man);
					pstmt.setInt(3, id);
					rs = pstmt.executeQuery();
					while (rs.next()){
						writeToFile.write(((rs.getString(1) != null) ? rs.getString(1) : "") + "\t" + ((rs.getString(2) != null) ? rs.getString(2) : "") + "\t" + ((rs.getString(3) != null) ? rs.getString(3) : "") + "\t" + ((rs.getString(4) != null) ? rs.getString(4) : "") + "\t" + ((rs.getString(5) != null) ? rs.getString(5) : "") + "\t" + ((rs.getString(6) != null) ? rs.getString(6) : "") + "\t" + ((rs.getString(7) != null) ? rs.getString(7) : "") + "\t" + ((rs.getString(8) != null) ? rs.getString(8) : "") + "\t" + ((rs.getString(9) != null) ? rs.getString(9) : "") + "\t" + ((rs.getString(10) != null) ? rs.getString(10) : "") + "\t" + ((rs.getString(11) != null) ? rs.getString(11) : "") + "\t" + ((rs.getString(12) != null) ? rs.getString(12) : "") + "\t" + ((rs.getString(13) != null) ? rs.getString(13) : "") + "\t" + ((rs.getString(14) != null) ? rs.getString(14) : "") + "\t" + ((rs.getString(15) != null) ? rs.getString(15) : "") + "\t" + ((rs.getString(16) != null) ? rs.getString(16) : ""));
						writeToFile.newLine();
					}
				} catch (Exception e){
					// TODO: handle exception
					e.printStackTrace();
				}
				rs.close();
			}
			writeToFile.close();
			pstmt.close();
			con.close();
		} catch (Exception e){
			e.printStackTrace();
			try{
				con.close();
			} catch (SQLException e1){
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return Url;
	}
	/*
	 public static String exportPNSAction(String fileName, ArrayList<ArrayList<String>> txtDataList, int id) {
			String Url = "";
			String query = "";
			ResultSet rs = null;
			Connection con = null;
			PreparedStatement pstmt = null;
			try{
				String pn = "", man = "";
				@SuppressWarnings("unused")
				String directory = Utils.createDirector(log + "\\StaticInfoTools");
				Url = log + "StaticInfoTools\\" + System.currentTimeMillis() + fileName + ".txt";
				BufferedWriter writeToFile = new BufferedWriter(new FileWriter(Url));
				con = Utils.connectDatabase();
				writeToFile.write("Product Line\tPart Number\tSupplier\tCode Name\tCode Version\tDYN_CLASS\tSTATIC_CLASS\tSUPPLIER_CLASS\tSE_CLASS\tREF\tREF_URL\tCONF_CLASS_LVL\tMANUAL_FLAG\tSUP_REF_URL\tSTATIC_REF_URL");
				writeToFile.newLine();
				query = "select get_pl_name(p.PL_ID), x.COM_PARTNUM, get_man_name(x.MAN_ID), c.CLAS_NAME, c.CLAS_VER, p.DYN_CLASS, p.STATIC_CLASS, p.SUPPLIER_CLASS, p.SE_CLASS, p.REF, p.REF_URL, p.CONF_CLASS_LVL, p.MANUAL_FLAG, p.SUP_REF_URL, p.STATIC_REF_URL from " + sch + ".PART_CODE p,cm.XLP_SE_COMPONENT x," + sch + ".CLASSIFICATION_CODE c where p.COM_ID IN (SELECT GET_COM_ID_BY_PN_VEN(PART,MAN) FROM TABLE (CAST ( ? AS PART_MAN_TAB))) and p.CLAS_ID=? and p.COM_ID=x.COM_ID and p.CLAS_ID=c.CLAS_ID";
				pstmt = con.prepareStatement(query);
				PartMan partManRow;
				PartMan[] partManRows = new PartMan[txtDataList.size()];
				for(int row = 0; row < txtDataList.size(); row++){
					partManRow = new PartMan();
					partManRow.setPart(txtDataList.get(row).get(0).trim());
					partManRow.setMan(txtDataList.get(row).get(1).trim());
					partManRows[row] = partManRow;
				}
				ArrayDescriptor descriptor = ArrayDescriptor.createDescriptor("IMPORTER.PART_MAN_TAB", con);
				oracle.sql.ARRAY sqlRowArr = new oracle.sql.ARRAY(descriptor, con, partManRows);
				pstmt.setArray(1, sqlRowArr);
				pstmt.setInt(2, id);
				rs = pstmt.executeQuery();
				while (rs.next()){
					writeToFile.write(((rs.getString(1) != null) ? rs.getString(1) : "") + "\t" + ((rs.getString(2) != null) ? rs.getString(2) : "") + "\t" + ((rs.getString(3) != null) ? rs.getString(3) : "") + "\t" + ((rs.getString(4) != null) ? rs.getString(4) : "") + "\t" + ((rs.getString(5) != null) ? rs.getString(5) : "") + "\t" + ((rs.getString(6) != null) ? rs.getString(6) : "") + "\t" + ((rs.getString(7) != null) ? rs.getString(7) : "") + "\t" + ((rs.getString(8) != null) ? rs.getString(8) : "") + "\t" + ((rs.getString(9) != null) ? rs.getString(9) : "") + "\t" + ((rs.getString(10) != null) ? rs.getString(10) : "") + "\t" + ((rs.getString(11) != null) ? rs.getString(11) : "") + "\t" + ((rs.getString(12) != null) ? rs.getString(12) : "") + "\t" + ((rs.getString(13) != null) ? rs.getString(13) : "") + "\t" + ((rs.getString(14) != null) ? rs.getString(14) : "") + "\t" + ((rs.getString(15) != null) ? rs.getString(15) : ""));
					writeToFile.newLine();
				}
				writeToFile.close();
				rs.close();
				pstmt.close();
				con.close();
			} catch (Exception e){
				e.printStackTrace();
				try{
					con.close();
				} catch (SQLException e1){
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			return Url;
		}
	 */
	public  String exportPLAction(String fileName, ArrayList<ArrayList<String>> txtDataList, int id) {
		String Url = "";
		String query = "";
		ResultSet rs = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		try{
			String pl = "";
			@SuppressWarnings("unused")
			String directory = Utils.createDirector(log);
			Url = log + "StaticInfoTools\\" + System.currentTimeMillis() + fileName + ".txt";
			BufferedWriter writeToFile = new BufferedWriter(new FileWriter(log+"Log.txt"));
			con = Utils.connectDatabase();
			writeToFile.write("Product Line\tPart Number\tSupplier\tCode Name\tCode Version\tDYN_CLASS\tSTATIC_CLASS\tSUPPLIER_CLASS\tSE_CLASS\tREF\tREF_URL\tCONF_CLASS_LVL\tMANUAL_FLAG\tSUP_REF_URL\tSTATIC_REF_URL\tModify Date");
			writeToFile.newLine();
			query = "select get_pl_name(p.PL_ID), x.COM_PARTNUM, get_man_name(x.MAN_ID), c.CLAS_NAME, c.CLAS_VER, p.DYN_CLASS, p.STATIC_CLASS, p.SUPPLIER_CLASS, p.SE_CLASS, p.REF, p.REF_URL, p.CONF_CLASS_LVL, p.MANUAL_FLAG, p.SUP_REF_URL, p.STATIC_REF_URL, p.MODIFY_DATE from cm.PART_CODE p,cm.XLP_SE_COMPONENT x,importer.CLASSIFICATION_CODE c where p.PL_ID=get_pl_id(?) and p.CLAS_ID=? and p.COM_ID=x.COM_ID and p.CLAS_ID=c.CLAS_ID";
			pstmt = con.prepareStatement(query);
			for(int row = 1; row < txtDataList.size(); row++){
				try{
					pl = txtDataList.get(row).get(0).trim().replaceAll("\"","");
					pstmt.setString(1, pl);
					pstmt.setInt(2, id);
					rs = pstmt.executeQuery();
					while (rs.next()){
						writeToFile.write(((rs.getString(1) != null) ? rs.getString(1) : "") + "\t" + ((rs.getString(2) != null) ? rs.getString(2) : "") + "\t" + ((rs.getString(3) != null) ? rs.getString(3) : "") + "\t" + ((rs.getString(4) != null) ? rs.getString(4) : "") + "\t" + ((rs.getString(5) != null) ? rs.getString(5) : "") + "\t" + ((rs.getString(6) != null) ? rs.getString(6) : "") + "\t" + ((rs.getString(7) != null) ? rs.getString(7) : "") + "\t" + ((rs.getString(8) != null) ? rs.getString(8) : "") + "\t" + ((rs.getString(9) != null) ? rs.getString(9) : "") + "\t" + ((rs.getString(10) != null) ? rs.getString(10) : "") + "\t" + ((rs.getString(11) != null) ? rs.getString(11) : "") + "\t" + ((rs.getString(12) != null) ? rs.getString(12) : "") + "\t" + ((rs.getString(13) != null) ? rs.getString(13) : "") + "\t" + ((rs.getString(14) != null) ? rs.getString(14) : "") + "\t" + ((rs.getString(15) != null) ? rs.getString(15) : "") + "\t" + ((rs.getString(16) != null) ? rs.getString(16) : ""));
						writeToFile.newLine();
//						System.out.println(((rs.getString(1) != null) ? rs.getString(1) : "") + "\t" + ((rs.getString(2) != null) ? rs.getString(2) : "") + "\t" + ((rs.getString(3) != null) ? rs.getString(3) : "") + "\t" + ((rs.getString(4) != null) ? rs.getString(4) : "") + "\t" + ((rs.getString(5) != null) ? rs.getString(5) : "") + "\t" + ((rs.getString(6) != null) ? rs.getString(6) : "") + "\t" + ((rs.getString(7) != null) ? rs.getString(7) : "") + "\t" + ((rs.getString(8) != null) ? rs.getString(8) : "") + "\t" + ((rs.getString(9) != null) ? rs.getString(9) : "") + "\t" + ((rs.getString(10) != null) ? rs.getString(10) : "") + "\t" + ((rs.getString(11) != null) ? rs.getString(11) : "") + "\t" + ((rs.getString(12) != null) ? rs.getString(12) : "") + "\t" + ((rs.getString(13) != null) ? rs.getString(13) : "") + "\t" + ((rs.getString(14) != null) ? rs.getString(14) : "") + "\t" + ((rs.getString(15) != null) ? rs.getString(15) : "") + "\t" + ((rs.getString(16) != null) ? rs.getString(16) : ""));
					}
				} catch (Exception e){
					// TODO: handle exception
					e.printStackTrace();
				}
				rs.close();
			}
			writeToFile.close();
			pstmt.close();
			con.close();
		} catch (Exception e){
			e.printStackTrace();
			try{
				con.close();
			} catch (SQLException e1){
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return Url;
	}

}
