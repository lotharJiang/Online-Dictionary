import java.net.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

public class DictionaryClient {
	private String serverHostName;
	private int port;

	public DictionaryClient(String serverHostName, int port) {
		this.serverHostName = serverHostName;
		this.port = port;
	}

	public String sendRequest(String operation, String word, String meaning) {

		String response = "";
		String address="";
		// Transfer request information into JSON format
		Map<String, String> argumentMap = new HashMap<String, String>();
		try {
			address = InetAddress.getLocalHost().getHostAddress().toString();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		argumentMap.put("opt", operation.toLowerCase());
		argumentMap.put("word", word);
		argumentMap.put("meaning", meaning);
		JSONObject argumentJson = new JSONObject(argumentMap);
		Map<String, String> messageMap = new HashMap<String, String>();
		messageMap.put("messageType", "0");
		messageMap.put("address", address);
		messageMap.put("opt", operation.toLowerCase());
		messageMap.put("argument", argumentJson.toString());
		JSONObject message = new JSONObject(messageMap);

		// Create socket between client and server
		try {
			Socket socket = new Socket(serverHostName, port);
			DataOutputStream outToServer = new DataOutputStream(
					socket.getOutputStream());
			BufferedReader inFromServer = new BufferedReader(
					new InputStreamReader(socket.getInputStream()));
			// Send request
			outToServer.writeBytes(message.toString());
			socket.shutdownOutput();
			// Get response
			response = inFromServer.readLine();
			socket.close();
		} catch (IOException e) {
			Map<String, String> errorMap = new HashMap<String, String>();
			errorMap.put("status", "003");
			errorMap.put("result", "");
			errorMap.put("info", e.getMessage());
			JSONObject errorJSON = new JSONObject(errorMap);
			
			Map<String, String> responseMap = new HashMap<String, String>();
			responseMap.put("messageType", "2");
			responseMap.put("address", address);
			responseMap.put("opt", "");
			responseMap.put("argument", errorJSON.toString());
			JSONObject responseJSON = new JSONObject(responseMap);
			response = responseJSON.toString();
		}

		return response;
	}

}
