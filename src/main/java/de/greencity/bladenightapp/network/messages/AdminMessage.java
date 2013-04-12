package de.greencity.bladenightapp.network.messages;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.greencity.bladenightapp.time.Clock;
import de.greencity.bladenightapp.time.SystemClock;

public class AdminMessage {

	public boolean authenticate(String password) {
		setTimestamp(getClock().currentTimeMillis());
		setNoise(generateNoise());
		String checksum = generateChecksum(password);
		setChecksum(checksum);
		return ! "".equals(getChecksum());
	}

	private long generateNoise() {
		return Math.abs(random.nextLong());
	}

	private String generateChecksum(String password) {
		String msg = password + getTimestamp() + getNoise();
		try {
			return checksum(msg);
		} catch (NoSuchAlgorithmException e) {
			getLog().error("Failed to generate checksum:", e);
			return "";
		} catch (UnsupportedEncodingException e) {
			getLog().error("Failed to generate checksum:", e);
			return "";
		}
	}

	public boolean verify(String password, long maxAge) {
		long now = getClock().currentTimeMillis();
		if ( getTimestamp() > now ) {
			getLog().warn("Timestamp is in the future: " + this.toString());
			return false;
		}
		if ( now - getTimestamp() > maxAge ) {
			getLog().warn("Message expired: " + this.toString());
			return false;
		}
		String referenceChecksum = generateChecksum(password);
		if ( ! referenceChecksum.equals(getChecksum()) ) {
			getLog().warn("Message verification failed: " + this.toString());
			return false;
		}
		return true;
	}

	public long getTimestamp() {
		return tim;
	}
	public void setTimestamp(long timestamp) {
		this.tim = timestamp;
	}
	public String getChecksum() {
		return chk;
	}
	public void setChecksum(String checksum) {
		this.chk = checksum;
	}
	public long getNoise() {
		return noi;
	}
	public void setNoise(long noise) {
		this.noi = noise;
	}

	private static String checksum(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest digest = MessageDigest.getInstance( "SHA-1" );
		byte[] bytes = text.getBytes("UTF-8");
		digest.update(bytes, 0, bytes.length);
		bytes = digest.digest();
		StringBuilder sb = new StringBuilder();
		for( byte b : bytes ) {
			sb.append( String.format("%02x", b) );
		}
		return sb.toString();
	}

	public Clock getClock() {
		return clock;
	}

	public void setClock(Clock clock) {
		this.clock = clock;
	}

	public Random getRandom() {
		return random;
	}

	public void setRandom(Random random) {
		this.random = random;
	}


	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public long tim;
	public String chk;
	public long noi;
	private transient Clock clock = new SystemClock();
	private transient Random random = new Random();

	private static Log log;

	public static void setLog(Log log) {
		AdminMessage.log = log;
	}

	protected static Log getLog() {
		if (log == null)
			setLog(LogFactory.getLog(AdminMessage.class));
		return log;
	}

}
