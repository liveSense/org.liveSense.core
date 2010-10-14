package org.liveSense.utils.scanI18n;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class GetLocalizationStringFromBean {
	
	static int indentLevel = -1;
	static ArrayList<String> iteratedPath = new ArrayList<String>();

	static final String OBJ_PATTERN = "new\\s*Object\\s*\\x5b\\s*\\x5d\\s*\\x7b([\\p{ASCII}]+)\\x7d\\s*\\)\\s*";
	static final String ONE_PARAM_PATTERN = "(\\s*)\\s*\\)\\s*";
	static final String TWO_PARAM_PATTERN = "(\\s*),\\s*\\S+\\)";
	static final String TWO_PARAM_WITH_OBJ_PATTERN = "\\s*,\\s*"+OBJ_PATTERN;

	static Pattern pattern = Pattern.compile("getMessages\\s*\\(\\s*\\)\\s*\\.get\\s*\\(\\s*\\\"(\\S+)\\\"(("+TWO_PARAM_WITH_OBJ_PATTERN+")|("+TWO_PARAM_PATTERN+")|("+ONE_PARAM_PATTERN+"))");
	static Pattern paramPattern = Pattern.compile("\\x7b([0-9]+)\\x7d");
	
	static Messages locMessages;
	
	public static void processPath(File path) throws IOException {
		   File files[]; 
		    indentLevel++; 
		    String packageName = "";
			for (String pathL : iteratedPath) packageName += pathL+"/";

		    files = path.listFiles();
			
		    Arrays.sort(files);

			for (int i = 0, n = files.length; i < n; i++) {
				if (files[i].isDirectory()) {
				   iteratedPath.add(files[i].getName());
				   processPath(files[i]);
				   iteratedPath.remove(iteratedPath.size()-1);
			    } else {
			    	
			    	// Processing JAVA files
					if (files[i].getName().toLowerCase().endsWith(".java")) {
						//System.out.println("FileName: "+files[i].getName()+" "+files[i]);
						System.out.println("Processing: "+files[i].getParent()+"/"+files[i].getName());
						
						int lineNum = 1;
						Scanner fs = new Scanner(files[i]);
						while (fs.hasNextLine()) {
							String line = fs.nextLine();
							//System.out.println(line);
							
							Matcher match = pattern.matcher(line);
							
							
							while (match.find()) {
								//String key = match.group(1).replaceAll("\\\"\\)\\+", "");
								String key = match.group(1);
								System.out.println("Found: "+key);
								LocMessage msg = locMessages.get(key);
								if (msg == null) {
									msg = new LocMessage(key, "BEAN");
									locMessages.put(key, msg);
								}
								
								msg.putReference(files[i].getParent()+"/"+files[i].getName(), new Integer(lineNum));

									for (int j=2; j<match.groupCount();j++) {
										String m = match.group(j);
										if (m != null) m = m.trim();
										if (m != null && !m.isEmpty() && !m.startsWith(",") && !m.startsWith(")")) {
											System.out.println("-- PARAMS ----->"+m);

											int paramNum = m.split(",").length;
											for (String lngs : locMessages.getLanguages()) {
												HashSet<String> paramNums = new HashSet<String>();
												String mmsg = msg.messages.get(lngs);
												if  (mmsg == null) mmsg = "";
	
												Matcher matchp = paramPattern.matcher(mmsg);
												while (matchp.find()) {
													//String num = matchp.group(1);
													paramNums.add(matchp.group(1));
												}

												for (int k=0; k<paramNum; k++) {
													if (!paramNums.contains(String.valueOf(k))) {
														msg.messages.put(lngs, msg.messages.get(lngs)+"{"+String.valueOf(k)+"}");
													}
												}
											}
										}
									}

							}
							lineNum++;
						}
						fs.close();
						
					}
				}
			}

		    indentLevel--; 
	}
	
	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Usage: GetLocalizationStringFromBean <PATH_OF_JAVA> <EXCEL_SHEET>");
			return;
		}
		
		
		File directory = new File(args[0]);

		if (!directory.exists()) {
			System.out.println("Directory does not exists: "+directory.getAbsolutePath());
			return;
		}
		
		if (!directory.isDirectory()) {
			System.out.println("It's not a directory: "+directory.getAbsolutePath());
			return;			
		}

		File excel = new File(args[1]);
		if (!excel.exists()) {
			System.out.println("Excel does not exists: "+excel.getAbsolutePath());
			return;			
		}
		
		try {
			locMessages = new Messages(excel);
			processPath(directory);
			locMessages.WriteSheet();

		} catch (Throwable e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// List Image File
		//File files[];		
		//files = directory.listFiles();
		// /Users/robson/Project/Sling/com.esayfasi.gwt.Editor/src/
		
	}

}
