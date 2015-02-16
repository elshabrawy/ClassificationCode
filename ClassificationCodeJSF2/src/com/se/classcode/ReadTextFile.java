package com.se.classcode;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;





// =======================================================================================
// ********************************* Sherif ELSbaey **************************************
// ======================================EIVA=============================================

/**
 * @author sherif_elsbaey
 */
public class ReadTextFile {
     
//	 public static void uploadFileProccess(HttpServletRequest request, HttpServletResponse response, String header) {
//		
//		 try{
//			long start = System.currentTimeMillis();
//			if (!ServletFileUpload.isMultipartContent(request)){
//				return;
//			}
//			String chkHeader = "true";
//			BufferedReader reader = null;
//			List<List<String>> txtDataList = new ArrayList<List<String>>();
//			FileItemFactory factory = new DiskFileItemFactory();
//			ServletFileUpload upload = new ServletFileUpload(factory);
//			List<?> items = null;
//			try{
//				items = upload.parseRequest(request);
//			} catch (Exception e){
//				e.printStackTrace();
//				return;
//			}
//			for(Iterator<?> i = items.iterator(); i.hasNext();){
//				FileItem item = (FileItem) i.next();
//				if (item.isFormField())
//					continue;
//				try{
//					reader = new BufferedReader(new InputStreamReader(item.getInputStream()));
//					String firstLine = reader.readLine();
//					if (firstLine != null && !firstLine.trim().equalsIgnoreCase("") && firstLine.equalsIgnoreCase(header)){
//						int headerLength = StringUtils.split(firstLine, "\t").length;
//						txtDataList = readTextFile(reader, headerLength);
//						
//					}
//					else{
//						chkHeader = "false";
//					}
//				} catch (Exception e){
//					e.printStackTrace();
//				}
//			}	
//
//			
//			
//			request.getSession().setAttribute("chkHeader", chkHeader);
//			request.getSession().setAttribute("txtDataList", txtDataList);		
//
//		} catch (Exception e){
//			// TODO: handle exception
//			e.printStackTrace();
//		}
//	}
//	 public static void uploadFileProccess2(HttpServletRequest request, HttpServletResponse response, String header) {
//		 try{
//				long start = System.currentTimeMillis();
//				if (!ServletFileUpload.isMultipartContent(request)){
//					return;
//				}
//				String chkHeader = "true";
//				BufferedReader reader = null;
//				List<List<String>> txtDataList = new ArrayList<List<String>>();
//				FileItemFactory factory = new DiskFileItemFactory();
//				ServletFileUpload upload = new ServletFileUpload(factory);
//				List<?> items = null;
//				try{
//					items = upload.parseRequest(request);
//				} catch (Exception e){
//					e.printStackTrace();
//					return;
//				}
//				for(Iterator<?> i = items.iterator(); i.hasNext();){
//					FileItem item = (FileItem) i.next();
//					if (item.isFormField())
//						continue;
//					try{
//						reader = new BufferedReader(new InputStreamReader(item.getInputStream()));
//						String firstLine = reader.readLine();
//						if (firstLine != null && !firstLine.trim().equalsIgnoreCase("") && firstLine.equalsIgnoreCase(header)){
//							int headerLength = StringUtils.split(firstLine, "\t").length;
//							txtDataList = readTextFile(reader, headerLength);
//							
//						}
//						else{
//							chkHeader = "false";
//						}
//					} catch (Exception e){
//						e.printStackTrace();
//					}
//				}	
//
//				ArrayList<String>list=read.Dparts;
//				for(int i=0;i<list.size();i++){
//					String s=list.get(i);
//					for(int j=0;j<txtDataList.size();j++){
//						if(txtDataList.get(j).get(0).equals(s)){
//							txtDataList.remove(txtDataList.get(j));
//						}						
//					}
//				}
//				
//				request.getSession().setAttribute("chkHeader", chkHeader);
//				request.getSession().setAttribute("txtDataList", txtDataList);		
//
//			} catch (Exception e){
//				// TODO: handle exception
//				e.printStackTrace();
//			}
//	 }

	private static List<List<String>> readTextFile(BufferedReader reader, int headerLength) {
		String txtline = null;
//		BufferedReader reader=new BufferedReader(new FileReader(new File("")));
		String[] lineData = null;
		List<List<String>> txtDataList = new ArrayList<List<String>>();
		try{
			txtline = reader.readLine();
			while (txtline != null){
				lineData = txtline.split( "\t");
				if (lineData.length == 0){
					txtline = reader.readLine();
					continue;
				}
				List<String> txtLineDataList = new ArrayList<String>();
				for(int i = 0; i < headerLength; i++){
					try{
						txtLineDataList.add(lineData[i]);
					} catch (ArrayIndexOutOfBoundsException e){
						// TODO: handle exception
						txtLineDataList.add("");
					}
				}
				txtDataList.add(txtLineDataList);
				txtline = reader.readLine();
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		return txtDataList;
	}
}
