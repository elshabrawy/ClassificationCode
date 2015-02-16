package com.se.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.faces.bean.ManagedBean;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;


public abstract class ToolBean {
		String currentEnabl = "";
	String function;
	ArrayList<String> functions;
	
	ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();

	public abstract String performAction(ArrayList<ArrayList<String>> list,
			String function,int classId);

	public ArrayList<ArrayList<String>> getList() {
		return list;
	}

	public void setList(ArrayList<ArrayList<String>> list) {
		this.list = list;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	public ArrayList<String> getFunctions() {
		return functions;
	}

	public void setFunctions(ArrayList<String> functions) {
		this.functions = functions;
	}

	public ToolBean() {

	}

	public boolean checkHeader(String validHeader, ArrayList<String> header) {
		String[] arr = validHeader.split("\t");
		if (arr.length != header.size()) {
			return false;
		}
		for (int i = 0; i < arr.length; i++) {
			if (!arr[i].equals(header.get(i))) {
				return false;
			}
		}
		return true;
	}
	


}
