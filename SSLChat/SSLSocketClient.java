
import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.event.*;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.swing.*;
import java.io.*;
import java.net.*;

class SSLSocketClient extends JFrame implements ActionListener {
	JTextArea text;
	JLabel clicked;
	JButton button;
	JPanel panel;
	JTextField textField;
	JLabel history;
	SSLSocket socket = null;
	PrintWriter out = null;
	BufferedReader in = null;

	SSLSocketClient() { // Begin Constructor
		text = new JTextArea("");
		text.setEditable(false);
		textField = new JTextField(20);
		button = new JButton("Click Me");
		button.addActionListener(this);
		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setBackground(Color.white);
		getContentPane().add(panel);
		panel.add("North", text);
		panel.add("Center", textField);
		panel.add("South", button);
	} // End Constructor

	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();
		Thread t = new Listener();
		t.start();
		if (source == button) {
			// Send data over socket
			String message = textField.getText();
			out.println(message);
			textField.setText(new String(""));
			text.append("You: " + message + "\n");
			// Receive text from server
		}
	}

	private class Listener extends Thread {
		@Override
		public void run() {
			while (true) {
				try {
					String line = in.readLine();
					text.append("Other: " + line + "\n");
					System.out.println("Text received: " + line);
				} catch (IOException e) {
					System.out.println("Read failed");
					System.exit(1);
				}
			}
		}
	}

	public void listenSocket() {
		// Create socket connection
		try {
			// socket = new Socket("kq6py", 4444);
			// SSLsocket = new Socket("localhost", 4444);
			SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory
					.getDefault();
			SSLSocket sslsocket = (SSLSocket) sslsocketfactory.createSocket(
					"localhost", 4444);
			socket = sslsocket;
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket
					.getInputStream()));
		} catch (UnknownHostException e) {
			System.out.println("Unknown host: kq6py.eng");
			System.exit(1);
		} catch (IOException e) {
			System.out.println("No I/O");
			System.exit(1);
		}
	}

	public static void main(String[] args) {
		SSLSocketClient frame = new SSLSocketClient();
		frame.setTitle("Client Program");
		WindowListener l = new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		};
		frame.addWindowListener(l);
		frame.pack();
		frame.setVisible(true);
		frame.listenSocket();
	}
}
