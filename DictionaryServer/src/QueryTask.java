import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.io.*;
import org.json.JSONObject;

public class QueryTask implements Runnable {

	public QueryTask() {
		super();
	}

	public QueryTask(Socket socket, Dictionary dictionary) {
		super();
		this.socket = socket;
		this.dictionary = dictionary;
	}

	@Override
	public void run() {
		DataOutputStream dos = null;
		try {
			dos = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		JSONObject response = new JSONObject();
		
		try {

			DataInputStream dis = new DataInputStream(socket.getInputStream());

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] by = new byte[2048];
			int n;
			while ((n = dis.read(by)) != -1) {
				baos.write(by, 0, n);
			}
			String strInputstream = new String(baos.toByteArray());
			baos.close();

			JSONObject json = new JSONObject(strInputstream);
			String client = (String) json.get("address");
			String operation = (String) json.get("opt");
			String arguments = (String) json.get("argument");
			JSONObject argumentJson = new JSONObject(arguments);
			String word = (String) argumentJson.get("word");
			String meaning = (String) argumentJson.get("meaning");
			System.out.println("Request from client "+ client+" :");
			System.out.println("Operation: "+operation + "\t|Word: " + word + "\t|Meaning: " + meaning);

			switch (operation) {
			case "query":
				response = dictionary.query(word);
				break;
			case "add":
				response = dictionary.addEntry(argumentJson);
				break;
			case "remove":
				response = dictionary.removeEntry(word);
				break;

			default:
				break;
			}
			String status = (String) response.get("status");
			if(status.equals("001")) status+=" (Success)";
			else if(status.equals("002")) status+=" (Operation Error)";
			else if(status.equals("003")) status+=" (System Error)";
			String result = (String) response.get("result");
			String infomation = (String) response.get("info");
			System.out.println("Respone to client "+ client+" :");
			System.out.println("Status: "+status + "\t|Result: " + result + "\t|Infomation: " + infomation+"\n");

			

		} catch (Exception e) {
			Map<String, String> errorMap = new HashMap<String, String>();
			errorMap.put("status", "003");
			errorMap.put("result", "");
			errorMap.put("info", e.getMessage());
			System.err.println(e.getMessage());
			JSONObject errorJSON = new JSONObject(errorMap);
			response = errorJSON;
			
		}finally {
			try {
				Map<String, String> messageMap = new HashMap<String, String>();
				messageMap.put("messageType", "1");
				messageMap.put("address", InetAddress.getLocalHost().getHostAddress().toString());
				messageMap.put("opt", "");
				messageMap.put("argument", response.toString());
				JSONObject messageJSON = new JSONObject(messageMap);
				dos.writeBytes(messageJSON.toString());
				socket.close();
			} catch (IOException e1) {
				System.err.println(e1.getMessage());
			}
			
		}
	}

	private Socket socket;
	private Dictionary dictionary;
}
