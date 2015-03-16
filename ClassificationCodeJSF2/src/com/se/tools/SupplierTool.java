package com.se.tools;

import java.util.ArrayList;

import javax.faces.bean.ManagedBean;

@ManagedBean
public class SupplierTool extends ToolBean {

	SupplierToolActions supplier = new SupplierToolActions();

	public SupplierTool() {
		this.function = "Update by PN & Supplier";
		this.functions = new ArrayList<String>();
		this.functions.add("Update by PN & Supplier");
		this.functions.add("Export by PN & Supplier");
		this.functions.add("Delete by PN & Supplier");
		this.functions.add("Update by PL & Supplier");
		this.functions.add("Export by PL & Supplier");
		this.functions.add("Delete by PL & Supplier");
		this.functions.add("Update by Supplier");
		this.functions.add("Export by Supplier");
		this.functions.add("Delete by Supplier");
	}

	public String performAction(ArrayList<ArrayList<String>> list,
			String function,int classId) {
		System.out.println("Supplier action here");
		String currentHeader = "";
		boolean validHeader = false;
		if (function.equals("Update by PN & Supplier")) {
			currentHeader = "Part Number\tSupplier Name\tClass Name\tClass Version\tSupplier\tREF_URL";
			validHeader = checkHeader(currentHeader, list.get(0));
			if (validHeader) {
				supplier.updatePNSAction("filename", list);
			} else {
				return "the header must be " + currentHeader;
			}
		} else if (function.equals("Export by PN & Supplier")) {
			currentHeader = "Part Number\tSupplier Name";
			validHeader = checkHeader(currentHeader, list.get(0));
			if (validHeader) {
				supplier.exportPNSAction("", list, classId);
			} else {
				return "the header must be " + currentHeader;
			}

		} else if (function.equals("Delete by PN & Supplier")) {
			currentHeader = "Part Number\tSupplier Name";
			validHeader = checkHeader(currentHeader, list.get(0));
			if (validHeader) {
				supplier.deletePNSAction("", list, classId);
			} else {
				return "the header must be " + currentHeader;
			}

		} else if (function.equals("Update by PL & Supplier")) {
			supplier.updatePLSuppAction("filename", list, classId);
		} else if (function.equals("Export by PL & Supplier")) {
			currentHeader = "Product Line\tSupplier Name";
			validHeader = checkHeader(currentHeader, list.get(0));
			if (validHeader) {
				supplier.exportPLSAction("", list, classId);
			} else {
				return "the header must be " + currentHeader;
			}

		} else if (function.equals("Delete by PL & Supplier")) {
			currentHeader = "Product Line\tSupplier Name";
			validHeader = checkHeader(currentHeader, list.get(0));
			if (validHeader) {
				supplier.deletePLSAction("", list, classId);
			} else {
				return "the header must be " + currentHeader;
			}

		} else if (function.equals("Update by Supplier")) {
			supplier.updateSuppAction("", list, classId);
		} else if (function.equals("Export by Supplier")) {
			currentHeader = "Supplier Name";
			validHeader = checkHeader(currentHeader, list.get(0));
			if (validHeader) {
				supplier.exportSuppAction("", list, classId);
			} else {
				return "the header must be " + currentHeader;
			}

		} else if (function.equals("Delete by Supplier")) {
			currentHeader = "Supplier Name";
			validHeader = checkHeader(currentHeader, list.get(0));
			if (validHeader) {
				supplier.deleteSuppAction("", list, classId);
			} else {
				return "the header must be " + currentHeader;
			}

		}
//		MainWindowBean.downloadController();
		return "The Process Done";
	}
}
