package com.se.tools;

import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.event.ActionEvent;

@ManagedBean
public class DynamicTool extends ToolBean {
	DynamicToolAction actions = new DynamicToolAction();

	public DynamicTool() {
		function = "Import by PL & Code Name & Code Version";
		this.functions = new ArrayList<String>();
		this.functions.add("Import by PL & Code Name & Code Version");
		this.functions.add("Export by PL & Code Name & Code Version");
		this.functions.add("Delete by PL & Code Name & Code Version");
		this.functions.add("Export by Code Name & Code Version");
		this.functions.add("Import Rad Hardened Rules");
		this.functions.add("Export Rad Hardened Rules");
		this.functions.add("Delete Rad Hardened Rules");
	}

	@Override
	public String performAction(ArrayList<ArrayList<String>> list,String function,int classId) {
		String currentHeader = "";
		boolean validHeader = false;
		System.out.println("Dynamic action here");
		if (function.equals("Import by PL & Code Name & Code Version")) {
			currentHeader = "SE Product Line\tCode Name\tCode Version\tCode Description\tREF_URL\tExecution Order(Exit when fired)\tCode\tSE Parameters Name 1\tStart Cond1\tStart Value1\tStart Multiplier1\tStart Unit1\tEnd Cond1\tEnd Value1\tEnd Multiplier1\tEnd Unit1\tSE Parameters Name 2\tStart Cond2\tStart Value2\tStart Multiplier2\tStart Unit2\tEnd Cond2\tEnd Value2\tEnd Multiplier2\tEnd Unit2\tSE Parameters Name 3\tStart Cond3\tStart Value3\tStart Multiplier3\tStart Unit3\tEnd Cond3\tEnd Value3\tEnd Multiplier3\tEnd Unit3\tSE Parameters Name 4\tStart Cond4\tStart Value4\tStart Multiplier4\tStart Unit4\tEnd Cond4\tEnd Value4\tEnd Multiplier4\tEnd Unit4\tSE Parameters Name 5\tStart Cond5\tStart Value5\tStart Multiplier5\tStart Unit5\tEnd Cond5\tEnd Value5\tEnd Multiplier5\tEnd Unit5";
			validHeader = checkHeader(currentHeader, list.get(0));
			if (validHeader) {
				actions.importRuleAction("", list);
			} else {
				return "the header must be " + currentHeader;
			}			
			
		} else if (function.equals("Export by PL & Code Name & Code Version")) {
			currentHeader = "Product Line\tCode Name\tCode Version";
			validHeader = checkHeader(currentHeader, list.get(0));
			if (validHeader) {
				actions.exportRuleAction("", list);
			} else {
				return "the header must be " + currentHeader;
			}	
			
		} else if (function.equals(
				"Delete by PL & Code Name & Code Version")) {
			currentHeader = "Product Line\tCode Name\tCode Version";
			validHeader = checkHeader(currentHeader, list.get(0));
			if (validHeader) {
				actions.deleteRulePLAction("", list);
			} else {
				return "the header must be " + currentHeader;
			}	
			
		} else if (function.equals(
				"Export by Code Name & Code Version")) {
			currentHeader = "Code Name\tCode Version";
			validHeader = checkHeader(currentHeader, list.get(0));
			if (validHeader) {
				actions.exportRulePLAction("", list);
			} else {
				return "the header must be " + currentHeader;
			}	
			
		} else if (function.equals("Import Rad Hardened Rules")) {
			currentHeader = "SE Product Line\tCode Name\tCode Version\tCode Description\tREF_URL\tExecution Order(Exit when fired)\tCode\tSE Parameters Name 1\tStart Cond1\tStart Value1\tStart Multiplier1\tStart Unit1\tEnd Cond1\tEnd Value1\tEnd Multiplier1\tEnd Unit1\tSE Parameters Name 2\tStart Cond2\tStart Value2\tStart Multiplier2\tStart Unit2\tEnd Cond2\tEnd Value2\tEnd Multiplier2\tEnd Unit2\tSE Parameters Name 3\tStart Cond3\tStart Value3\tStart Multiplier3\tStart Unit3\tEnd Cond3\tEnd Value3\tEnd Multiplier3\tEnd Unit3\tSE Parameters Name 4\tStart Cond4\tStart Value4\tStart Multiplier4\tStart Unit4\tEnd Cond4\tEnd Value4\tEnd Multiplier4\tEnd Unit4\tSE Parameters Name 5\tStart Cond5\tStart Value5\tStart Multiplier5\tStart Unit5\tEnd Cond5\tEnd Value5\tEnd Multiplier5\tEnd Unit5";
			validHeader = checkHeader(currentHeader, list.get(0));
			if (validHeader) {
				actions.importRadRuleAction("", list);
			} else {
				return "the header must be " + currentHeader;
			}	
			
		} else if (function.equals("Export Rad Hardened Rules")) {
			currentHeader = "Code Name\tCode Version";
			validHeader = checkHeader(currentHeader, list.get(0));
			if (validHeader) {
				actions.exportRadRuleAction("", list);
			} else {
				return "the header must be " + currentHeader;
			}	
			
		} else if (function.equals("Delete Rad Hardened Rules")) {
			currentHeader = "Code Name\tCode Version";
			validHeader = checkHeader(currentHeader, list.get(0));
			if (validHeader) {
				actions.deleteRadRuleAction("", list);
				} else {
				return "the header must be " + currentHeader;
			}	
			
		}
//		MainWindowBean.downloadController();
		return "The Process Done";
	}
}
