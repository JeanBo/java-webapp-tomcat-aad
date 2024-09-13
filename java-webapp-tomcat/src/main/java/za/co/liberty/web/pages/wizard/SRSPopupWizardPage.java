package za.co.liberty.web.pages.wizard;

import org.apache.wicket.Page;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;

import za.co.liberty.web.pages.BaseWindowPage;

/**
 * Popup Page used to display a wizard
 * 
 * @author DZS2610
 *
 */
public class SRSPopupWizardPage extends BaseWindowPage {
	
	private static final long serialVersionUID = 1L;
	
	private ModalWindow parentWindow;	
	private SRSPopupWizard wizard;
	private Page parentPage;	
	private String pageName;
	
	
	public SRSPopupWizardPage(String pageName, SRSPopupWizard wizard, ModalWindow modalWindow, Page parentPage){
		this.parentWindow = modalWindow;
		this.parentPage = parentPage;
		this.wizard = wizard;
		this.pageName = pageName;
		if(wizard != null){
			add(wizard);   
		}		
	}
	
	
	@Override
	public String getPageName() {		
		return pageName;
	}	
	
	public boolean isShowFeedBackPanel() {
		return false;
	}
}
