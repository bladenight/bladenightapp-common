package de.greencity.bladenightapp.tracking;

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

public class TraceLoggerTest {
	@Test
	public void testOptionalTimestamp() throws IOException {
		File tmpFolder = Files.createTemporaryFolder();
		File traceFile = new File(tmpFolder, "geo-trace-optional-timestamp.txt");
		
		TraceLogger traceLogger = new TraceLogger(traceFile);
		
		double latitude = 48.133333;
		double longitude = 11.566667;
		double accuracy = 12.0;
		double linearPosition = 7200.0;

		traceLogger.setTimestamp(new DateTime(2012, 02, 15, 18, 30));
		traceLogger.setLatitude(latitude);
		traceLogger.setLongitude(longitude);
		traceLogger.setAccuracy(accuracy);
		traceLogger.setLinearPosition(linearPosition);
		
		traceLogger.write();
		
		traceLogger.setLatitude(latitude);
		traceLogger.setLongitude(longitude);
		traceLogger.setAccuracy(2 * accuracy);
		traceLogger.setLinearPosition(linearPosition);

		traceLogger.write();

		List<String> lines = FileUtils.readLines(traceFile);
		
		assertEquals(2, lines.size());

		String matchLine0 = "ts=2012-02-15T18:30:00.000\\+[0-9]{2}:[0-9]{2}	la=48.133333	lo=11.566667	lp=7200.0	ac=12.0";
		assertTrue(lines.get(0).matches(matchLine0));

		String matchLine1 = "ts="+ new DateTime().getYear() + "-.*	la=48.133333	lo=11.566667	lp=7200.0	ac=24.0";
		assertTrue(lines.get(1).matches(matchLine1));
	}

	@Test
	public void testWriteWithTimeLimit() throws IOException, InterruptedException {
		File tmpFolder = Files.createTemporaryFolder();
		File traceFile = new File(tmpFolder, "geo-trace-timelimit.txt");
		
		TraceLogger traceLogger = new TraceLogger(traceFile);
		
		double latitude = 48.133333;
		double longitude = 11.566667;
		double accuracy = 12.0;
		double linearPosition = 7200.0;
		
		long timeLimitInMs = 50;

		traceLogger.setLatitude(latitude);
		traceLogger.setLongitude(longitude);
		traceLogger.setLinearPosition(linearPosition);
		
		traceLogger.setAccuracy(accuracy);
		traceLogger.writeWithTimeLimit(timeLimitInMs);
		
		assertEquals(1, FileUtils.readLines(traceFile).size());
		
		traceLogger.setAccuracy(2 * accuracy);
		traceLogger.writeWithTimeLimit(timeLimitInMs);

		assertEquals(1, FileUtils.readLines(traceFile).size());

		Sleep.sleep(timeLimitInMs+1);
		
		traceLogger.setAccuracy(3 * accuracy);
		traceLogger.writeWithTimeLimit(timeLimitInMs);

		assertEquals(2, FileUtils.readLines(traceFile).size());
	}


}
