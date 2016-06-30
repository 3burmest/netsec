//http://java.sun.com/developer/onlineTraining/Programming/BasicJava2/socket.html#server

import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.event.*;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.swing.*;
import java.io.*;
import java.net.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class SSLSocketServer extends JFrame implements ActionListener {
	JButton button;
	JLabel label = new JLabel("Text received over socket:");
	JPanel panel;
	JTextArea textArea = new JTextArea();
	SSLServerSocket server = null;
	SSLSocket client;
	List<PrintWriter> outs;
	String line;
	Map<InetAddress, Long> _clientAddresses;

	SSLSocketServer() { // Begin Constructor
		button = new JButton("Click Me");
		button.addActionListener(this);
		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setBackground(Color.white);
		getContentPane().add(panel);
		panel.add("North", label);
		panel.add("Center", textArea);
		panel.add("South", button);
		outs = new ArrayList<PrintWriter>();
		_clientAddresses = new HashMap<InetAddress, Long>();
	} // End Constructor

	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();
		if (source == button) {
			textArea.setText(line);
		}
	}

	public void listenSocket() {
		try {
			// server = new ServerSocket(4444);
			SSLServerSocketFactory sslserversocketfactory = (SSLServerSocketFactory) SSLServerSocketFactory
					.getDefault();
			SSLServerSocket sslserversocket = (SSLServerSocket) sslserversocketfactory
					.createServerSocket(4444);
			// SSLSocket sslsocket = (SSLSocket) sslserversocket.accept();
			server = sslserversocket;
		} catch (IOException e) {
			System.out.println("Could not listen on port 4444");
			System.exit(-1);
		}
		while (true) {
			try {
				client = (SSLSocket) server.accept();
				Thread t = new listener(client, outs);
				t.start();
			} catch (IOException e) {
				System.out.println("Accept failed: 4444");
				System.exit(-1);
			}
		}
		/*
		 * try{ in = new BufferedReader(new
		 * InputStreamReader(client.getInputStream())); out.add(new
		 * PrintWriter(client.getOutputStream(), true)); } catch (IOException e)
		 * { System.out.println("Accept failed: 4444"); System.exit(-1); }
		 */
		/*
		 * while(true){ try{ line = in.readLine(); //Send data back to client
		 * out.println(line); } catch (IOException e) { System.out.println(
		 * "Read failed"); System.exit(-1); } }
		 */
	}

	private class listener extends Thread {
		Socket client;
		BufferedReader in = null;
		List<PrintWriter> outs;
		String line;

		public listener(Socket client, List<PrintWriter> outs) {
			this.client = client;
			this.outs = outs;
		}

		@Override
		public void run() {
			PrintWriter out = null;
			try {
				in = new BufferedReader(new InputStreamReader(client
						.getInputStream()));
				out = new PrintWriter(client.getOutputStream(), true);

				outs.add(out);
			} catch (IOException e) {
				System.out.println("Accept failed: 4444");
				return;
			}
			while (true) {
				try {
					line = in.readLine();
					// Send data back to client
					for (PrintWriter o : outs) {
						if (!out.equals(o)) {
							if (line != null) {
								o.println(line);
							} else {
								break;
							}
						}
					}
				} catch (IOException e) {
					System.out.println("Read failed");
					System.exit(-1);
				}
			}
		}

		@Override
		protected void finalize() throws Throwable {
			in.close();
			client.close();
		}
	}

	protected void finalize() {
		// Clean up
		try {
			server.close();
		} catch (IOException e) {
			System.out.println("Could not close.");
			System.exit(-1);
		}
	}

	public static void main(String[] args) {
		SSLSocketServer frame = new SSLSocketServer();
		frame.setTitle("Server Program");
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
