package de.greencity.bladenightapp.keyvaluestore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.logging.impl.NoOpLog;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class KeyValueStoreTest {

	private KeyValueStore store;

	@BeforeClass
	static public void initializeClass() {
		KeyValueStore.setLog(new NoOpLog());
	}

	@Before
	public void initialize() {
		store = new KeyValueStore() {
			Map<String, String> map = new HashMap<String, String>();
			@Override
			public boolean writeExternalResource(String identifier) {
				return false;
			}
			@Override
			public boolean readExternalResource(String identifier) {
				return false;
			}
			
			@Override
			public void setString(String key, String value) {
				map.put(key, value);
			}
			
			@Override
			public String getString(String key) {
				return map.get(key);
			}
		}; 
		
	}

	@Test
	public void testStrings() {
		String key = "key " + UUID.randomUUID().toString();
		String value = "value " + UUID.randomUUID().toString();
		store.setString(key, value);
		assertEquals(value, store.getString(key));

		assertNull(store.getString("invalid key"));
		String defaultValue = "default value " + UUID.randomUUID().toString();
		assertEquals(defaultValue, store.getString("invalid key", defaultValue));
	}

	@Test(expected=IllegalArgumentException.class)
	public void testGetNonNullString() {
		assertNull(store.getNonNullString("invalid key"));
	}
	
}
