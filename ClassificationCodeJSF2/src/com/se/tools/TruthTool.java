package com.se.tools;

import java.util.ArrayList;

import javax.faces.bean.ManagedBean;

@ManagedBean
public class TruthTool extends ToolBean {

	TruthToolActions actions = new TruthToolActions();

	public TruthTool() {
		this.function = "Export Truth Table";
		this.functions = new ArrayList<String>();
		this.functions.add("Export Truth Table");
		this.functions.add("Import Truth Table");
	}

	public String performAction(ArrayList<ArrayList<String>> list,
			String function, int classId,String fileName) {
		String currentHeader = "";
		boolean validHeader = false;
		System.out.println("Truth action here");
		if (function.equals("Import Truth Table")) {
			currentHeader = "DYN_CLASS,STATIC_CLASS,SUPPLIER_CLASS,SE_CLAS,CLAS_REF,CLAS_CONF_LVL,FLAGY";
			validHeader = checkHeader(currentHeader, list.get(0));
			if (validHeader) {
				actions.importTruthTableAction(list);
			} else {
				return "the header must be " + currentHeader;
			}			
			
		} else if (function.equals("Export Truth Table")) {
			actions.exportTruthDataAction("");
		}
		return "Done";
	}

}
