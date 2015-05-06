package com.se.tools;

import java.util.ArrayList;

import javax.faces.bean.ManagedBean;

@ManagedBean
public class StatisticsTool extends ToolBean {
	StatisticsTooActions actions = new StatisticsTooActions();

	public StatisticsTool() {
		function = "Export All PNs take Blank Code";
		this.functions = new ArrayList<String>();
		this.functions.add("Export All PNs take Blank Code");
		this.functions.add("Export All PNs take TBD Code");
		this.functions.add("Statistics Per Product Lines");
		this.functions.add("Statistics Per Part Numbers");
		this.functions.add("Export 4 Codes in One File");
	}

	@Override
	public String performAction(ArrayList<ArrayList<String>> list,
			String function, int classId,String fileName) {
		String currentHeader = "";
		boolean validHeader = false;
		System.out.println("Statistics action here");
		if (function.equals("Export All PNs take Blank Code")) {
			// currentHeader =
			// "SE Product Line,Code Name,Code Version,Code Description,REF_URL,Execution Order(Exit when fired),Code,SE Parameters Name 1,Start Cond1,Start Value1,Start Multiplier1,Start Unit1,End Cond1,End Value1,End Multiplier1,End Unit1,SE Parameters Name 2,Start Cond2,Start Value2,Start Multiplier2,Start Unit2,End Cond2,End Value2,End Multiplier2,End Unit2,SE Parameters Name 3,Start Cond3,Start Value3,Start Multiplier3,Start Unit3,End Cond3,End Value3,End Multiplier3,End Unit3,SE Parameters Name 4,Start Cond4,Start Value4,Start Multiplier4,Start Unit4,End Cond4,End Value4,End Multiplier4,End Unit4,SE Parameters Name 5,Start Cond5,Start Value5,Start Multiplier5,Start Unit5,End Cond5,End Value5,End Multiplier5,End Unit5";
			// validHeader = checkHeader(currentHeader, list.get(0));
			// if (validHeader) {
			actions.exportBlankParts(classId);
			// } else {
			// return "the header must be " + currentHeader;
			// }

		} else if (function.equals("Export All PNs take TBD Code")) {
			actions.exportTBLParts(classId);

		} else if (function.equals("Statistics Per Product Lines")) {
			actions.exportPL(classId);
		} else if (function.equals("Statistics Per Part Numbers")) {

			actions.exportPLandPN(classId);
		} else if (function.equals("Export 4 Codes in One File")) {
			currentHeader = "Part Number,Supplier Name";
			validHeader = checkHeader(currentHeader, list.get(0));
			if (validHeader) {
				actions.exportPartCodes(list);
			} else {
				return "the header must be " + currentHeader;
			}

		}
		return "Done";
	}
}
