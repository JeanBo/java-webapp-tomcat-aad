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
import za.co.liberty.dto.transaction.RecordPolicyInfoDTO;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.exceptions.security.TabAccessException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.agreements.PolicyInfoKindType;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.businesscard.model.BusinessCardPageModel;
import za.co.liberty.web.pages.businesscard.model.MaintainBusinessCardPanelModel;
import za.co.liberty.web.pages.request.BaseRequestViewAndAuthorisePanel;
import za.co.liberty.web.pages.request.tree.RequestTreePanel;
import za.co.liberty.web.pages.request.tree.model.RequestTreePanelModel;
import za.co.liberty.web.pages.transactions.model.PolicyTransactionModel;
import za.co.liberty.web.system.SRSAuthWebSession;

/**
 * Authorisation panel for transactions
 * 
 * @author jzb0608 21/07/2016
 * 
 *
 */
public class PolicyTransactionsAuthorisationPanel extends BaseRequestViewAndAuthorisePanel {
	
	private static final long serialVersionUID = 1L;
	//private static Logger logger = Logger.getLogger(MaintainBusinessCardAuthorisationPanel.class);
	
	private PolicyTransactionModel authModel;
	
	/**
	 * Default constructor 
	 * 
	 * @param id
	 * @param pageModel
	 * @param editState
	 * @throws TabAccessException 
	 */
	public PolicyTransactionsAuthorisationPanel(String id, 
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
			
			
			// Now lets add the property panel
			RequestTreePanelModel treeModel = new RequestTreePanelModel();
			treeModel.setRequestNo(getPageModel().getViewRequestContextDto().getRequestDto().getRequestId());
			panelList.add(new RequestTreePanel(id, treeModel ));
			
			
		}else if(requestKindList.contains(RequestKindType.RecordPolicyInfo)){
			PolicyTransactionModel model = new PolicyTransactionModel();
			authModel = model;
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
			
			// Now lets add the property panel
			RequestTreePanelModel treeModel = new RequestTreePanelModel();
			treeModel.setRequestNo(getPageModel().getViewRequestContextDto().getRequestDto().getRequestId());
			panelList.add(new RequestTreePanel(id, treeModel ));
		}
		
		return panelList;
	}
	
	/**
	 * Don't auto expand for the tree view.
	 */
	public boolean isShowPanelType(Class<? extends Panel> class1) {
		if (class1!=null && class1.equals(RequestTreePanel.class)) {
			return false;
		}
		return true;
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
	
	
	/**
	 * Show an additional message for commission calculations after authorisation.
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void doAuthoriseRequest(List<?> selectList) throws ValidationException {
		super.doAuthoriseRequest(selectList);
		
		// Check if the calculations would have updated
		if(getPageModel() != null && getPageModel().getCurrentImage() instanceof IPolicyTransactionDTO) {
			IPolicyTransactionDTO dto = (IPolicyTransactionDTO) getPageModel().getCurrentImage();
			
			if (dto instanceof RecordPolicyInfoDTO && ((RecordPolicyInfoDTO)dto).getInfoKindType() != null) {
				
				PolicyInfoKindType t = PolicyInfoKindType.getPolicyInfoKindWithType(((RecordPolicyInfoDTO)dto).getInfoKindType());
				
				if (t!=null && t.hasCommissionCalculation()) {
					info("Close and reload the page to view the calculation results."); 
					// Tsts
				}
			}
			
		}

	}

}