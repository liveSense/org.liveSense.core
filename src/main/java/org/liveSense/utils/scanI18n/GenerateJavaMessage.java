package org.liveSense.utils.scanI18n;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GenerateJavaMessage {

	static Messages locMessages = null;
	static String resourceName;
	static String resourceDir;
	static String packageName;
	static String defaultLocale;
	static Pattern paramnum = Pattern.compile("\\{\\d\\}");
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 4) {
			System.out.println("Usage: GenerateJavaMessages <PATH_OF_JAVA> <RESOURCE_NAME> <DEFAULT_LOCALE> <CSV_SHEET1> <CSV_SHEET2> ...");
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

		for (int i=3; i<args.length; i++) {
			File csv = new File(args[i]);
			if (!csv.exists()) {
				System.out.println("CSV does not exists: "+csv.getAbsolutePath());
				return;
			}
		}

		resourceDir = args[0];
		resourceName = args[1];
		defaultLocale = args[2];

		locMessages = null;
		ArrayList<String> allLanguages = new ArrayList<String>();
		for (int i=3; i<args.length; i++) {
			File csv = new File(args[i]);

			try {

				locMessages = new Messages(csv, locMessages);
				ArrayList<String> languages = locMessages.getLanguages();
				for (String lang : languages) {
					boolean exists = false;
					for (String existLang : allLanguages) {
						if (existLang.equals(lang)) exists = true;
					}
					if (!exists) {
						allLanguages.add(lang);
					}
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		try {

			// Generate default message
			File langFile = new File(resourceDir+"/"+resourceName+".properties");
			BufferedWriter output = new BufferedWriter(new FileWriter(langFile));

			// Iterate message name
			for (String key : locMessages.keySet()) {
				// Little trick - counting {x} parameters. All parameters have to be String!
				LocMessage msg = locMessages.get(key);

				//if ("JSP".equalsIgnoreCase(msg.getType()) || "Java".equalsIgnoreCase(msg.getType())) {
					output.write(key+"="+stringEncode.native2ascii(msg.getMessages().get(defaultLocale))+"\n");
				//}
			}
			output.flush();
			output.close();

			// Languages
			for (String lang : allLanguages) {
				langFile = new File(resourceDir+"/"+resourceName+"_"+lang+".properties");
				output = new BufferedWriter(new FileWriter(langFile));
				// Iterate message name
				for (String key : locMessages.keySet()) {
					// Little trick - counting {x} parameters. All parameters have to be String!
					LocMessage msg = locMessages.get(key);

				//if ("JSP".equalsIgnoreCase(msg.getType()) || "Java".equalsIgnoreCase(msg.getType())) {
					output.write(key+"="+stringEncode.native2ascii(msg.getMessages().get(lang))+"\n");
				//	}
				}
				output.flush();
				output.close();
			}

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
