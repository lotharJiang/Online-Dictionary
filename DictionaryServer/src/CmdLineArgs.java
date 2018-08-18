import org.kohsuke.args4j.*;

public class CmdLineArgs {
	@Option(required = false, name = "-d", aliases = {"--dictionary"}, usage = "Dictionary File")
	private String dictionary = "dictionary.dat";
	
	@Option(required = false, name = "-p", usage = "Port number")
	private int port = 4444;
	
	@Option(required = false, name = "-wps", usage = "Size of worker thread pool")
	private int workerPoolSize = 5;


	public String getDictinary() {
		return dictionary;
	}

	public int getPort() {
		return port;
	}
	
	public int getWorkerPoolSize() {
		return workerPoolSize;
	}
}
