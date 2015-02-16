package com.se.tools;

import java.util.ArrayList;

import javax.faces.bean.ManagedBean;

@ManagedBean
public class StaticTool extends ToolBean {

	StaticToolActions staticActions = new StaticToolActions();

	public StaticTool() {
		this.function = "Update by PN & Supplier";
		this.functions = new ArrayList<String>();
		this.functions.add("Update by PN & Supplier");
		this.functions.add("Export by PN & Supplier");
		this.functions.add("Delete by PN & Supplier");
		this.functions.add("Update by PL");
		this.functions.add("Export by PL");
		this.functions.add("Delete by PL");
	}

	public String performAction(ArrayList<ArrayList<String>> list,String function,int classId) {
		System.out.println("Supplier action here");
		String currentHeader = "";
		boolean validHeader = false;
		if (function.equals("Update by PN & Supplier")) {
			currentHeader = "Part Number\tSupplier Name\tClass Name\tClass Version\tSupplier\tREF_URL";
			validHeader = checkHeader(currentHeader, list.get(0));
			if (validHeader) {
				staticActions.updatePNSAction("filename", list, classId);
			} else {
				return "the header must be " + currentHeader;
			}
		} else if (function.equals("Export by PN & Supplier")) {
			currentHeader = "Part Number\tSupplier Name";
			validHeader = checkHeader(currentHeader, list.get(0));
			if (validHeader) {
				staticActions.exportPNSAction("", list, classId);
			} else {
				return "the header must be " + currentHeader;
			}

		} else if (function.equals("Delete by PN & Supplier")) {
			currentHeader = "Part Number\tSupplier Name";
			validHeader = checkHeader(currentHeader, list.get(0));
			if (validHeader) {
				staticActions.deletePNSAction("", list, classId);
			} else {
				return "the header must be " + currentHeader;
			}

		} else if (function.equals("Update by PL")) {
			staticActions.updatePLAction("filename", list, classId);
		} else if (function.equals("Export by PL")) {
			currentHeader = "Product Line\tSupplier Name";
			validHeader = checkHeader(currentHeader, list.get(0));
			if (validHeader) {
				staticActions.exportPLAction("", list, classId);
			} else {
				return "the header must be " + currentHeader;
			}

		} else if (function.equals("Delete by PL")) {
			currentHeader = "Product Line\tSupplier Name";
			validHeader = checkHeader(currentHeader, list.get(0));
			if (validHeader) {
				staticActions.deletePLAction("", list, classId);
			} else {
				return "the header must be " + currentHeader;
			}

		}
		MainWindowBean.downloadController();
		return "The Process Done";
	}
}
