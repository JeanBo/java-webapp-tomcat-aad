package za.co.liberty.web.pages.maintainagreement;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.NamingException;

import org.apache.wicket.Page;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.panel.EmptyPanel;

import za.co.liberty.business.guicontrollers.agreement.IAgreementGUIController;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.BasePage;
import za.co.liberty.web.pages.maintainagreement.model.MaintainAgreementPageModel;


public class SIMSLBFAddPage   extends BasePage{
	
	private ModalWindow mw;
	
	private transient IAgreementGUIController guiController;
	
	private transient Logger logger = Logger.getLogger(SIMSLBFAddPage.class.getName());
	
	private MaintainAgreementPageModel pageModel;
	
	private EditStateType editState;
	
	private String pageName = "LBF ADD HOME ROLE PAGE";
	
	private AgreementHierarchyLBFPanel agreementHierarchyLBFPanel;
	
	private Page parentPage;	
	
	public SIMSLBFAddPage(String id, MaintainAgreementPageModel pageModel, EditStateType editState,			
		Page parentPage , ModalWindow mw){
		this.mw = mw;
		this.pageModel = pageModel;
		this.editState = editState;
		this.parentPage = parentPage;
		if(editState == EditStateType.ADD){
			//initPageModel();
		}
		else if(editState == EditStateType.MODIFY){
			try {
				processDeferredLoading();
			} catch (DataNotFoundException e) {
				logger.log(Level.SEVERE,"Error occured during process if Deferred Loading of Hierarchy roles",e);
			}
		}

		 agreementHierarchyLBFPanel= new AgreementHierarchyLBFPanel(id,this.pageModel, this.editState, null, parentPage, mw);
		 agreementHierarchyLBFPanel.setOutputMarkupId(true);

		
		add(agreementHierarchyLBFPanel);
		 
	}
	private void processDeferredLoading() throws DataNotFoundException {
		RequestKindType[] requestKindTypes = {RequestKindType.MaintainAgreementHierarchy, RequestKindType.MaintainAgreementHome};
		if (requestKindTypes!=null) {
			getGUIController().loadDeferredDataForRequest(
					pageModel.getMaintainAgreementDTO().getAgreementDTO(),
					pageModel.getPreviousMaintainAgreementDTO().getAgreementDTO(),
					pageModel.getValidAgreementValues(),
					requestKindTypes);
		}
	}
	
	private IAgreementGUIController getGUIController() {
		if (guiController==null) {
			/**
			 * Load agreement controller
			 */
			try {
				guiController = ServiceLocator.lookupService(IAgreementGUIController.class);
			} catch (NamingException e) {
				logger.log(Level.SEVERE,"Naming exception looking up Agreement GUI Controller",e);
				throw new CommunicationException("Naming exception looking up Agreement GUI Controller",e);
			}
		}
		return guiController;
	}

	@Override
	public String getPageName() {
		return pageName;
	}
	

}

