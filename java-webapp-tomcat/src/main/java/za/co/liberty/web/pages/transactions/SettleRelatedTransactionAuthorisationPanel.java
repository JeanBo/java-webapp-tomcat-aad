package za.co.liberty.web.pages.transactions;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import org.apache.wicket.markup.html.panel.Panel;

import za.co.liberty.business.guicontrollers.transactions.IPolicyTransactionGuiController;
import za.co.liberty.database.enums.DatabaseEnumHelper;
import za.co.liberty.dto.agreement.request.RequestEnquiryRowDTO;
import za.co.liberty.dto.businesscard.BusinessCardDetailsDTO;
import za.co.liberty.dto.databaseenum.LanguagePreferenceDBEnumDTO;
import za.co.liberty.dto.gui.request.ViewRequestModelDTO;
import za.co.liberty.dto.transaction.IPolicyTransactionDTO;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.exceptions.security.TabAccessException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.businesscard.model.BusinessCardPageModel;
import za.co.liberty.web.pages.businesscard.model.MaintainBusinessCardPanelModel;
import za.co.liberty.web.pages.request.BaseRequestViewAndAuthorisePanel;
import za.co.liberty.web.pages.transactions.model.PolicyTransactionModel;


/**
 * This panel will show all settle related request in a special view to expand on the default properties view.
 * 
 * TODO For now only the VAT request is catered for
 * 
 * @author jzb0608
 *
 */
public class SettleRelatedTransactionAuthorisationPanel extends BaseRequestViewAndAuthorisePanel {
	
	private static final long serialVersionUID = 1L;
	//private static Logger logger = Logger.getLogger(MaintainBusinessCardAuthorisationPanel.class);
	
	/**
	 * Default constructor 
	 * 
	 * @param id
	 * @param pageModel
	 * @param editState
	 * @throws TabAccessException 
	 */
	public SettleRelatedTransactionAuthorisationPanel(String id, 
			ViewRequestModelDTO viewRequestPageModel) {
		super(id, viewRequestPageModel);	
	}

	/**
	 * Initialise the panels required.
	 * 
	 */
	@Override
	public List<Panel> createPanels(String id, Object imageObject) {
		List<RequestKindType> requestKindList = getPageModel().getRequestKindList();
		List<Panel> panelList = new ArrayList<Panel>();
		
		// Process Party hierarchy Details
		if (requestKindList.contains(RequestKindType.DistributePolicyEarning)) {
			PolicyTransactionModel model = new PolicyTransactionModel();
			model.setSelectedObject((IPolicyTransactionDTO) imageObject);
			try {
				ServiceLocator.lookupService(IPolicyTransactionGuiController.class).initialisePageModel(model);
			} catch (NamingException e) {
				throw new CommunicationException(e);
			}
			panelList.add(new PolicyTransactionDPEPanel(id,EditStateType.AUTHORISE, model, null, false));
		}else if(requestKindList.contains(RequestKindType.RecordPolicyInfo)){
			PolicyTransactionModel model = new PolicyTransactionModel();
			model.setSelectedObject((IPolicyTransactionDTO) imageObject);
			try {
				IPolicyTransactionGuiController controller = ServiceLocator.lookupService(IPolicyTransactionGuiController.class);
				controller.initialisePageModel(model);
				List<RequestEnquiryRowDTO> list = getPageModel().getRequestEnquiryRowList();
				if (list.size()==1) {
					// Check if there was a comm calc request for this
					model.setSelectedPolicyInfoCalculation(
							controller.initialisePolicyInfoCalculation(list.get(0).getRequestId()));
				}
			} catch (NamingException e) {
				throw new CommunicationException(e);
			}
			panelList.add(new PolicyTransactionInfoPanel(id,EditStateType.AUTHORISE, model, null, false));
		}
		
		return panelList;
	}
	
	
	/**
	 * Initialise the model for businesscard
	 * 
	 * @param currentImage
	 * @return
	 */
	protected BusinessCardPageModel initialiseBusinessCardPageModel(Object imageObject) {
		BusinessCardPageModel model = new BusinessCardPageModel();
		if(imageObject instanceof BusinessCardDetailsDTO){
			BusinessCardDetailsDTO bc = (BusinessCardDetailsDTO)imageObject;
			MaintainBusinessCardPanelModel panelModel = new MaintainBusinessCardPanelModel();
			panelModel.setBusinessCardDetails(bc);
			model.setMaintainBusinessCardPanelModel(panelModel);	
			panelModel.setAllSpokenLanguages(DatabaseEnumHelper.getDatabaseDTO(LanguagePreferenceDBEnumDTO.class,
					true,true, false, false));
		}
		return model;
	}

}