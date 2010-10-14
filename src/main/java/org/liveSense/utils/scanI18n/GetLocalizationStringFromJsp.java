package org.liveSense.utils.scanI18n;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetLocalizationStringFromJsp {
	
	static int indentLevel = -1;
	static ArrayList<String> iteratedPath = new ArrayList<String>();
	
	
	//static HashMap<String, LocMessage> messages = new HashMap<String, LocMessage>();
	static Pattern pattern = Pattern.compile("<fmt:message key=\\\"(\\S+)\\\"/>");
	
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
			    	
			    	// Processing JSP files
					if (files[i].getName().toLowerCase().endsWith(".jsp")) {
						//System.out.println("FileName: "+files[i].getName()+" "+files[i]);
						System.out.println("Processing: "+files[i].getParent()+"/"+files[i].getName());
						
						int lineNum = 1;
						Scanner fs = new Scanner(files[i]);
						while (fs.hasNextLine()) {
							String line = fs.nextLine();
							//System.out.println(line);
							
							Matcher match = pattern.matcher(line);
							
							
							while (match.find()) {
								String key = match.group(1);
								System.out.println("Found: "+key);
								LocMessage msg = locMessages.get(key);
								if (msg == null) {
									msg = new LocMessage(key, "JSP");
									locMessages.put(key, msg);
								}
								
								msg.putReference(files[i].getParent()+"/"+files[i].getName(), new Integer(lineNum));
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
			System.out.println("Usage: GetLocalizationStringFromJsp <PATH_OF_JSPS> <CSV_SHEET>");
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
			System.out.println("CSV does not exists: "+excel.getAbsolutePath());
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
