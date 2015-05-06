package com.se.tools;

import java.util.ArrayList;

import javax.faces.bean.ManagedBean;

@ManagedBean
public class ErrorTool extends ToolBean {

	ErrorToolActions errorActions = new ErrorToolActions();

	public ErrorTool() {
		this.function = "Update by PN & Supplier";
		this.functions = new ArrayList<String>();
		this.functions.add("Import All");
		this.functions.add("Import by PN & Supplier");
		this.functions.add("Import by PL");
		this.functions.add("Import by Supplier");
		this.functions.add("Export All");
		this.functions.add("Export by PN & Supplier");
		this.functions.add("Export by PL");
		this.functions.add("Export by Supplier");
		this.functions.add("Delete All");
		this.functions.add("Add Part to Daily");

	}

	public String performAction(ArrayList<ArrayList<String>> list,
			String function,int classId,String fileName) {
		System.out.println("Error action here");
		String currentHeader = "";
		boolean validHeader = false;
		if (function.equals("Import All")) {
			errorActions.importAllErrorCode();
		} else if (function.equals("Import by PN & Supplier")) {
			currentHeader = "Part Number,Supplier";
			validHeader = checkHeader(currentHeader, list.get(0));
			if (validHeader) {
				errorActions.importByPNSAction("", list, classId);
			} else {
				return "the header must be " + currentHeader;
			}
		} else if (function.equals("Import by PL")) {
			currentHeader = "Product Line";
			validHeader = checkHeader(currentHeader, list.get(0));
			if (validHeader) {
				errorActions.importByPLAction("", list, classId);
			} else {
				return "the header must be " + currentHeader;
			}
		} else if (function.equals("Import by Supplier")) {
			currentHeader = "Product Line";
			validHeader = checkHeader(currentHeader, list.get(0));
			if (validHeader) {
				errorActions.importBySuppAction("", list, classId);
			} else {
				return "the header must be " + currentHeader;
			}
		} else if (function.equals("Export All")) {
			errorActions.exportAllErrorCode(fileName);
		} else if (function.equals("Export by PN & Supplier")) {
			currentHeader = "Product Line";
			validHeader = checkHeader(currentHeader, list.get(0));
			if (validHeader) {
				errorActions.exportPNSAction("", list, classId);
			} else {
				return "the header must be " + currentHeader;
			}
		} else if (function.equals("Export by PL")) {
			currentHeader = "Product Line";
			validHeader = checkHeader(currentHeader, list.get(0));
			if (validHeader) {
				errorActions.exportPLAction("", list, classId);
			} else {
				return "the header must be " + currentHeader;
			}
		} else if (function.equals("Export by Supplier")) {
			currentHeader = "Product Line";
			validHeader = checkHeader(currentHeader, list.get(0));
			if (validHeader) {
				errorActions.exportSuppAction("", list, classId);
			} else {
				return "the header must be " + currentHeader;
			}
		} else if (function.equals("Delete All")) {
			errorActions.deleteAllErrorCode();
		} else if (function.equals("Add Part to Daily")) {
			currentHeader = "Product Line";
			validHeader = checkHeader(currentHeader, list.get(0));
			if (validHeader) {
				errorActions.addToDaily("", list);
			} else {
				return "the header must be " + currentHeader;
			}
		}
//		MainWindowBean.downloadController();
		return "The Process Done";
	}

}
