package com.se.tools;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.se.classcode.Utils;


// =======================================================================================
// ********************************* Sherif ELSbaey **************************************
// ======================================EIVA=============================================

/**
 * @author sherif_elsbaey
 */
public class TruthToolActions {

	
	static String log = Utils.getProperty("log.path");
	

	public String updateTruthTableAction(ArrayList<String> txtDataList, String pK) {
		String status = "";
		String query = "";
		Connection con = null;
		PreparedStatement pstmt = null;	
		try{
			query = "INSERT INTO importer.CLAS_TRUTH (DYN_CLASS,STATIC_CLASS,SUPPLIER_CLASS,SE_CLAS,CLAS_REF,CLAS_CONF_LVL,FLAG) VALUES(?,?,?,?,?,?,?)";
			con = Utils.connectDatabase();
			pstmt = con.prepareStatement(query);
			System.out.println(query);
			try{
				
				pstmt.setString(1, txtDataList.get(0).trim());//DYN_CLASS
				System.out.println(txtDataList.get(0).trim());
				pstmt.setString(2, txtDataList.get(1).trim());//STATIC_CLASS
				System.out.println(txtDataList.get(1).trim());
				pstmt.setString(3, txtDataList.get(2).trim());//SUPPLIER_CLASS
				System.out.println(txtDataList.get(2).trim());
				pstmt.setString(4, txtDataList.get(3).trim());//SE_CLAS
				System.out.println(txtDataList.get(3).trim());
				pstmt.setString(5, txtDataList.get(4).trim());//CLAS_REF
				System.out.println(txtDataList.get(4).trim());
				pstmt.setString(6, txtDataList.get(5).trim());//CLAS_CONF_LVL
				System.out.println(txtDataList.get(5).trim());
				pstmt.setString(7, txtDataList.get(6).trim());//FLAG
				System.out.println(txtDataList.get(6).trim());
				
				int count = pstmt.executeUpdate();
				if (count > 0){
					status = "Inserted";
					if (!pK.equalsIgnoreCase(""))
						status = "Updated";
					}
				else{
					status = "Row Not Inserted";
					if (!pK.equalsIgnoreCase(""))
						status = "Row Not Found To Update";
				}
			} catch (Exception e){
				// TODO: handle exception
				e.printStackTrace();
				status = e.getMessage();
			}
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
		return status;
	}

	public  String exportTruthDataAction(String fileName) {
		String Url = "";
		String query = "";
		ResultSet rs = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		try{
			@SuppressWarnings("unused")
			String directory = Utils.createDirector(log + "\\TruthAlgorithmsTools");
			Url = log + "TruthAlgorithmsTools\\" + System.currentTimeMillis() + fileName + ".txt";
			BufferedWriter writeToFile = new BufferedWriter(new FileWriter(Url));
			con = Utils.connectDatabase();
			writeToFile.write("DYN_CLASS\tSTATIC_CLASS\tSUPPLIER_CLASS\tSE_CLAS\tCLAS_REF\tCLAS_CONF_LVL\tFLAG");
			writeToFile.newLine();
			query = "select ct.DYN_CLASS,ct.STATIC_CLASS,ct.SUPPLIER_CLASS,ct.SE_CLAS,ct.CLAS_REF,ct.CLAS_CONF_LVL,ct.FLAG from importer.CLAS_TRUTH ct";
			pstmt = con.prepareStatement(query);
			try{
				rs = pstmt.executeQuery();
				while (rs.next()){
					writeToFile.write(((rs.getString(1) != null) ? rs.getString(1) : "") + "\t" + ((rs.getString(2) != null) ? rs.getString(2) : "") + "\t" + ((rs.getString(3) != null) ? rs.getString(3) : "") + "\t" + ((rs.getString(4) != null) ? rs.getString(4) : "") + "\t" + ((rs.getString(5) != null) ? rs.getString(5) : "") + "\t" + ((rs.getString(6) != null) ? rs.getString(6) : "") + "\t" + ((rs.getString(7) != null) ? rs.getString(7) : ""));
					writeToFile.newLine();
				}
			} catch (Exception e){
				// TODO: handle exception
				e.printStackTrace();
			}
			rs.close();

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
	public String importTruthTableAction(ArrayList<ArrayList<String>> txtDataList) {
		String status = "";
		String query = "";
		Connection con = null;
		PreparedStatement pstmt = null;	
		Statement st=null;
		try{
			String query2="truncate table importer.CLAS_TRUTH";
			System.out.print("table truncated");
			
			query = "INSERT INTO importer.CLAS_TRUTH (DYN_CLASS,STATIC_CLASS,SUPPLIER_CLASS,SE_CLAS,CLAS_REF,CLAS_CONF_LVL,FLAG) VALUES(?,?,?,?,?,?,?)";
			con = Utils.connectDatabase();
			st=con.createStatement();
			st.execute(query2);
			st.close();
			pstmt = con.prepareStatement(query);
			System.out.println(query);
			for(int i=0;i<txtDataList.size();i++){
			try{
				
				pstmt.setString(1, txtDataList.get(i).get(0).trim());//DYN_CLASS
				System.out.println(txtDataList.get(i).get(0).trim());
				pstmt.setString(2, txtDataList.get(i).get(1).trim());//STATIC_CLASS
				System.out.println(txtDataList.get(i).get(1).trim());
				pstmt.setString(3, txtDataList.get(i).get(2).trim());//SUPPLIER_CLASS
				System.out.println(txtDataList.get(i).get(2).trim());
				pstmt.setString(4, txtDataList.get(i).get(3).trim());//SE_CLAS
				System.out.println(txtDataList.get(i).get(3).trim());
				pstmt.setString(5, txtDataList.get(i).get(4).trim());//CLAS_REF
				System.out.println(txtDataList.get(i).get(4).trim());
				pstmt.setString(6, txtDataList.get(i).get(5).trim());//CLAS_CONF_LVL
				System.out.println(txtDataList.get(i).get(5).trim());
				pstmt.setString(7, txtDataList.get(i).get(6).trim());//FLAG
				System.out.println(txtDataList.get(i).get(6).trim());
				
				int count = pstmt.executeUpdate();
				if (count > 0){
					status = "Inserted";
					
					}
				else{
					status = "Row Not Inserted";					
				}
			} catch (Exception e){
				// TODO: handle exception
				e.printStackTrace();
				status = e.getMessage();
			}
		}
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
		return status;
	}
}
