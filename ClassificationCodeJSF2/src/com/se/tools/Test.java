package com.se.tools;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.faces.bean.ManagedBean;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
@ManagedBean
public class Test {
private StreamedContent outputFile;
	
	private File logFile;
	public void downloadController() {

		InputStream stream;
		try {
			stream = new FileInputStream(new File("D:\\FilesTemp\\ClassificationTools\\Log.txt"));
			outputFile = new DefaultStreamedContent(stream, "text/plain",
					"LogFile.txt");
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		}

	}
	public StreamedContent getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(StreamedContent outputFile) {
		this.outputFile = outputFile;
	}

	public File getLogFile() {
		return logFile;
	}

	public void setLogFile(File logFile) {
		this.logFile = logFile;
	}
}
