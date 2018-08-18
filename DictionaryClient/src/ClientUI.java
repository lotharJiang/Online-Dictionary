import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.json.JSONObject;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextPane;
import javax.swing.JLabel;
import javax.swing.UIManager;

public class ClientUI extends JFrame {

//	private JPanel contentPane;
//	private JTextField wordField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				String hostName = "";
				int port = 0;
				// Object that will store the parsed command line arguments
				CmdLineArgs argsBean = new CmdLineArgs();

				// Parser provided by args4j
				CmdLineParser parser = new CmdLineParser(argsBean);
				try {

					// Parse the arguments
					parser.parseArgument(args);

					// After parsing, the fields in argsBean have been updated
					// with the
					// given
					// command line arguments
					hostName = argsBean.getHost();
					port = argsBean.getPort();

				} catch (CmdLineException e) {

					System.err.println(e.getMessage());

					// Print the usage to help the user understand the arguments
					// expected by the program
					parser.printUsage(System.err);
				}

				final DictionaryClient client = new DictionaryClient(hostName, port);
				ClientUI frame = new ClientUI(client);
				frame.setVisible(true);

			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ClientUI(DictionaryClient client) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		JTextField wordField = new JTextField();
		wordField.setBounds(124, 29, 229, 30);
		wordField.setColumns(10);
		JTextPane meaningPane = new JTextPane();
		meaningPane.setBounds(19, 71, 413, 133);
		meaningPane.setEditable(false);
		contentPane.add(meaningPane);
		JLabel informationLabel = new JLabel("");
		informationLabel.setBounds(19, 216, 413, 30);
		contentPane.add(informationLabel);
		JComboBox comboBox = new JComboBox();
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				wordField.setText("");
				meaningPane.setText("");
				if (comboBox.getSelectedItem().equals("Add")) {
					meaningPane.setEditable(true);
				} else {
					meaningPane.setEditable(false);
				}
			}
		});
		comboBox.setModel(new DefaultComboBoxModel(new String[] { "Query", "Add", "Remove" }));
		comboBox.setBounds(19, 29, 110, 30);
		contentPane.add(comboBox);

		JButton btnNewButton = new JButton("Go");
		btnNewButton.setBounds(351, 30, 81, 31);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String message = "";
				String operation = (String) comboBox.getSelectedItem();
				String word = wordField.getText();
				if (word.equals("")) return;
				String meaning = meaningPane.getText();
				if(operation.equalsIgnoreCase("add")&&meaning.equals("")){
					informationLabel.setForeground(UIManager.getColor("CheckBox.select"));
					informationLabel.setText("Please enter meaning before adding entry into dictionary.");
					return;				
				}
				message = client.sendRequest(operation, word, meaning);
				JSONObject messageJson = new JSONObject(message);
				String messageType = (String) messageJson.get("messageType");
				if(!messageType.equals("0")){
					String argument = (String) messageJson.get("argument");
					JSONObject responseJson = new JSONObject(argument);
					String responseStatus = (String) responseJson.get("status");
					String responseMessage = (String) responseJson.get("result");
					String responseInfo = (String) responseJson.get("info");
					if (responseStatus.equals("001")) {
						informationLabel.setForeground(UIManager.getColor("infoText"));
					} else {
						informationLabel.setForeground(UIManager.getColor("CheckBox.select"));
					}
					informationLabel.setText(responseInfo);
					meaningPane.setText(responseMessage);
				}
				

			}
		});

		contentPane.setLayout(null);
		contentPane.add(wordField);
		contentPane.add(btnNewButton);

	}
}
