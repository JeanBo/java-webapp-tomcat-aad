package za.co.liberty.web.system;

import org.apache.wicket.core.util.resource.locator.ResourceStreamLocator;
import org.apache.wicket.util.resource.IResourceStream;

import za.co.liberty.web.pages.BasePage;

/**
 * <p>This class helps with the location of resources and has enabled
 * this application to find resources in non default locations.</p> 
 *
 */
public class PathLocator extends ResourceStreamLocator {

	/**
	 * The root path that is used when searching for resources. 
	 */
	public static String basePath = null;
	
	static {
		// Initialise static variable
		
		String str = BasePage.class.getName().replaceAll("\\.", "/");
		basePath = str.substring(0, str.lastIndexOf("/"))+"/";
	}

	public PathLocator() {
		
	}

	@Override
	public IResourceStream locate(final Class clazz, final String path) {
		IResourceStream located = super.locate(clazz, trimFolders(path));
		if (located != null) {
//			System.out.println("#JB - FOUND clazz=" + clazz.toString()
//					+ ";  path=" + path);
			return located;
		}
//		System.out.println("   #JB - not located  clazz=" + clazz.toString()
//				+ ";  path=" + path);
		return super.locate(clazz, path);
	}

	/**
	 * Determine the folder to search from
	 * 
	 * @param path
	 * @return
	 */
	private String trimFolders(String path) {
//		System.out.println("#JB trimfolders - path=" + path + ";  subString="
//				+ path.replace(basePath, "") 
//				+ "    - Basepath = :"+basePath+":");
		path = path.replace(basePath, "");

		return path;
	}
}