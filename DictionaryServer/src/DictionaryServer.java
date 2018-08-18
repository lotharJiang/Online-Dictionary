import java.net.*;
import java.io.*;

public class DictionaryServer {
	private WorkerPool workerPool;
	private Dictionary dictionary;
	private ServerSocket serverSocket;
	public WorkerPool getWorkerPool() {
		return workerPool;
	}

	public DictionaryServer(String dictionaryPath, int port, int workerPoolSize) {

		dictionary = new Dictionary(dictionaryPath);
		workerPool = new WorkerPool(workerPoolSize);
		try {
			serverSocket = new ServerSocket(port);
			System.out.println("Socket Open. Port:" + port);
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	public void runServer() {
		while (true) {
			Socket s1 = null;

			System.out.println("Server socket is waiting for connecting request...\n");
			try {
				s1 = serverSocket.accept();
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
			QueryTask newTask = new QueryTask(s1, dictionary);
			workerPool.pushTask(newTask);
		}

	}

}
