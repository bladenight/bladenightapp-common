package de.greencity.bladenightapp.routes;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RouteStore {

	private File baseDirDirectory;

	public RouteStore(File baseDirDirectory) {
		setBaseDirectory(baseDirDirectory);
	}

	public void setBaseDirectory(File baseDir) {
		this.baseDirDirectory = baseDir;
	}

	public File getBaseDirectory() {
		return baseDirDirectory;
	}

	public List<String> getAvailableRoutes() {
		List<String> list = new ArrayList<String>();

		for (final File fileEntry : baseDirDirectory.listFiles()) {
			if ( isValidRouteFile(fileEntry)) {
				String name = getNameFromPath(fileEntry.getAbsolutePath());
				list.add(name);
			}
		}
		return list;
	}

	public Route getRoute(String routeName) {
		File file = getFileFromRouteName(routeName);
		if ( file == null) {
			getLog().warn("Could not find the file for a route named \""+routeName+"\"");
			return null;
		}
		Route route = new Route();
		if ( ! route.load(file) ) {
			getLog().warn("Could not load route from file \""+file.getAbsolutePath()+"\"");
			return null;
		}
		route.setName(routeName);
		return route;
	}

	private File getFileFromRouteName(String routeName) {
		File[] fileList = baseDirDirectory.listFiles();
		if ( fileList == null ) {
			getLog().error("Could not retrieve route files. Does the directory exist ? \n" + baseDirDirectory);
			return null;
		}
		// Scan the directory for a file with a matching name
		for (final File fileEntry : fileList) {
			if ( isValidRouteFile(fileEntry) && routeName.equals(getNameFromPath(fileEntry.getAbsolutePath()) ) )
				return fileEntry;
		}
		return null;
	}

	private String getNameFromPath(String path) {
		return FilenameUtils.getBaseName(path);
	}

	private boolean isValidRouteFile(File file) {
		if ( ! file.isFile() )
			return false;

		String extension = FilenameUtils.getExtension(file.getName());
		// TODO: ask the class Route what suffixes are supported
		return extension.equals("kml");
	}

	private static Log log;

	public static void setLog(Log log) {
		RouteStore.log = log;
	}

	protected static Log getLog() {
		if (log == null)
			setLog(LogFactory.getLog(RouteStore.class));
		return log;
	}

}
