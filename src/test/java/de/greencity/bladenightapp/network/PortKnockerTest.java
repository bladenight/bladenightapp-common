package de.greencity.bladenightapp.network;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.greencity.bladenightapp.network.scanner.PortKnocker;

public class PortKnockerTest {
	PortKnocker knocker;
	static PortKnocker.SocketFactory socketFactory;

	static class MySocket extends Socket {
		@Override
		public void connect(SocketAddress endpoint, int timeout)
				throws IOException {
			myIsConnected = false;
			if ( endpoint.equals(new InetSocketAddress("open",12345)) ) {
				myIsConnected = true;
			}
			else if ( endpoint.equals(new InetSocketAddress("close",12345)) ) {
				throw new IOException("Port is close");
			}
			else {
				throw new SocketTimeoutException("Host unreachable");
			}
		}
		
		@Override
		public boolean isConnected() {
			return myIsConnected;
		}

		boolean myIsConnected = false;
	}
	
	static class MyFactory implements PortKnocker.SocketFactory {
		public Socket createSocket() {
			return new MySocket();
		}
	}
	
	@BeforeClass
	static public void beforeClass() {
		socketFactory = new MyFactory(); 
	}
	
	@Before
	public void before() {
		knocker = new PortKnocker();
		knocker.setSocketFactory(socketFactory);
	}
	
	@Test(expected=java.lang.IllegalStateException.class)
	public void noPort() {
		knocker.setHost("close");
		boolean result = knocker.knock();
		assertFalse("knock() should fail for closed ports", result);
	}

	@Test
	public void portClosed() {
		knocker.setHost("close");
		knocker.setPort(12345);
		boolean result = knocker.knock();
		assertFalse("knock() should fail for closed ports", result);
	}

	@Test
	public void portUnreachable() {
		knocker.setHost("unreachable");
		knocker.setPort(12345);
		boolean result = knocker.knock();
		assertFalse("knock() should fail for unreachable ports", result);
	}

	@Test
	public void portOpen() {
		knocker.setHost("open");
		knocker.setPort(12345);
		boolean result = knocker.knock();
		assertTrue("knock() should succeed", result);
	}
}
