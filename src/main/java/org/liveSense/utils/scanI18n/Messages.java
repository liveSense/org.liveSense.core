package org.liveSense.utils.scanI18n;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class Messages extends HashMap<String, LocMessage> {

	URL csvUrl;
	HashMap<String, Integer> columns = new HashMap<String, Integer>();

	public ArrayList<String> getLanguages() {
		return languages;
	}

	public void setLanguages(ArrayList<String> languages) {
		this.languages = languages;
	}
	ArrayList<String> languages = new ArrayList<String>();

	public Messages(File csv) throws MalformedURLException {
		initMessages(csv, null);
	}

	public Messages(File csv, Messages exists) throws MalformedURLException {
		initMessages(csv, exists);
	}

	public HashMap<String, Integer> getColumns() {
		return columns;
	}

	void initMessages(File csv, Messages exists) throws MalformedURLException {

		if (exists != null) {
//			columns = exists.getColumns();
//			languages = exists.getLanguages();
			for (String key : exists.keySet()) {
				this.put(key, exists.get(key));
			}
		}

		csvUrl = csv.toURI().toURL();

		// Parsing language table
		if (csvUrl != null) {
			try {
				CSVReader reader = new CSVReader(new InputStreamReader(new BufferedInputStream(csvUrl.openStream()), "UTF-8"));
				String[] nextLine;
				boolean firstLine = true;
				String[] colNames = null;

				while ((nextLine = reader.readNext()) != null) {
					if (firstLine) {
						colNames = nextLine.clone();
						// Determinate locale
						for (int i = 0; i < nextLine.length; i++) {
							if (!nextLine[i].trim().equalsIgnoreCase("name")
									&& !nextLine[i].trim().equalsIgnoreCase("type")) {
								
								String lang = nextLine[i].trim().toLowerCase();
								boolean langExists = false;
								for (String langexists : languages) {
									if (langexists.equals(lang)) {
										langExists = true;
									}
								}
								if (!langExists) {
									languages.add(lang);
								}
							}
							;
							columns.put(nextLine[i].trim().toLowerCase(), new Integer(i));
						}
						for (int i = 0; i < nextLine.length; i++) {
							System.out.println(nextLine[i]);
						}
						firstLine = false;
					} else {
						HashMap<String, String> lang = new HashMap<String, String>();
						for (int i = 0; i < nextLine.length; i++) {
							//lang.put(colNames[i], nextLine[i]);

							LocMessage locMsg = new LocMessage(nextLine[columns.get("name").intValue()], nextLine[columns.get("type").intValue()]);

							// Languages
							for (int j = 0; j < languages.size(); j++) {
								if (columns.containsKey(languages.get(j))) {
									locMsg.setMessage(languages.get(j), nextLine[columns.get(languages.get(j)).intValue()]);
								}
							}
							put(nextLine[columns.get("name").intValue()], locMsg);

						}

					}
				}
			} catch (IOException ex) {
				System.out.println("Error on CSV loading (" + csvUrl.getFile() + ")");
				ex.printStackTrace();
			}
		}
	}

	public void WriteSheet() throws Throwable {

		try {
			CSVWriter writer = new CSVWriter(new OutputStreamWriter(new FileOutputStream(new File(csvUrl.toURI())), "utf-8"), ',');

			// Column names
			String[] entries = new String[columns.size()];
			//int idx = 0;
			for (String key : columns.keySet()) {
				entries[columns.get(key).intValue()] = key;
			}

			writer.writeNext(entries);

			for (String key : this.keySet()) {
				//ArrayList<String> out = columns.entrySet().clone();
				String[] out = new String[columns.size()];

				LocMessage msg = this.get(key);
				for (String colName : columns.keySet()) {
					if (colName.equals("name")) {
						out[columns.get(colName).intValue()] = key;
					} else if (colName.equals("type")) {
						out[columns.get(colName).intValue()] = msg.getType();
					} else {
						out[columns.get(colName).intValue()] = msg.getMessages().get(colName);
					}
				}
				writer.writeNext(out);
			}

			writer.flush();
			writer.close();
		} catch (Throwable th) {
			throw th;
		}


	}
}
