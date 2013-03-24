package de.greencity.bladenightapp.network.scanner;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/***
 * Tells if a given port on a given host is reachable and open 
 *
 */
public class PortKnocker {

	public interface SocketFactory {
		public Socket createSocket();
	}
	public class DefaultSocketFactory implements SocketFactory {
		public Socket createSocket() {
			return new Socket();
		}
	}

	public PortKnocker() {

	}

	public PortKnocker(String host, int port, int timeout) {
		this.host = host;
		this.port = port;
		this.timeout = timeout;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getHost() {
		return host;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean knock() {
		if ( port == 0 )
			throw new IllegalStateException("Port is not set");

		Socket socket = factory.createSocket();
		try {
			//			System.out.println("Knocking at " + host);
			InetSocketAddress address = new InetSocketAddress(host, port);
			socket.connect(address, timeout);
			if ( socket.isConnected() ) {
				return true;
			}
		}
		catch (IOException e) {
			// Timeout or port closed
		}
		finally {
			try {
				socket.close();
			} catch (IOException e) {
			}
		}
		return false;
	}

	public void setSocketFactory(SocketFactory socketFactory) {
		this.factory = socketFactory;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout; 
	}

	protected String host = null;
	protected int port = 0;
	protected SocketFactory factory = new DefaultSocketFactory();
	protected int timeout = 10000;
}
