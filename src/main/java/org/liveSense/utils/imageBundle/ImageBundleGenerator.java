package org.liveSense.utils.imageBundle;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;

public class ImageBundleGenerator {

	static int indentLevel = -1;
	static ArrayList<String> iteratedPath = new ArrayList<String>();
	static String className;
	
	public static String convertImageName(String name) {
		String sp[] = name.split("\\.");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i<sp.length-1; i++) {
			sb.append(sp[i]);
		}
		sp = sb.toString().split("\\s+");
		sb = new StringBuilder();
		for (int i = 0; i<sp.length; i++) {
			sb.append(sp[i].substring(0, 1).toUpperCase()+sp[i].substring(1));
		}
		return sb.toString();
	}
	
	
	public static void processPath(File path) throws IOException {
		   File files[]; 
		    indentLevel++; 
		    String packageName = "";
			for (String pathL : iteratedPath) packageName += pathL+"/";

		    files = path.listFiles();

		    Arrays.sort(files);

			File javaFile = null;
			Writer output = null;

			for (int i = 0, n = files.length; i < n; i++) {
				if (files[i].isDirectory()) {
				   iteratedPath.add(files[i].getName());
				   processPath(files[i]);
				   iteratedPath.remove(iteratedPath.size()-1);
			    } else {
					if (files[i].getName().toLowerCase().endsWith(".png") || files[i].getName().toLowerCase().endsWith(".jpg") || files[i].getName().toLowerCase().endsWith(".gif")) {
						//System.out.println("FileName: "+files[i].getName()+" "+files[i]);
						System.out.println("Processing: "+files[i].getParent()+files[i].getName());
						if (javaFile == null) {
							
							javaFile = new File(files[i].getParent()+"/"+className+".java");
							output = new BufferedWriter(new FileWriter(javaFile));
							String pack = "";
							for (String pathL : iteratedPath) pack += pathL+".";
							pack = pack.substring(0, pack.length()-1);

							output.write("package "+pack+";\n\n");
							output.write("import com.google.gwt.user.client.ui.AbstractImagePrototype;\n");
							output.write("import com.google.gwt.user.client.ui.ImageBundle;\n\n");


							output.write("public interface "+className+" extends ImageBundle {\n");
						}
						
						output.write("@Resource(\""+packageName+files[i].getName()+"\")\n");
						output.write("AbstractImagePrototype "+convertImageName(files[i].getName())+"();\n");
					}					
				}
			}
			if (output != null) {
				output.write("}\n");
				output.flush();
			}

		    indentLevel--; 
	}
	
	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Usage: ImageBundleGenerator <PATH_OF_IMAGES_AND_GWT_SRC> <CLASS_NAME>");
			return;
		}
		
		File directory = new File(args[0]);
		className = args[1];
		
		if (!directory.exists()) {
			System.out.println("Directory does not exists: "+directory.getAbsolutePath());
			return;
		}
		
		if (!directory.isDirectory()) {
			System.out.println("It's not a directory: "+directory.getAbsolutePath());
			return;			
		}
		
		try {
			processPath(directory);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// List Image File
		//File files[];
		
		//files = directory.listFiles();
		
		// /Users/robson/Project/Sling/com.esayfasi.gwt.Editor/src/
		
		
	}

}
