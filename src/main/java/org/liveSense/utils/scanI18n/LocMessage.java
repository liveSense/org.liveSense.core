package org.liveSense.utils.scanI18n;

import java.util.ArrayList;
import java.util.HashMap;

public class LocMessage {
	
	public class SourceCodeRef {
		private String srcName;
		private Integer lineNum;
		
		public SourceCodeRef(String srcName, Integer lineNum) {
			this.srcName = srcName;
			this.lineNum = lineNum;
		}

		public String getSrcName() {
			return srcName;
		}
		public void setSrcName(String srcName) {
			this.srcName = srcName;
		}
		public Integer getLineNum() {
			return lineNum;
		}
		public void setLineNum(Integer lineNum) {
			this.lineNum = lineNum;
		}
	};
	
	String key;
//	Boolean sourceRegerenced;
//	Boolean excelReferenced;
	String type;
	
	HashMap<String, String> messages; // Key: Language, Value: Message
	ArrayList<SourceCodeRef> sourceReferences; // Key: Source name, Value: Line number
	ArrayList<SourceCodeRef>  excelSourceReferences; // What is referenced in excel - to color the new messages and unused)
	Integer excelRowNum;
	
	
	public LocMessage(String key, String type) {
		this.key = key; 
		this.type = type;
		messages = new HashMap<String, String>();
		sourceReferences = new ArrayList<SourceCodeRef>();
		excelSourceReferences = new ArrayList<SourceCodeRef>();
	}
	
	
	public void putReference(String src, Integer line) {
		sourceReferences.add(new SourceCodeRef(src, line));
	}
	
	public void putExcelReference(String src, Integer line) {
		excelSourceReferences.add(new SourceCodeRef(src, line));
	}
	
	
	public ArrayList<SourceCodeRef> getReferences() {
		return sourceReferences;
	}

	public ArrayList<SourceCodeRef> getExcelReferences() {
		return excelSourceReferences;
	}

	public String getKey() {
		return key;
	}
	
	public HashMap<String, String> getMessages() {
		return messages;
	}
	
	public void setMessage(String lang, String message) {
		messages.put(lang, message);
	}


	public Integer getExcelRowNum() {
		return excelRowNum;
	}


	public void setExcelRowNum(Integer excelRowNum) {
		this.excelRowNum = excelRowNum;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}

	
	
}

