package com.se.tools;

import java.util.ArrayList;

import javax.faces.bean.ManagedBean;

@ManagedBean
public class ExceptionTool extends ToolBean {

	ExceptionToolActions exceptionActions = new ExceptionToolActions();

	public ExceptionTool() {
		this.function = "Update by PN & Supplier";
		this.functions = new ArrayList<String>();
		this.functions.add("Update by PN & Supplier");
		this.functions.add("Export by PN & Supplier");
		this.functions.add("Delete by PN & Supplier");
		this.functions.add("Export by Supplier");
		this.functions.add("Export by PL");
		this.functions.add("Export All Exception Parts");
	}

	public String performAction(ArrayList<ArrayList<String>> list,
			String function, int classId) {
		System.out.println("Exception action here");
		String currentHeader = "";
		boolean validHeader = false;
		if (function.equals("Update by PN & Supplier")) {
			currentHeader = "Part Number\tSupplier Name\tClass Name\tClass Version\tSupplier\tREF_URL";
			validHeader = checkHeader(currentHeader, list.get(0));
			if (validHeader) {
				exceptionActions.updatePNSAction("filename", list, classId);
			} else {
				return "the header must be " + currentHeader;
			}
		} else if (function.equals("Export by PN & Supplier")) {
			currentHeader = "Part Number\tSupplier Name";
			validHeader = checkHeader(currentHeader, list.get(0));
			if (validHeader) {
				exceptionActions.exportPNSAction("", list, classId);
			} else {
				return "the header must be " + currentHeader;
			}

		} else if (function.equals("Delete by PN & Supplier")) {
			currentHeader = "Part Number\tSupplier Name";
			validHeader = checkHeader(currentHeader, list.get(0));
			if (validHeader) {
				exceptionActions.deletePNSAction("", list, classId);
			} else {
				return "the header must be " + currentHeader;
			}

		} else if (function.equals("Export All Exception Parts")) {
			exceptionActions.exportAllErrorParts();
		} else if (function.equals("Export by PL")) {
			currentHeader = "Product Line\tSupplier Name";
			validHeader = checkHeader(currentHeader, list.get(0));
			if (validHeader) {
				exceptionActions.exportPLAction("", list, classId);
			} else {
				return "the header must be " + currentHeader;
			}

		} else if (function.equals("Export by PL")) {
			currentHeader = "Product Line\tSupplier Name";
			validHeader = checkHeader(currentHeader, list.get(0));
			if (validHeader) {
				exceptionActions.exportPLAction("", list, classId);
			} else {
				return "the header must be " + currentHeader;
			}
		} else if (function.equals("Export by Supplier")) {
			currentHeader = "Product Line\tSupplier Name";
			validHeader = checkHeader(currentHeader, list.get(0));
			if (validHeader) {
				exceptionActions.exportSuppAction("", list, classId);
			} else {
				return "the header must be " + currentHeader;
			}
		}
		MainWindowBean.downloadController();
		return "The Process Done";
	}
}
