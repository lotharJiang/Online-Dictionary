import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.json.JSONObject;

public class WorkerPool {
	private final BlockingQueue<QueryTask> blockingQueue = new LinkedBlockingQueue<QueryTask>();

	private QueryThread threads[];
	private int workerPoolSize;

	public int getWorkerPoolSize() {
		return workerPoolSize;
	}

	public WorkerPool(int workerPoolSize) {
		this.workerPoolSize = workerPoolSize;
		threads = new QueryThread[this.workerPoolSize];
		for (int i = 0; i < this.workerPoolSize; i++) {
			threads[i] = new QueryThread(blockingQueue);
			threads[i].start();
		}

	}

	public void switchThread(int i) {
		if (threads[i].getState().toString().equals("TERMINATED")) {
			threads[i] = new QueryThread(blockingQueue);
			threads[i].start();
		} else {
			threads[i].interrupt();
		}
	}

	public void restartAllThreads() {
		for (int i = 0; i < workerPoolSize; i++) {
			if (threads[i].getState().toString().equals("TERMINATED")) {
				threads[i] = new QueryThread(blockingQueue);
				threads[i].start();
			}
		}

	}
	public void terminateAllThreads() {
		for (int i = 0; i < workerPoolSize; i++) {
			if (!threads[i].getState().toString().equals("TERMINATED")) {
				threads[i].interrupt();
			}
		}

	}


//	public void refresh() {
//		for (int i = 0; i < workerPoolSize; i++) {
//			if (threads[i].getState().toString().equals("TERMINATED")) {
//				threads[i] = new QueryThread(blockingQueue);
//				threads[i].start();
//				System.out.println("No." + i + " thread has been renewed\n");
//			}
//
//		}
//
//	}
//
//	public void resize(int size) {
//		if (size < threads.length) {
//			for (int i = threads.length - size; i < threads.length; i++) {
//				threads[i].interrupt();
//			}
//		}
//		workerPoolSize = size;
//		QueryThread tmpThreads[] = new QueryThread[workerPoolSize];
//		for (int i = 0; i < workerPoolSize; i++) {
//			if (i < threads.length) {
//				tmpThreads[i] = threads[i];
//			} else {
//				tmpThreads[i] = new QueryThread(blockingQueue);
//				tmpThreads[i].start();
//			}
//		}
//		threads = tmpThreads;
//	}

	public void pushTask(QueryTask queryTask) {
		try {
			blockingQueue.put(queryTask);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public JSONObject[] getThreadsStatus() {
		JSONObject[] threadsStatus = new JSONObject[workerPoolSize];
		for (int i = 0; i < workerPoolSize; i++) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("name", threads[i].getName());
			map.put("status", threads[i].getState().toString());
			JSONObject status = new JSONObject(map);
			threadsStatus[i] = status;
		}
		return threadsStatus;

	}
}
