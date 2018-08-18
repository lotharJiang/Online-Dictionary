import java.util.concurrent.BlockingQueue;

public class QueryThread extends Thread {

	private BlockingQueue<QueryTask> tasks;
	
	public QueryThread(final BlockingQueue<QueryTask> tasks) {
		this.tasks=tasks;
		System.out.println(this.getName() +" is generated.\n");
	}
	@Override
	public void run() {
		while (true) {
			try {
				System.out.println(this.getName() +" is waiting for task...\n");
				QueryTask task = tasks.take(); 
				System.out.println(this.getName() +" is handling task.\n");
				task.run();
			} catch (InterruptedException e) {
				throw new RuntimeException(this.getName() +" is terminated.\n");
			}
		}
		
	}

}
