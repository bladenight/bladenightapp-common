package de.greencity.bladenightapp.protocol;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ProtocolTest {

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();
    
	@Test
	public void test() throws IOException {
		File tempFile = testFolder.newFile("protocol.txt");
		Protocol protocol = new Protocol(tempFile);
		String tag1 = "MYTAG1";
		String tag2 = "MYTAG2";
		String msg = "My message";
		protocol.write(tag1, tag2, msg);
		protocol.close();

		 List<String> lines = FileUtils.readLines(tempFile, "UTF-8");
		 assertEquals(1, lines.size());

		 Pattern p = Pattern.compile("^([0-9]+)\t(.*)\t(.*)\t(.*)");
		 Matcher m = p.matcher(lines.get(0));

		 assertEquals(true, m.find());
		 assertEquals(tag1, m.group(2));
		 assertEquals(tag2, m.group(3));
		 assertEquals(msg, m.group(4));
	}
}
