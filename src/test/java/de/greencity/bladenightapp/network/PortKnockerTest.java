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
	static final String nonExistingDomain = "invalid-domain-851719812873t7381.de";
	static final String openHost = "open-host." + nonExistingDomain;
	static final int openPort = 12345;
	static final String closedHost = "closed-host." + nonExistingDomain;
	static final int closedPort = 12346;
	static final String unreachableHost = "unreachable-host." + nonExistingDomain;
	static final int unreachablePort = 12347;

	static class MySocket extends Socket {
		@Override
		public void connect(SocketAddress endpoint, int timeout)
				throws IOException {
			myIsConnected = false;
			if ( endpoint.equals(new InetSocketAddress(openHost,openPort)) ) {
				myIsConnected = true;
			}
			else if ( endpoint.equals(new InetSocketAddress(closedHost,closedPort)) ) {
				throw new IOException("Port is close");
			}
			else if ( endpoint.equals(new InetSocketAddress(unreachableHost,unreachablePort)) ) {
				throw new SocketTimeoutException("Host unreachable");
			}
			else {
				throw new IllegalArgumentException("Don't know this host: " + endpoint);
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
		knocker.setHost(closedHost);
		boolean result = knocker.knock();
		assertFalse("knock() should fail for closed ports", result);
	}

	@Test
	public void portClosed() {
		knocker.setHost(closedHost);
		knocker.setPort(closedPort);
		boolean result = knocker.knock();
		assertFalse("knock() should fail for closed ports", result);
	}

	@Test
	public void portUnreachable() {
		knocker.setHost(unreachableHost);
		knocker.setPort(unreachablePort);
		boolean result = knocker.knock();
		assertFalse("knock() should fail for unreachable ports", result);
	}

	@Test
	public void portOpen() {
		knocker.setHost(openHost);
		knocker.setPort(openPort);
		boolean result = knocker.knock();
		assertTrue("knock() should succeed", result);
	}
}
