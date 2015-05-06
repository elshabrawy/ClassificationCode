package com.se.tools;

import java.util.ArrayList;

import javax.faces.bean.ManagedBean;

import com.se.classcode.Utils;

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
		this.functions.add("Import Lookup Values");
		this.functions.add("Export Lookup Values");
		this.functions.add("Delete Lookup Values");
	}

	public String performAction(ArrayList<ArrayList<String>> list,
			String function, int classId,String fileName) {
		
		System.out.println("Supplier action here");
		String currentHeader = "";
		boolean validHeader = false;
		if (function.equals("Update by PN & Supplier")) {
			currentHeader = "Part Number,Supplier Name,Class Name,Class Version,Supplier,REF_URL";
			validHeader = checkHeader(currentHeader, list.get(0));
			if (validHeader) {
				supplier.updatePNSAction(fileName, list);
			} else {
				return "the header must be " + currentHeader;
			}
		} else if (function.equals("Export by PN & Supplier")) {
			currentHeader = "Part Number,Supplier Name";
			validHeader = checkHeader(currentHeader, list.get(0));
			if (validHeader) {
				supplier.exportPNSAction(fileName, list, classId);
			} else {
				return "the header must be " + currentHeader;
			}

		} else if (function.equals("Delete by PN & Supplier")) {
			currentHeader = "Part Number,Supplier Name";
			validHeader = checkHeader(currentHeader, list.get(0));
			if (validHeader) {
				supplier.deletePNSAction(fileName, list, classId);
			} else {
				return "the header must be " + currentHeader;
			}

		} else if (function.equals("Update by PL & Supplier")) {
			currentHeader = "Product Line,Supplier Name,Supplier,REF_URL";
			validHeader = checkHeader(currentHeader, list.get(0));
			if (validHeader) {
			supplier.updatePLSuppAction(fileName, list, classId);
			} else {
				return "the header must be " + currentHeader;
			}
		} else if (function.equals("Export by PL & Supplier")) {
			currentHeader = "Product Line,Supplier Name";
			validHeader = checkHeader(currentHeader, list.get(0));
			if (validHeader) {
				supplier.exportPLSAction(fileName, list, classId);
			} else {
				return "the header must be " + currentHeader;
			}

		} else if (function.equals("Delete by PL & Supplier")) {
			currentHeader = "Product Line,Supplier Name";
			validHeader = checkHeader(currentHeader, list.get(0));
			if (validHeader) {
				supplier.deletePLSAction(fileName, list, classId);
			} else {
				return "the header must be " + currentHeader;
			}

		} else if (function.equals("Update by Supplier")) {
			currentHeader = "Supplier Name,Supplier,REF_URL";
			validHeader = checkHeader(currentHeader, list.get(0));
			if (validHeader) {
			supplier.updateSuppAction(fileName, list, classId);
			}else {
				return "the header must be " + currentHeader;
			}
		} else if (function.equals("Export by Supplier")) {
			currentHeader = "Supplier Name";
			validHeader = checkHeader(currentHeader, list.get(0));
			if (validHeader) {
				supplier.exportSuppAction(fileName, list, classId);
			} else {
				return "the header must be " + currentHeader;
			}

		} else if (function.equals("Delete by Supplier")) {
			currentHeader = "Supplier Name";
			validHeader = checkHeader(currentHeader, list.get(0));
			if (validHeader) {
				supplier.deleteSuppAction(fileName, list, classId);
			} else {
				return "the header must be " + currentHeader;
			}

		} else if (function.equals("Import Lookup Values")) {
			currentHeader = "Class Name,Class Version,Lookup Code,SE Code";
			validHeader = checkHeader(currentHeader, list.get(0));
			if (validHeader) {
				supplier.updateLookupValues(fileName, list);
			} else {
				return "the header must be " + currentHeader;
			}
		} else if (function.equals("Export Lookup Values")) {
			supplier.exportLookupValues(classId,fileName);
		} else if (function.equals("Delete Lookup Values")) {
			currentHeader = "Class Name,Class Version,SE Code";
			validHeader = checkHeader(currentHeader, list.get(0));
			if (validHeader) {
				supplier.deleteLookupValues(fileName, list);
			} else {
				return "the header must be " + currentHeader;
			}
		}

		return "The Process Done";
	}
}
