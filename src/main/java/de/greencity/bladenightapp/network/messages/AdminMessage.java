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

	public AdminMessage() {
	}

	public AdminMessage(String password) {
		setTimestamp(getClock().currentTimeMillis());
		setNoise(generateNoise());
		authenticate(password);
	}

	public boolean authenticate(String password) {
		setTimestamp(getClock().currentTimeMillis());
		setNoise(generateNoise());
		String checksum = generateChecksum(password);
		setChecksum(checksum);
		return ! "".equals(getChecksum());
	}

	private long generateNoise() {
		return Math.abs(getRandom().nextLong());
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
		String currentChecksum = getChecksum();
		if ( "".equals(referenceChecksum)  ) {
			getLog().warn("Failed to generate current checksum: " + this.toString());
			return false;
		}
		if ( "".equals(referenceChecksum)  ) {
			getLog().warn("Failed to generate reference checksum: " + this.toString());
			return false;
		}
		if ( ! referenceChecksum.equals(currentChecksum) ) {
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
		if ( clock == null )
			clock = new SystemClock();
		return clock;
	}

	public void setClock(Clock clock) {
		this.clock = clock;
	}

	public Random getRandom() {
		if ( random == null )
			random = new Random();
		return random;
	}

	public void setRandom(Random random) {
		this.random = random;
	}


	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public long tim = -1;
	public String chk = "";
	public long noi = -1;
	private transient Clock clock;
	private transient Random random;

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
