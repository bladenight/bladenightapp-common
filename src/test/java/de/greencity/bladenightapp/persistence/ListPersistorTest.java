package de.greencity.bladenightapp.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.logging.impl.NoOpLog;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.gson.Gson;

public class ListPersistorTest {

	@Before
	public void init() {
		ListPersistor.setLog(new NoOpLog());
	}

	public class MyListItem implements ListItem {
		public MyListItem(String s, int i) {
			this.s  = s;
			this.i = i;
		}
		public String s;
		public int i;
		@Override
		public String getPersistenceId() {
			return s + "-" + i;
		}
		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}
		@Override
		public boolean equals(Object o) {
			return EqualsBuilder.reflectionEquals(this, o);
		}
	};

	@Test
	public void writeSingleEntryFromList() throws IOException {
		List<MyListItem> list = new ArrayList<MyListItem>();
		File directory = createDirectory("writeSingleEntryFromList");
		ListPersistor<MyListItem> persistor = new ListPersistor<MyListItem>(MyListItem.class);
		persistor.setDirectory(directory);
		persistor.setList(list);

		assertEquals(0, directory.listFiles().length);

		MyListItem item = new MyListItem("string",42); 
		list.add(item);

		persistor.write();

		assertEquals(1, directory.listFiles().length);
		String fileContent = FileUtils.readFileToString(new File(directory,item.getPersistenceId()+".per"));
		MyListItem crossCheck = new Gson().fromJson(fileContent, MyListItem.class);
		assertEquals(item, crossCheck);
	}

	@Test(expected=IllegalStateException.class)
	public void writeConflictingEntries() throws IOException {
		List<MyListItem> list = new ArrayList<MyListItem>();
		File directory = createDirectory("writeSingleEntry");
		ListPersistor<MyListItem> persistor = new ListPersistor<MyListItem>(MyListItem.class);
		persistor.setDirectory(directory);
		persistor.setList(list);

		MyListItem item = new MyListItem("string",42); 
		list.add(item);
		list.add(item);

		persistor.write();

	}

	@Test
	public void writeMultipleItems() throws IOException {
		List<MyListItem> list = new ArrayList<MyListItem>();
		File directory = createDirectory("writeSingleEntry");
		ListPersistor<MyListItem> persistor = new ListPersistor<MyListItem>(MyListItem.class);
		persistor.setDirectory(directory);
		persistor.setList(list);

		list.add(new MyListItem("string",42));
		list.add(new MyListItem("string",43));

		persistor.write();

		assertEquals(2, directory.listFiles().length);
	}

	@Test
	public void readFromDir() throws IOException, InconsistencyException {
		File dir = FileUtils.toFile(ListPersistorTest.class.getResource("/de.greencity.bladenightapp.persistence/mylistitems"));

		List<MyListItem> list = new ArrayList<MyListItem>();
		ListPersistor<MyListItem> persistor = new ListPersistor<MyListItem>(MyListItem.class);
		persistor.setDirectory(dir);
		persistor.setList(list);

		list.add(new MyListItem("to-be-removed-from-the-list", 10));

		persistor.read();

		assertEquals(2, list.size());
		assertEquals(new MyListItem("string1", 1), list.get(0));
		assertEquals(new MyListItem("string2", 2), list.get(1));
	}

	@Test
	public void readFromDirTwice() throws IOException, InconsistencyException {
		File dir = FileUtils.toFile(ListPersistorTest.class.getResource("/de.greencity.bladenightapp.persistence/mylistitems"));

		List<MyListItem> list = new ArrayList<MyListItem>();
		ListPersistor<MyListItem> persistor = new ListPersistor<MyListItem>(MyListItem.class);
		persistor.setDirectory(dir);
		persistor.setList(list);

		persistor.read();
		persistor.read();

		assertEquals(2, list.size());
	}

	@Test(expected=IllegalStateException.class)
	public void readDiscrepency() throws IOException, InconsistencyException {
		File dir = FileUtils.toFile(ListPersistorTest.class.getResource("/de.greencity.bladenightapp.persistence/discrepency"));

		List<MyListItem> list = new ArrayList<MyListItem>();
		ListPersistor<MyListItem> persistor = new ListPersistor<MyListItem>(MyListItem.class);
		persistor.setDirectory(dir);
		persistor.setList(list);

		persistor.read();

		assertEquals(2, list.size());
	}

	@Test(expected=IOException.class)
	public void readInvalidSyntax() throws IOException, InconsistencyException {
		File dir = FileUtils.toFile(ListPersistorTest.class.getResource("/de.greencity.bladenightapp.persistence/invalidsyntax"));

		List<MyListItem> list = new ArrayList<MyListItem>();
		ListPersistor<MyListItem> persistor = new ListPersistor<MyListItem>(MyListItem.class);
		persistor.setDirectory(dir);
		persistor.setList(list);

		persistor.read();

		assertEquals(0, list.size());
	}


	@Test
	public void deleteDeprecatedItems() throws IOException, InconsistencyException {
		List<MyListItem> list = new ArrayList<MyListItem>();
		File directory = createDirectory("deleteDeprecatedItems");
		ListPersistor<MyListItem> persistor = new ListPersistor<MyListItem>(MyListItem.class);
		persistor.setDirectory(directory);
		persistor.setList(list);

		list.add(new MyListItem("string",42));
		MyListItem toBeDeleted1 = new MyListItem("to-be-deleted-1",43); 
		list.add(toBeDeleted1);
		MyListItem toBeDeleted2 = new MyListItem("to-be-deleted-2",43); 
		list.add(toBeDeleted2);

		persistor.write();

		assertEquals(3, directory.listFiles().length);

		list.remove(toBeDeleted1);

		persistor.write();

		assertEquals(2, directory.listFiles().length);

		persistor.read();

		assertTrue(! list.contains(toBeDeleted1));

		list.remove(toBeDeleted2);

		persistor.write(toBeDeleted2);

		assertEquals(1, directory.listFiles().length);

		persistor.read();

		assertTrue(! list.contains(toBeDeleted2));
	}

	private File createDirectory(String name) throws IOException {
		return folder.newFolder(name);
	}

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
}
