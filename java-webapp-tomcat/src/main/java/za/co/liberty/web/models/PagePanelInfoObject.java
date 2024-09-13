package za.co.liberty.web.models;

import java.io.Serializable;

/**	
 * This Object will contain info about a panels that the implementation page uses<br/>
 *
 * @author DZS2610
 *
 */
public class PagePanelInfoObject implements Serializable{
	private String clazzName;
	private int tabIndexOfPanel;
	private String panelName;	

	public PagePanelInfoObject(String clazzName,int tabIndexOfPanel,String panelName){
		this.clazzName = clazzName;
		this.tabIndexOfPanel = tabIndexOfPanel;
		this.panelName = panelName;
	}
	
	public String getPanelName() {
		return panelName;
	}

	public void setPanelName(String panelName) {
		this.panelName = panelName;
	}
	
	public String getClazzName() {			
		return clazzName;
	}
	public void setClazzName(String clazzName) {
		this.clazzName = clazzName;
	}
	public int getTabIndexOfPanel() {
		return tabIndexOfPanel;
	}
	public void setTabIndexOfPanel(int tabIndexOfPanel) {
		this.tabIndexOfPanel = tabIndexOfPanel;
	}		
}