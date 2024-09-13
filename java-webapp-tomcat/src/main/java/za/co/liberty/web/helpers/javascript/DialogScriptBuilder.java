package za.co.liberty.web.helpers.javascript;

/**
 * <p>Helps building the required javascript for invoking a javscript
 * dialog.</p>
 * 
 * <p><b>Note</b> the script code is currently included in the base page
 * and thus available for most pages.
 * <br/>TODO jzb0608 - should move into it's own component</p>
 * 
 * @author Jean Bodemer (JZB0608) - 30 Sep 2008
 *
 */
public class DialogScriptBuilder extends ScriptBuilder {

	/**
	 * Type of available dialogs
	 */
	public enum DialogType {
		WARNING("Warning"), ERROR("Error"), SUCCESS("Success"), PROMPT("Prompt");
		
		private String title;
		
		DialogType(String title) {
			this.title = title;
		}
	
		public String getTitle() {
			return title;
		}
	};
	
	public static void main(String [] args) {
		System.out.println(buildShowDialog(DialogType.WARNING, "Bla bla bla"));
	}
	
	/**
	 * Build the show dialog script to call the "showDialog" function.
	 * 
	 * @param type
	 * @param message
	 * @return
	 */
	public static String buildShowDialog(DialogType type, String message) {
		return buildShowDialog(type, message, type.getTitle(), false);
	}
	
	/**
	 * Build the show dialog script to call the "showDialog" function.
	 * 
	 * @param type
	 * @param message
	 * @param title
	 * @return
	 */
	public static String buildShowDialog(DialogType type, String message, String title) {
		return buildShowDialog(type, message, title, false);
	}
	
	/**
	 * Build the show dialog script to call the "showDialog" function.
	 * 
	 * @param type
	 * @param message
	 * @param autoHide
	 * @return
	 */
	public static String buildShowDialog(DialogType type, String message, boolean autoHide) {
		return buildShowDialog(type, message, type.getTitle(), autoHide);
	}
	
	/**
	 * Build the show dialog script to call the "showDialog" function.
	 * 
	 * @param type
	 * @param message
	 * @param title
	 * @param autoHide
	 * @return
	 */
	public static String buildShowDialog(DialogType type, String message, String title, boolean autoHide) {
		StringBuilder build = new StringBuilder();
		build.append("showDialog('");
		build.append(title);
		build.append("','");
		build.append(message);
		build.append("','");
		build.append(type.toString().toLowerCase());
		build.append("'");
		if (autoHide) {
			build.append(",true");
		}
		build.append(");");
		return build.toString();	
	}
}