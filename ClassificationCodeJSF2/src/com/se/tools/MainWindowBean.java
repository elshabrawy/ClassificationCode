package com.se.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.hibernate.Session;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import com.se.classcode.Utils;

import Util.DataBaseConnection;

@ManagedBean
@SessionScoped
public class MainWindowBean {

	private String codeName, CodeVer;
	boolean showUpload = false, showClassId = false;
	HashMap<String, ArrayList<String[]>> codesMap = new HashMap<String, ArrayList<String[]>>();
	String currentEnabl = "";
	private ArrayList<String> codeNames, codeVers;
	private String toolName = "Supplier Tool";
	private ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
	private String function;
	private ArrayList<String> functions;
	ToolBean tool = null;
	private UploadedFile file;
	private static StreamedContent outputFile;

	private File logFile;

	public MainWindowBean() {
		codeNames = new ArrayList<String>();
		codeNames.add("Code Name");
		codeVers = new ArrayList<String>();
		codeVers.add("Code Version");
		getCodeNameAndVersion();

	}

	public void performAction(ActionEvent actionEvent) {
		int classId = getClassId(this.getCodeName(), this.getCodeVer());
		System.out.println("action here");
		File logFile = new File("d:\\FilesTemp\\ClassificationTools\\Log.txt");
		logFile.delete();
		readFile();
		String outputMessage = tool.performAction(this.getList(),
				this.getFunction(), classId);

		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "",
				outputMessage);

		RequestContext.getCurrentInstance().showMessageInDialog(message);
	}

	private int getClassId(String codeName, String codeVer) {

		Statement st = null;
		ResultSet rs = null;

		String query = "";
		Connection con = null;
		int classId = 0;
		try {
			con = Utils.connectDatabase();

			query = "select clas_id from importer.CLASSIFICATION_CODE where clas_name='"
					+ codeName + "' and clas_ver= '" + codeVer + "'";
			st = con.createStatement();
			rs = st.executeQuery(query);
			while (rs.next()) {
				classId = rs.getInt("clas_id");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
				st.close();
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return classId;
	}

	public void codeVersion(ActionEvent event) {

		this.setToolName("Code Version");

	}
	public void dynamicTool(ActionEvent event) {

		this.setToolName("Dynamic Tool");

	}
	public void supplierTool(ActionEvent event) {

		this.setToolName("Supplier Tool");

	}
	public void staticTool(ActionEvent event) {

		this.setToolName("Static Tool");

	}
	public void errorTool(ActionEvent event) {

		this.setToolName("Error Tool");

	}
	public void exceptionTool(ActionEvent event) {

		this.setToolName("Exception Tool");

	}
	public void truthTool(ActionEvent event) {

		this.setToolName("Truth Tool");

	}
	public void statisticsTool(ActionEvent event) {

		this.setToolName("Statistics Tool");

	}
	

	public void getCodeNameAndVersion() {
		Session session = null;
		List<Object[]> rs = null;
		List<Object> codesRs = null;
		String codes = "";
		String query = "";
		ArrayList<String> rowlist = null;
		try {
			session = DataBaseConnection.getSession();
			codes = "select distinct clas_name from importer.CLASSIFICATION_CODE where enable=1";
			codesRs = session.createSQLQuery(codes).list();
			String code = "";
			for (Object codeN : codesRs) {
				code = (String) codeN;
				codeNames.add(code);

				query = "select  CLAS_VER,enable FROM importer.CLASSIFICATION_CODE where enable =1 and CLAS_NAME= '"
						+ code + "'";
				rs = session.createSQLQuery(query).list();
				BigDecimal enable = null;
				String arr[] = null;
				ArrayList<String[]> list = new ArrayList<String[]>();
				;
				for (Object[] row : rs) {
					arr = new String[2];
					arr[0] = (String) row[0];
					enable = (BigDecimal) row[1];
					arr[1] = enable.toString();
					list.add(arr);
				}
				codesMap.put(code, list);
			}
			session.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getCodeName() {
		return codeName;
	}

	public void setCodeName(String codeName) {
		codeVers = new ArrayList<String>();
		codeVers.add("Code Version");
		ArrayList<String[]> row = codesMap.get(codeName);
		if (row != null) {
			for (int i = 0; i < row.size(); i++) {
				codeVers.add(row.get(i)[0]);
			}
		}
		this.codeName = codeName;

	}

	public String getCodeVer() {
		return CodeVer;
	}

	public void setCodeVer(String codeVer) {
		ArrayList<String[]> row = codesMap.get(this.getCodeName());
		if (row != null) {
			for (int i = 0; i < row.size(); i++) {
				if (row.get(i)[0].equals(this.getCodeVer())) {
					currentEnabl = row.get(i)[1];
					break;
				}
			}
		}
		CodeVer = codeVer;
	}

	public ArrayList<String> getCodeNames() {
		return codeNames;
	}

	public void setCodeNames(ArrayList<String> codeNames) {
		this.codeNames = codeNames;
	}

	public ArrayList<String> getCodeVers() {
		return codeVers;
	}

	public void setCodeVers(ArrayList<String> codeVers) {
		this.codeVers = codeVers;
	}

	public void handleFileUpload(FileUploadEvent event) {
		this.setFile(event.getFile());
	}

	public void readFile() {
		this.list = new ArrayList<ArrayList<String>>();
		ArrayList<String> line = null;
		System.out.println("start upload");
		String name = this.getFile().getFileName();
		System.out.println("name sis " + name);
		System.out.println(this.getFunction());
		UploadedFile uploadFile = this.getFile();
		try {
			BufferedReader r = new BufferedReader(new InputStreamReader(
					uploadFile.getInputstream()));
			String s = "";
			int index = 0;
			String[] row = null;
			while ((s = r.readLine()) != null) {
				if (index == 0) {
					index = s.split("\t").length;
				}
				row = s.split("\t");
				line = new ArrayList<String>();
				for (int i = 0; i < row.length; i++) {
					line.add(row[i]);
				}
				for (int j = row.length - 1; j < index - 1; j++) {
					line.add("");
				}
				this.list.add(line);
				System.out.println("" + s);
			}
			setList(this.list);
			FacesContext.getCurrentInstance().getExternalContext()
					.getSessionMap().put("data", this.list);
		} catch (IOException e) {
		}
	}

	public void setList(ArrayList<ArrayList<String>> list) {
		this.list = list;
	}

	public ArrayList<ArrayList<String>> getList() {
		return this.list;
	}

	public String getToolName() {
		return toolName;
	}

	public void setToolName(String toolName) {

		if (toolName.equals("Supplier Tool")) {
			tool = new SupplierTool();
		} else if (toolName.equals("Dynamic Tool")) {
			tool = new DynamicTool();
		} else if (toolName.equals("Error Tool")) {
			tool = new ErrorTool();
		} else if (toolName.equals("Static Tool")) {
			tool = new StaticTool();
		} else if (toolName.equals("Exception Tool")) {
			tool = new ExceptionTool();
		}
		this.setFunction(tool.getFunction());
		this.setFunctions(tool.getFunctions());
		this.toolName = toolName;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		if (this.getToolName().equals("Supplier Tool")) {
			if (function.equals("Update by PN & Supplier")) {
				this.setShowClassId(true);

			} else {
				this.setShowClassId(false);
			}
		} else if (this.getToolName().equals("Dynamic Tool")
				|| this.getToolName().equals("Static Tool")) {
			this.setShowClassId(false);
		} else if (this.getToolName().equals("Error Tool")) {
			if (function.equals("Add Part to Daily")) {
				this.setShowClassId(true);
			} else if (function.equals("Import All")
					|| function.equals("Export All")
					|| function.equals("Delete All")) {
				this.setShowClassId(true);
				this.setShowUpload(true);
			} else {
				this.setShowClassId(false);
			}
		} else {
			this.setShowClassId(false);
		}
		if (tool != null) {
			tool.setFunction(function);
		}
		this.function = function;
	}

	public ArrayList<String> getFunctions() {
		return functions;
	}

	public void setFunctions(ArrayList<String> functions) {
		this.functions = functions;
	}

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}

	public boolean isShowUpload() {
		return showUpload;
	}

	public void setShowUpload(boolean showUpload) {
		this.showUpload = showUpload;
	}

	public boolean isShowClassId() {
		return showClassId;
	}

	public void setShowClassId(boolean showClassId) {
		this.showClassId = showClassId;
	}

	public static void downloadController() {
		System.out.println("dowloand start");
		InputStream stream;
		try {
			stream = new FileInputStream(new File("D:\\FilesTemp\\ClassificationTools\\Log.txt"));
			 outputFile = new DefaultStreamedContent(stream,"text/plain", "LogFile.txt");
//			setOutputFile(outputFile2);
			System.out.println("done");

//			FacesContext.getCurrentInstance().responseComplete();
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}

	}

	public StreamedContent getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(StreamedContent outputFile) {
		this.outputFile = outputFile;
	}

	public File getLogFile() {
		return logFile;
	}

	public void setLogFile(File logFile) {
		this.logFile = logFile;
	}
}