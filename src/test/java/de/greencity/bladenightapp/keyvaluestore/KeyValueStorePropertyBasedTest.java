package de.greencity.bladenightapp.keyvaluestore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.text.ParseException;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.impl.NoOpLog;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.greencity.bladenightapp.events.EventsList;

public class KeyValueStorePropertyBasedTest {
	private static final String NON_EXISTING_KEY = "non-existing.key";
	static private KeyValueStorePropertyBased store;

	@BeforeClass
	static public void initializeClass() {
		KeyValueStore.setLog(new NoOpLog());

		store = new KeyValueStorePropertyBased(); 
		File file = FileUtils.toFile(EventsList.class.getResource("/de.greencity.bladenightapp.keyvaluestore/sample.properties"));
		assertTrue(store.readExternalResource(file.getAbsolutePath()));
		
	}

	@Before
	public void initialize() {
	}

	@Test
	public void testPrimitives() {
		assertEquals(123456, store.getInt("some.int", 0));
		assertEquals(42, store.getInt("non-existing.int", 42));

		assertEquals(12345678910L, store.getLong("some.long", 0));
		assertEquals(42L, store.getLong("non-existing.long", 42L));

	}

	@Test
	public void testRelativePath() {
		String path = store.getPath("some.relpath");
		assertNotNull(path);
		assertFalse("The path is relative, so it shall be prefixed with the base path", "dir1/file".equals(path) );
		assertTrue("The path is relative, so it shall be prefixed with the base path", path.endsWith("dir1/file"));
	}

	@Test
	public void testDefaultRelativePath() {
		String defaultValue = "somedir/" + UUID.randomUUID().toString();
		String path = store.getPath(NON_EXISTING_KEY, defaultValue);
		assertNotNull(path);
		assertFalse("The path is relative, so it shall be prefixed with the base path", defaultValue.equals(path) );
		assertTrue("The path is relative, so it shall be prefixed with the base path", path.endsWith(defaultValue));
	}

	@Test
	public void testAbsolutePaths() {
		String path = store.getPath("some.abspath");
		assertEquals("/dir2/file", path);
	}
	@Test
	public void testDefaultAbsolutePath() {
		String defaultValue = "/somedir/" + UUID.randomUUID().toString();
		String path = store.getPath(NON_EXISTING_KEY, defaultValue);
		assertEquals(defaultValue, path);
	}

	@Test
	public void testDouble() {
		double value = store.getDouble("some.double", 0.0);
		assertEquals(3.141592, value, 0);
	}
	@Test
	public void testDefaultDouble() {
		double defaultValue = 42.42;
		double value = store.getDouble(NON_EXISTING_KEY, defaultValue);
		assertEquals(defaultValue, value, 0);
	}

	@Test
	public void testDate() throws IllegalArgumentException, ParseException {
		Date value = store.getDate("some.date");
		assertTrue(value.before(new Date()));
	}

	@Test
	public void testDefaultDate() throws IllegalArgumentException, ParseException {
		Date value = store.getDate(NON_EXISTING_KEY, "2040-01-01 10:10");
		assertTrue(value.after(new Date()));
	}
}
