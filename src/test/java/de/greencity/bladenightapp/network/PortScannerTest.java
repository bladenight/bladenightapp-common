package de.greencity.bladenightapp.network;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.greencity.bladenightapp.network.scanner.PortKnocker;
import de.greencity.bladenightapp.network.scanner.PortScanner;
import de.greencity.bladenightapp.network.scanner.PortScanner.PortKnockerFactory;

public class PortScannerTest {

    static class MyPortKnocker extends PortKnocker {

        @Override
        public boolean knock() {
            if ( "open".equals(host) )
                return true;
            if ( "192.168.178.236".equals(host) )
                return true;
            return false;
        }
    }
    static class MyKnockerFactory implements PortScanner.PortKnockerFactory {
        @Override
        public PortKnocker createKnocker() {
            return new MyPortKnocker();
        }
    }

    static private PortKnockerFactory factory;
    private PortScanner scanner;

    @BeforeClass
    static public void createKnockerFactory() {
        factory = new MyKnockerFactory();
    }

    @Before
    public void createScanner() {
        scanner = new PortScanner(12345);
        scanner.setKnockerFactory(factory);
    }

    @Test
    public void singleMatchFromExplicitHost() throws InterruptedException {
        scanner.addHost("host1");
        scanner.addHost("open");
        scanner.addHost("host2");
        scanner.scan();
        assertEquals("open", scanner.getFoundHost());
    }


    @Test
    public void singleMatchFromSubnet() throws InterruptedException {
        scanner.addHost("host1");
        scanner.addHost("host2");
        scanner.addHost("host3");
        scanner.addIpRange("192.168.178", 1, 254);
        scanner.scan();
        assertEquals("192.168.178.236", scanner.getFoundHost());
    }

    @Test
    public void noMatchShallBeReturned() throws InterruptedException {
        scanner.addHost("host1");
        scanner.addHost("host2");
        scanner.addHost("host3");
        scanner.addIpRange("192.168.0", 1, 254);
        scanner.scan();
        assertEquals(null, scanner.getFoundHost());
    }

}
