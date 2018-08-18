import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

public class Dictionary {
	public Dictionary(String dictPath) {
		dictFilePath = dictPath;
		// Read data from dictionary file
		BufferedReader fileinputStream = null;
		try {
			fileinputStream = new BufferedReader(new FileReader(dictFilePath));
		} catch (FileNotFoundException e) {
			// If file does not exist, create a empty file
			try {
				File file = new File(dictFilePath);
				file.createNewFile();
				fileinputStream = new BufferedReader(new FileReader(file));
			} catch (IOException e1) {
				System.err.println(e.getMessage());
			}
		}

		// Build the Collection of dictionary entries
		try {
			while (fileinputStream.ready()) {
				String line = fileinputStream.readLine();
				JSONObject wordJsonObject = new JSONObject(line);
				addEntry(wordJsonObject);
			}

		} catch (Exception e) {
			// IOExceptionn & JSONException
			System.err.println(e.getMessage());
		}

	}

	public synchronized JSONObject query(String wordToSearch) {
		boolean wordFound = false;

		Map<String, String> map = new HashMap<String, String>();

		try {
			for (JSONObject a : dictEntries) {
				String word = (String) a.get("word");
				String meaning = (String) a.get("meaning");
				if (word.equals(wordToSearch)) {
					wordFound = true;
					map.put("status", "001");
					map.put("result", meaning);
					map.put("info", "");
					break;
				}
			}
			if (!wordFound) {
				throw new Exception("The word '" + wordToSearch + "' does not exist in the dictionary.");
			}
		} catch (Exception e) {
			map.put("status", "002");
			map.put("result", "");
			map.put("info", e.getMessage());
			System.err.println(e.getMessage());
		}

		JSONObject responseJSON = new JSONObject(map);
		return responseJSON;
	}

	public synchronized JSONObject addEntry(JSONObject entryToAdd) {
		boolean wordFound = false;
		entryToAdd.remove("opt");
		String wordToAdd = (String) entryToAdd.get("word");
		Map<String, String> map = new HashMap<String, String>();
		try {
			for (JSONObject a : dictEntries) {
				String word = (String) a.get("word");
				if (word.equals(wordToAdd)) {
					wordFound = true;
					throw new Exception("The word '" + wordToAdd + "' already exists in the dictionary.");
				}
			}
			if (!wordFound) {
				dictEntries.add(entryToAdd);
				map.put("status", "001");
				map.put("result", (String) entryToAdd.get("meaning"));
				map.put("info", "The word '" + wordToAdd + "' is added successfully.");
			}
		} catch (Exception e) {
			map.put("status", "002");
			map.put("result", "");
			map.put("info", e.getMessage());
			System.err.println(e.getMessage());
		}

		try {
			writeDictFile();
		} catch (IOException e) {
			map.put("status", "003");
			map.put("result", "");
			map.put("info", "Fail to synchronize the dictionary file ");
			System.err.println(e.getMessage());

		}

		JSONObject responseJSON = new JSONObject(map);
		return responseJSON;
	}

	public synchronized JSONObject removeEntry(String wordToRemove) {
		boolean wordFound = false;
		Map<String, String> map = new HashMap<String, String>();
		try {
			for (JSONObject a : dictEntries) {
				String word = (String) a.get("word");
				if (word.equals(wordToRemove)) {
					dictEntries.remove(a);
					wordFound = true;
					map.put("status", "001");
					map.put("result", "");
					map.put("info", "The word '" + wordToRemove + "' is removed successfully.");
					break;
				}
			}
			if (!wordFound) {
				throw new Exception("The word '" + wordToRemove + "' does not exist in the dictionary.");
			}
		} catch (Exception e) {
			map.put("status", "002");
			map.put("result", "");
			map.put("info", e.getMessage());
			System.err.println(e.getMessage());

		}

		try {
			writeDictFile();
		} catch (IOException e) {
			map.put("status", "003");
			map.put("result", "");
			map.put("info", "Fail to synchronize the dictionary file ");
			System.err.println(e.getMessage());
		}
		JSONObject responseJSON = new JSONObject(map);
		return responseJSON;

	}

	private void writeDictFile() throws IOException {
		// Use to temporarily store the data of dictionary
		File file = new File(dictFilePath);
		File temDict;

		temDict = File.createTempFile(dictFilePath, "tmp");
		FileWriter writer = new FileWriter(temDict, true);
		for (JSONObject wordJson : dictEntries) {
			String wordJsonString = wordJson.toString() + "\n";
			writer.write(wordJsonString);
			writer.flush();
		}
		temDict.renameTo(file);
		writer.close();

	}
//
//	public ArrayList<JSONObject> getDictEntries() {
//		return dictEntries;
//	}

	private ArrayList<JSONObject> dictEntries = new ArrayList<>();
	private String dictFilePath;

}
