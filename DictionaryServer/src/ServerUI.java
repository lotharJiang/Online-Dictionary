import java.awt.EventQueue;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.border.EmptyBorder;

import org.json.JSONObject;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.lang.model.type.NullType;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ServerUI extends JFrame {

	private JPanel contentPane;
	// private JTextField portField;
	private JTextArea infoArea;
	private JTable workerPoolTable;
	// private JTextField sizeField;
	private int workerPoolSize = 1;
	JScrollPane scrollpane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				String dictionaryPath = "";
				int port = 0;
				int size = 1;
				try {
					// Object that will store the parsed command line arguments
					CmdLineArgs argsBean = new CmdLineArgs();

					// Parser provided by args4j
					CmdLineParser parser = new CmdLineParser(argsBean);
					try {

						// Parse the arguments
						parser.parseArgument(args);

						// After parsing, the fields in argsBean have been
						// updated with the
						// given command line arguments
						dictionaryPath = argsBean.getDictinary();
						port = argsBean.getPort();
						size = argsBean.getWorkerPoolSize();

					} catch (CmdLineException e) {

						System.err.println(e.getMessage());

						// Print the usage to help the user understand the
						// arguments
						// expected by the program
						parser.printUsage(System.err);
					}
					final String dict = dictionaryPath;
					final int p = port;
					final int s = size;

					EventQueue.invokeLater(new Runnable() {

						@Override
						public void run() {
							JFrame frame = new ServerUI(dict, p, s);
							frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
							frame.setVisible(true);
						}

					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ServerUI(String dictionaryPath, int port, int size) {
		redirectSystemStreams();
		final DictionaryServer server = new DictionaryServer(dictionaryPath, port, size);
		WorkerPool workerPool = server.getWorkerPool();
		workerPoolSize = workerPool.getWorkerPoolSize();
		ServerRunner serverRunner = new ServerRunner(server);
		serverRunner.execute();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		refreshWokerPool(workerPool);
		// portField = new JTextField();
		// portField.setBounds(408, 23, 155, 26);
		// portField.setText(new Integer(server.getPort()).toString());
		// contentPane.add(portField);
		// portField.setColumns(10);

		JLabel hostLabel = new JLabel("Host");
		hostLabel.setBounds(488, 28, 172, 16);
		try {
			hostLabel.setText("Host:\t" + InetAddress.getLocalHost().getHostAddress().toString());
		} catch (UnknownHostException e) {
			System.err.println(e.getMessage());
		}
		contentPane.add(hostLabel);

		JLabel lblPort = new JLabel("Port:");
		lblPort.setText("Port: " + port);
		lblPort.setBounds(672, 28, 78, 16);
		contentPane.add(lblPort);
		//
		// // final DictionaryServer tmpSever = server;
		// JButton ResetSocketBtn = new JButton("Reset Socket");
		// ResetSocketBtn.addActionListener(new ActionListener() {
		// public void actionPerformed(ActionEvent e) {
		// try {
		// server.setPort(new Integer(portField.getText()));
		// } catch (Exception e2) {
		// System.err.println(e2.getMessage());
		// }
		// }
		//
		// });
		// ResetSocketBtn.setBounds(575, 23, 175, 29);
		// contentPane.add(ResetSocketBtn);

		infoArea = new JTextArea();
		infoArea.setEditable(false);
		infoArea.setBounds(50, 50, 700, 200);

		JScrollPane scroll = new JScrollPane(infoArea);
		scroll.setBounds(50, 50, 700, 200);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		contentPane.add(scroll);

		JLabel lblServerInfomation = new JLabel("Server Infomation:");
		lblServerInfomation.setBounds(50, 28, 398, 16);
		contentPane.add(lblServerInfomation);

		JLabel lblWokerPool = new JLabel("Woker Pool:");
		lblWokerPool.setBounds(50, 267, 80, 16);
		contentPane.add(lblWokerPool);

		JButton btnRenewAllThread = new JButton("Start All Threads");
		btnRenewAllThread.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				workerPool.restartAllThreads();
				refreshWokerPool(workerPool);

			}
		});
		btnRenewAllThread.setBounds(446, 262, 139, 29);
		contentPane.add(btnRenewAllThread);

		JLabel lblThreadNo = new JLabel("Thread No. :");
		lblThreadNo.setBounds(142, 268, 105, 16);
		contentPane.add(lblThreadNo);

		JComboBox comboBox = new JComboBox();
		String[] threadsCount = new String[workerPoolSize];
		for (int i = 0; i < workerPoolSize; i++) {
			threadsCount[i] = new Integer(i).toString();
		}
		comboBox.setModel(new DefaultComboBoxModel(threadsCount));
		comboBox.setBounds(227, 263, 84, 27);
		contentPane.add(comboBox);

		JButton renewButton = new JButton("Start/Terminate");
		renewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				workerPool.switchThread(new Integer(comboBox.getSelectedIndex()));
				refreshWokerPool(workerPool);
			}
		});
		renewButton.setBounds(313, 262, 139, 29);
		contentPane.add(renewButton);
		
		JButton btnTerminateAllThreads = new JButton("Terminate All Threads");
		btnTerminateAllThreads.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				workerPool.terminateAllThreads();
				refreshWokerPool(workerPool);
			}
		});
		btnTerminateAllThreads.setBounds(579, 262, 172, 29);
		contentPane.add(btnTerminateAllThreads);
		

		// JLabel lblSize = new JLabel("Size:");
		// lblSize.setBounds(142, 266, 40, 16);
		// contentPane.add(lblSize);
		//
		// sizeField = new JTextField();
		// sizeField.setBounds(178, 261, 40, 26);
		// sizeField.setText(new Integer(workerPoolSize).toString());
		// contentPane.add(sizeField);
		// sizeField.setColumns(10);
		//
		// JButton resizeBtn = new JButton("Resize");
		// resizeBtn.addActionListener(new ActionListener() {
		// public void actionPerformed(ActionEvent e) {
		// workerPoolSize = new Integer(sizeField.getText());
		// workerPool.resize(workerPoolSize);
		// refreshWokerPool(workerPool);
		// if (comboBox != null) {
		// String[] threadsCount = new String[workerPoolSize];
		// for (int i = 0; i < workerPoolSize; i++) {
		// threadsCount[i] = new Integer(i + 1).toString();
		// }
		// comboBox.setModel(new DefaultComboBoxModel(threadsCount));
		// }
		// }
		// });
		// resizeBtn.setBounds(213, 260, 80, 29);
		// contentPane.add(resizeBtn);

		RefreshTimer refreshTimer = new RefreshTimer(workerPool);
		refreshTimer.execute();
	}

	private void refreshWokerPool(WorkerPool workerPool) {
		if (workerPoolTable != null) {
			workerPoolTable.setVisible(false);
		}
		JSONObject[] threadsStatus = workerPool.getThreadsStatus();

		String tableRows[] = { "No.", "Thread Name", "State" };
		String tableColunms[][] = new String[threadsStatus.length][tableRows.length];
		for (int i = 0; i < threadsStatus.length; i++) {
			tableColunms[i][0] = new Integer(i).toString();
			tableColunms[i][1] = (String) threadsStatus[i].get("name");
			tableColunms[i][2] = (String) threadsStatus[i].get("status");

		}

		workerPoolTable = new JTable(tableColunms, tableRows);
		workerPoolTable.setBounds(50, 292, 700, 261);
		workerPoolTable.setEnabled(false);

		scrollpane = new JScrollPane(workerPoolTable);
		scrollpane.setBounds(50, 292, 700, 261);
		scrollpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		workerPoolTable.setVisible(true);
		contentPane.add(scrollpane);
	}

	private void updateTextArea(final String text) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				infoArea.append(text);
			}
		});
	}

	private void redirectSystemStreams() {
		OutputStream out = new OutputStream() {
			@Override
			public void write(int b) throws IOException {
				updateTextArea(String.valueOf((char) b));
			}

			@Override
			public void write(byte[] b, int off, int len) throws IOException {
				updateTextArea(new String(b, off, len));
			}

			@Override
			public void write(byte[] b) throws IOException {
				write(b, 0, b.length);
			}
		};

		System.setOut(new PrintStream(out, true));
		System.setErr(new PrintStream(out, true));
	}

	// Run the core server code
	private class ServerRunner extends SwingWorker<NullType, NullType> {
		public ServerRunner(DictionaryServer server) {
			this.server = server;
		}

		@Override
		protected NullType doInBackground() throws Exception {
			server.runServer();
			return null;
		}

		private DictionaryServer server;
	}

	// Refresh the worker pool table
	private class RefreshTimer extends SwingWorker<NullType, NullType> {
		public RefreshTimer(WorkerPool workerPool) {
			this.workerPool = workerPool;
		}

		@Override
		protected NullType doInBackground() throws Exception {
			while (true) {
				// Refresh every 0.5s
				Thread.sleep(500);
				refreshWokerPool(workerPool);

			}
		}

		private WorkerPool workerPool;
	}
}
