package de.greencity.bladenightapp.valuelogger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.junit.Test;

import de.greencity.bladenightapp.testutils.Files;
import de.greencity.bladenightapp.time.Sleep;

public class ValueLoggerTest {
	@Test
	public void testOptionalTimestamp() throws IOException {
		File traceFile = traceFile("geo-trace-optional-timestamp.txt");
		ValueLogger valueLogger = new ValueLogger(traceFile);
		
		double latitude = 48.133333;
		double longitude = 11.566667;
		double accuracy = 12.0;
		double linearPosition = 7200.0;

		valueLogger.setTimestamp(new DateTime(2012, 02, 15, 18, 30));
		valueLogger.setValue("la", Double.toString(latitude));
		valueLogger.setValue("lo", Double.toString(longitude));
		valueLogger.setValue("ac", Double.toString(accuracy));
		valueLogger.setValue("lp", Double.toString(linearPosition));
		
		valueLogger.write();
		
		valueLogger.setValue("ac", Double.toString(2*accuracy));

		valueLogger.write();

		List<String> lines = FileUtils.readLines(traceFile);
		
		assertEquals(2, lines.size());

		String matchLine0 = "ts=2012-02-15T18:30:00.000\\+[0-9]{2}:[0-9]{2}	ac=12.0	la=48.133333	lo=11.566667	lp=7200.0";
		assertTrue(lines.get(0).matches(matchLine0));

		String matchLine1 = "ts="+ new DateTime().getYear() + "-.*	ac=24.0	la=48.133333	lo=11.566667	lp=7200.0";
		assertTrue(lines.get(1).matches(matchLine1));
	}

	@Test
	public void testWriteWithTimeLimit() throws IOException, InterruptedException {
		File traceFile = traceFile("geo-trace-optional-timelimit.txt");
		
		ValueLogger valueLogger = new ValueLogger(traceFile);
		
		double latitude = 48.133333;
		double longitude = 11.566667;
		double accuracy = 12.0;
		double linearPosition = 7200.0;
		
		long timeLimitInMs = 50;

		valueLogger.setTimestamp(new DateTime(2012, 02, 15, 18, 30));
		valueLogger.setValue("la", Double.toString(latitude));
		valueLogger.setValue("lo", Double.toString(longitude));
		valueLogger.setValue("ac", Double.toString(accuracy));
		valueLogger.setValue("lp", Double.toString(linearPosition));
		
		valueLogger.writeWithTimeLimit(timeLimitInMs);
		
		assertEquals(1, FileUtils.readLines(traceFile).size());
		
		valueLogger.setValue("ac", Double.toString( 2 * accuracy));
		valueLogger.writeWithTimeLimit(timeLimitInMs);

		assertEquals(1, FileUtils.readLines(traceFile).size());

		Sleep.sleep(timeLimitInMs+1);
		
		valueLogger.setValue("ac", Double.toString( 3 * accuracy));
		valueLogger.writeWithTimeLimit(timeLimitInMs);

		assertEquals(2, FileUtils.readLines(traceFile).size());
	}

	@Test(expected=IllegalArgumentException.class)
	public void invalidKey() throws IOException {
		ValueLogger valueLogger = new ValueLogger(traceFile("invalid-key.txt"));
		valueLogger.setValue(ValueLogger.TIMESTAMP_KEY, "test");
	}

	private File traceFile(String filename) throws IOException {
		File tmpFolder = Files.createTemporaryFolder();
		File traceFile = new File(tmpFolder, filename);
		return traceFile;
	}

}
