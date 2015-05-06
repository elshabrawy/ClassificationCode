package com.se.tools;

import java.util.ArrayList;

import javax.faces.bean.ManagedBean;

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
	public String performAction(ArrayList<ArrayList<String>> list,String function,int classId,String fileName) {
		String currentHeader = "";
		boolean validHeader = false;
		System.out.println("Dynamic action here");
		if (function.equals("Import by PL & Code Name & Code Version")) {
			currentHeader = "SE Product Line,Code Name,Code Version,Code Description,REF_URL,Execution Order(Exit when fired),Code,SE Parameters Name 1,Start Cond1,Start Value1,Start Multiplier1,Start Unit1,End Cond1,End Value1,End Multiplier1,End Unit1,SE Parameters Name 2,Start Cond2,Start Value2,Start Multiplier2,Start Unit2,End Cond2,End Value2,End Multiplier2,End Unit2,SE Parameters Name 3,Start Cond3,Start Value3,Start Multiplier3,Start Unit3,End Cond3,End Value3,End Multiplier3,End Unit3,SE Parameters Name 4,Start Cond4,Start Value4,Start Multiplier4,Start Unit4,End Cond4,End Value4,End Multiplier4,End Unit4,SE Parameters Name 5,Start Cond5,Start Value5,Start Multiplier5,Start Unit5,End Cond5,End Value5,End Multiplier5,End Unit5";
			validHeader = checkHeader(currentHeader, list.get(0));
			if (validHeader) {
				actions.importRuleAction(fileName, list);
			} else {
				return "the header must be " + currentHeader;
			}			
			
		} else if (function.equals("Export by PL & Code Name & Code Version")) {
			currentHeader = "Product Line,Code Name,Code Version";
			validHeader = checkHeader(currentHeader, list.get(0));
			if (validHeader) {
				actions.exportRuleAction(fileName, list);
			} else {
				return "the header must be " + currentHeader;
			}	
			
		} else if (function.equals(
				"Delete by PL & Code Name & Code Version")) {
			currentHeader = "Product Line,Code Name,Code Version";
			validHeader = checkHeader(currentHeader, list.get(0));
			if (validHeader) {
				actions.deleteRulePLAction(fileName, list);
			} else {
				return "the header must be " + currentHeader;
			}	
			
		} else if (function.equals(
				"Export by Code Name & Code Version")) {
			currentHeader = "Code Name,Code Version";
			validHeader = checkHeader(currentHeader, list.get(0));
			if (validHeader) {
				actions.exportRulePLAction(fileName, list);
			} else {
				return "the header must be " + currentHeader;
			}	
			
		} else if (function.equals("Import Rad Hardened Rules")) {
			currentHeader = "SE Product Line,Code Name,Code Version,Code Description,REF_URL,Execution Order(Exit when fired),Code,SE Parameters Name 1,Start Cond1,Start Value1,Start Multiplier1,Start Unit1,End Cond1,End Value1,End Multiplier1,End Unit1,SE Parameters Name 2,Start Cond2,Start Value2,Start Multiplier2,Start Unit2,End Cond2,End Value2,End Multiplier2,End Unit2,SE Parameters Name 3,Start Cond3,Start Value3,Start Multiplier3,Start Unit3,End Cond3,End Value3,End Multiplier3,End Unit3,SE Parameters Name 4,Start Cond4,Start Value4,Start Multiplier4,Start Unit4,End Cond4,End Value4,End Multiplier4,End Unit4,SE Parameters Name 5,Start Cond5,Start Value5,Start Multiplier5,Start Unit5,End Cond5,End Value5,End Multiplier5,End Unit5";
			validHeader = checkHeader(currentHeader, list.get(0));
			if (validHeader) {
				actions.importRadRuleAction(fileName, list);
			} else {
				return "the header must be " + currentHeader;
			}	
			
		} else if (function.equals("Export Rad Hardened Rules")) {
			currentHeader = "Code Name,Code Version";
			validHeader = checkHeader(currentHeader, list.get(0));
			if (validHeader) {
				actions.exportRadRuleAction(fileName, list);
			} else {
				return "the header must be " + currentHeader;
			}	
			
		} else if (function.equals("Delete Rad Hardened Rules")) {
			currentHeader = "Code Name,Code Version";
			validHeader = checkHeader(currentHeader, list.get(0));
			if (validHeader) {
				actions.deleteRadRuleAction(fileName, list);
				} else {
				return "the header must be " + currentHeader;
			}	
			
		}
		return "The Process Done";
	}
}
