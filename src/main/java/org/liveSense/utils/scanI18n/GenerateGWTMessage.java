package org.liveSense.utils.scanI18n;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GenerateGWTMessage {

	static Messages locMessages = null;
	static String resourceName;
	static String resourceDir;
	static String packageName;
	static String defaultLanguage;
	static Pattern paramnum = Pattern.compile("\\{\\d\\}");
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 5) {
			System.out.println("Usage: GenerateGWTMessages <PATH_OF_GWT> <RESOURCE_NAME> <PACKAGE_NAME> <DEFAULT_LANGUAGE> <CSV_SHEET1> <CSV_SHEET2> ...");
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
		
		for (int i=4; i<args.length; i++) {
			File csv = new File(args[i]);
			if (!csv.exists()) {
				System.out.println("CSV does not exists: "+csv.getAbsolutePath());
				return;
			}
		}

		resourceDir = args[0];
		resourceName = args[1];
		packageName = args[2];
		defaultLanguage = args[3];

		locMessages = null;
		ArrayList<String> allLanguages = new ArrayList<String>();
		for (int i=4; i<args.length; i++) {
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
				e1.printStackTrace();
			}
		}

		try {
			// Iterate loc messages
			// Generate JAVA
			File javaFile = new File(resourceDir+"/"+resourceName+".java");
			BufferedWriter output;
			output = new BufferedWriter(new FileWriter(javaFile));
			output.write("package "+packageName+";\n");
			output.write("import com.google.gwt.i18n.client.Messages;\n");
			output.write("public interface "+resourceName+" extends Messages {\n");

			// Iterate message name
			for (String key : locMessages.keySet()) {
				// Little trick - counting {x} parameters. All parameters have to be String!
				LocMessage msg = locMessages.get(key);

				if ("GWT".equalsIgnoreCase(msg.getType())) {
					Matcher match = paramnum.matcher(msg.getMessages().get("en"));
					int count = 0;
					while(match.find()) {
						   count++;
					}

					output.write("\tString "+key+"(");
					for (int i=0; i<count; i++) {
						if (i>0) output.write(", ");
						output.write("String param"+i);
					}
					output.write(");\n");
				}
			}
			output.write("}\n");
			output.flush();
			output.close();

			// Generate default message
			File langFile = new File(resourceDir+"/"+resourceName+".properties");
			output = new BufferedWriter(new FileWriter(langFile));

			// Iterate message name
			for (String key : locMessages.keySet()) {
				// Little trick - counting {x} parameters. All parameters have to be String!
				LocMessage msg = locMessages.get(key);
				
				//if ("GWT".equalsIgnoreCase(msg.getType())) {
				    output.write(key+"="+stringEncode.native2ascii(msg.getMessages().get(defaultLanguage))+"\n");
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
					
					//if ("GWT".equalsIgnoreCase(msg.getType())) {
						output.write(key+"="+stringEncode.native2ascii(msg.getMessages().get(lang))+"\n");
					//}
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
