package za.co.liberty.web.pages.core;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;

import za.co.liberty.dto.agreement.core.CoreTransferDto;
import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.error.request.RequestException;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.persistence.core.ICoreEntityManager;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.system.SRSAuthWebSession;

public class ContractTransferPage extends AdstractTransferPage {

	protected String pageName = "Contract Transfer";
	private transient ICoreEntityManager coreEntityManager;

	@Override
	public Panel createContainerPanel() {
		Panel panel = null;
		try {
			//setEditState(EditStateType.MODIFY,null);
			
			FeedbackPanel feedbackPanel = getFeedbackPanel();
			panel = new ContractTransferPanel(CONTAINER_PANEL_NAME, pageModel,
					EditStateType.MODIFY, feedbackPanel, this);
		} catch (Exception e) {
			error(e.getMessage());
			panel = new EmptyPanel(CONTAINER_PANEL_NAME);
		}

		panel.setOutputMarkupId(true);
		return panel;
	}

	@Override
	public String getPageName() {
		return pageName;
	}
	


	@Override
	public void doSave_onSubmit() {
		/*boolean res = feedbackPanel.anyErrorMessage();*/
		if (new CoreHelper().validateContractGrid(pageModel, this.getFeedbackPanel())){ 
			raiseRequests(SRSAuthWebSession.get().getCurrentUserid()) ;
		}
	}

	
	private void raiseRequests(String userId) {
		ISessionUserProfile userProfile = SRSAuthWebSession.get().getSessionUser();
		Long partyOid = pageContextDTO!=null && pageContextDTO.getPartyContextDTO()!=null
							?pageContextDTO.getPartyContextDTO().getPartyOid():null;
		try{					
			
			List<CoreTransferDto> coreTransferList= pageModel.getCoreTransferDto();
			for (CoreTransferDto coreTransferDto : coreTransferList) {
				coreTransferDto.setRequester(userId);
				coreTransferDto.setRowStatus(true);
			getGUIController().raiseCoreTransferRequest(
						userProfile, partyOid , 
						coreTransferDto,getAllowableRequestsForContractTransfer());
			}
		getSession().info("Record was saved succesfully");
		setResponsePage(new ContractTransferPage());
		}catch(ValidationException e){
			for(String error : e.getErrorMessages()){
				getFeedbackPanel().error(error);
			}
		} catch (DataNotFoundException e) {
			getFeedbackPanel().error("Data not found when trying to raise a request ");
			getSession().error("Data not found when trying to raise a request ");
	//		setResponsePage(new ContractTransferPage());
		} catch (RequestException e) {
			getFeedbackPanel().error("Could not raise the request " + e.getMessage());
			getSession().error("Could not raise the request " + e.getMessage());
	//		setResponsePage(new ContractTransferPage());
		}
				
		
	}
	
	
	private Set<RequestKindType> getAllowableRequestsForContractTransfer() {
		Set<RequestKindType> ret = new HashSet<RequestKindType>();
	
		ret.add(RequestKindType.ProcessContractTransfer);
		return ret;
	}
	
}
