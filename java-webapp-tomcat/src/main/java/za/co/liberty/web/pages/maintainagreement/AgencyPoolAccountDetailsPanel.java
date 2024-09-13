/**
 * 
 */
package za.co.liberty.web.pages.maintainagreement;

import java.util.List;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

import za.co.liberty.business.guicontrollers.agreement.IAgreementGUIController;
import za.co.liberty.dto.agreement.AgreementDTO;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.enums.fields.AgreementGUIField;
import za.co.liberty.web.pages.interfaces.ISecurityPanel;
import za.co.liberty.web.pages.maintainagreement.model.AgencyPoolAccountDetailsPanelModel;
import za.co.liberty.web.pages.panels.ViewTemplateBasePanel;
import za.co.liberty.web.wicket.view.ContextDrivenViewTemplate;

/**
 * Panel for Agency Advisor Pool Account screen
 * 
 * @author zzt2108
 * 
 */
public class AgencyPoolAccountDetailsPanel extends
		ViewTemplateBasePanel<AgreementGUIField, AgreementDTO> implements
		ISecurityPanel {

	private static final long serialVersionUID = 1L;

	private transient IAgreementGUIController guiController;

	private AgencyPoolAccountDetailsPanelModel panelModel;
	// protected MaintainAgreementPageModel pageModel;
	// private MaintainAgreementPageModel pageModel;

	private AgencyPoolAccountForm pageForm;
	private AgencyIntoPoolAccountPanel intoPoolAccountPanel;
	private AgencyPoolDrawPanel poolDrawPanel;
	private StopAgencyPoolTransferPanel closeAgencyPoolPanel;

	private boolean initialised;

	private Page parentPage;
	private EditStateType editState;

	private boolean existingAgencyPoolRequest;
	private boolean existingStopIntoPoolRequest;
	private boolean existingPoolDrawRequest;
	private boolean existingSetIntoPoolOverrideRequest;
	private boolean existingCloseAgencyPoolRequest;

	private static final Logger logger = Logger
			.getLogger(AgencyPoolAccountDetailsPanel.class);

	public AgencyPoolAccountDetailsPanel(String id, EditStateType editState) {
		super(id, editState);
	}

	public AgencyPoolAccountDetailsPanel(String id,
			AgencyPoolAccountDetailsPanelModel panelModel,
			FeedbackPanel feedBackPanel, EditStateType editState,
			Page parentPage) {		
		super(id, editState, parentPage);
		this.panelModel = panelModel;
		
	}

	public AgencyPoolAccountDetailsPanel(String id, EditStateType editState2,
			AgencyPoolAccountDetailsPanelModel panelModel2) {
		super(id, editState2);
		this.panelModel = panelModel2;
	}

	@Override
	protected void onBeforeRender() {
		if (!initialised) {
			initialised = true;
			// initialize the page model with the agreement data
			// initPageModel();
			List<RequestKindType> unAuthRequests = getOutStandingRequestKinds();
			// check for existing requests FIRST as other panels use variables
			// set here
			for (RequestKindType kind : unAuthRequests) {

				if (kind == RequestKindType.SetOverrideIntoPoolRate) {
					existingSetIntoPoolOverrideRequest = true;
				}

				if (kind == RequestKindType.StopAgencyPoolTransfer) {
					existingStopIntoPoolRequest = true;
				}

				if (kind == RequestKindType.AdhocAgencyPoolDraw) {
					existingPoolDrawRequest = true;
				}

				if (kind == RequestKindType.CloseAgencyPool) {
					existingCloseAgencyPoolRequest = true;
				}

				if (kind == RequestKindType.SetOverrideIntoPoolRate
						|| kind == RequestKindType.StopAgencyPoolTransfer
						|| kind == RequestKindType.AdhocAgencyPoolDraw
						|| kind == RequestKindType.CloseAgencyPool) {
					existingAgencyPoolRequest = true;
					break;
				}
			}
			
			

			add(getAgencyPoolPageForm());
			

		}

		super.onBeforeRender();
	};

	// private void initPageModel() {
	// if(pageModel == null){
	// error("Page Model should never be null, Please call support if you continue seeing this error");
	// pageModel = new MaintainAgreementPageModel(new AgreementDTO(),new
	// ValidAgreementValuesDTO());
	// }
	// if(pageModel.getMaintainAgreementDTO() == null){
	// error("An agreement needs to be selected to adjust the Associated Agreements");
	// pageModel.setMaintainAgreementDTO(new MaintainAgreementDTO());
	// }
	// if(pageModel.getMaintainAgreementDTO().getAgreementDTO() == null){
	// error("An agreement needs to be selected to adjust the Associated Agreements");
	// pageModel.getMaintainAgreementDTO().setAgreementDTO(new AgreementDTO());
	// }
	// }

	// public void setPageModel(MaintainAgreementPageModel pageModel) {
	// this.panelModel = new AgencyPoolAccountDetailsPanelModel(pageModel);
	// }

	/**
	 * Load the AgreementGUIController dynamically if it is null as this is a
	 * transient variable.
	 * 
	 * @return {@link IAgreementGUIController}
	 */
	private IAgreementGUIController getGuiController() {
		if (guiController == null) {
			try {
				guiController = ServiceLocator
						.lookupService(IAgreementGUIController.class);
			} catch (NamingException e) {
				logger.fatal("Could not lookup AgreementGUIController", e);
				throw new CommunicationException(
						"Could not lookup AgreementGUIController", e);
			}
		}
		return guiController;
	}

	public class AgencyPoolAccountForm extends Form {

		private static final long serialVersionUID = 1L;

		public AgencyPoolAccountForm(String id) {
			super(id);

			// Check of the maximum Pool draw have been made
			boolean hasMaxPoolDraw = getGuiController().hasPoolDraw(
					panelModel.getAgreementId());
			if (hasMaxPoolDraw && getEditState() != EditStateType.AUTHORISE) {
				AgencyPoolAccountDetailsPanel.this
						.warn("This agreement has had 2 or more pool draws in the current business year.");
			}
			// Disable IntoPool elements for advisers having tenures G and I.
			if (checkIsClosedPool()
					&& getEditState() != EditStateType.AUTHORISE) {
				AgencyPoolAccountDetailsPanel.this
						.info("The Agency Pool Account is closed.");
				add(getIntoPoolAccountPanel().setEnabled(false));
				add(getAgencyPoolDrawPanel().setEnabled(false));
				add(getCloseAgencyPoolPanel().setEnabled(false));
			} else if (!checkTenureValidity()
					&& getEditState() != EditStateType.AUTHORISE) {

				add(getIntoPoolAccountPanel().setEnabled(false));
				AgencyPoolAccountDetailsPanel.this
						.info("Transfers into pool cannot be performed for advisers having tenure (G,I)");
				add(getAgencyPoolDrawPanel());
				add(getCloseAgencyPoolPanel());
			} else if (isStopIntoPool()
					&& getEditState() != EditStateType.AUTHORISE) {
				add(getIntoPoolAccountPanel().setEnabled(false));
				AgencyPoolAccountDetailsPanel.this
						.info("Transfers into pool cannot be performed.");
				add(getAgencyPoolDrawPanel());
				add(getCloseAgencyPoolPanel());
			} else {
				add(getIntoPoolAccountPanel());
				add(getAgencyPoolDrawPanel());
				add(getCloseAgencyPoolPanel());
			}

		}

	}

	private Component getAgencyPoolPageForm() {
		if (pageForm == null) {
			pageForm = new AgencyPoolAccountForm("agencyPoolAccountForm");
		}
		return pageForm;
	}

	// /**
	// * initialise any additional data required by the panel model
	// *
	// */
	// private void initPageModel() {
	// if (pageModel == null) {
	// error("Page Model should never be null, Please call support if you continue seeing this error");
	// pageModel = new MaintainAgreementPageModel(new AgreementDTO(),
	// new ValidAgreementValuesDTO());
	// }
	// if (pageModel.getMaintainAgreementDTO() == null) {
	// error("An agreement needs to be selected to view the Agency Pool Details");
	// pageModel.setMaintainAgreementDTO(new MaintainAgreementDTO());
	// }
	// if (pageModel.getMaintainAgreementDTO().getAgreementDTO() == null) {
	// error("An agreement needs to be selected to view the Agency Pool Details");
	// pageModel.getMaintainAgreementDTO().setAgreementDTO(
	// new AgreementDTO());
	// }
	//
	// if (pageModel.getMaintainAgreementDTO().getAgreementDTO()
	// .getAgencyPoolAccountDetailDTO() == null) {
	// error("An agreement needs to be selected to view the Agency Pool Details");
	// pageModel
	// .getMaintainAgreementDTO()
	// .getAgreementDTO()
	// .setAgencyPoolAccountDetailDTO(
	// new AgencyPoolAccountDetailDTO());
	// }
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see za.co.liberty.web.pages.interfaces.ISecurityPanel#getPanelClass()
	 */
	public Class getPanelClass() {
		return AgencyPoolAccountDetailsPanel.class;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * za.co.liberty.web.pages.panels.ViewTemplateBasePanel#getViewTemplate()
	 */
	@Override
	protected ContextDrivenViewTemplate getViewTemplate() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * za.co.liberty.web.pages.panels.ViewTemplateBasePanel#getViewTemplateContext
	 * ()
	 */
	@Override
	protected AgreementDTO getViewTemplateContext() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return the intoPoolAccountPanel
	 */
	public AgencyIntoPoolAccountPanel getIntoPoolAccountPanel() {

		if (intoPoolAccountPanel == null) {

			intoPoolAccountPanel = new AgencyIntoPoolAccountPanel(
					"agencyPoolAccountIntoPool", panelModel,
					AgencyPoolAccountDetailsPanel.this.getEditState(),
					parentPage);
		}

		if (existingSetIntoPoolOverrideRequest
				|| existingCloseAgencyPoolRequest) {
			intoPoolAccountPanel.setEnabled(false);
		}
		return intoPoolAccountPanel;
	}

	public AgencyPoolDrawPanel getAgencyPoolDrawPanel() {
		if (poolDrawPanel == null) {
			poolDrawPanel = new AgencyPoolDrawPanel("agencyPoolDraw",
					this.getEditState(), panelModel, getParentPage(),
					existingAgencyPoolRequest);
		}
		
		if (existingPoolDrawRequest || existingCloseAgencyPoolRequest) {
			poolDrawPanel.setEnabled(false);
		}
		return poolDrawPanel;
	}

	public StopAgencyPoolTransferPanel getCloseAgencyPoolPanel() {
		if (closeAgencyPoolPanel == null) {
			closeAgencyPoolPanel = new StopAgencyPoolTransferPanel(
					"stopAgencyPoolTransfer", this.getEditState(), panelModel,
					getParentPage());
		}

		if (existingStopIntoPoolRequest || existingCloseAgencyPoolRequest) {
			closeAgencyPoolPanel.setEnabled(false);
		}
		return closeAgencyPoolPanel;
	}
	
	private boolean checkTenureValidity() {
		
		if (panelModel.getAgencyPoolAccountDetailDTO() != null
				&& panelModel.getAgencyPoolAccountDetailDTO().getTenure() != null) {
			if (panelModel.getAgencyPoolAccountDetailDTO().getTenure()
					.equalsIgnoreCase("G")
					|| panelModel.getAgencyPoolAccountDetailDTO()
							.getTenure().equalsIgnoreCase("I")) {
				return false;
			}
		}
		return true;
	}
	
	private boolean checkIsClosedPool() {

		if (panelModel.getAgencyPoolAccountDetailDTO() != null
				&& panelModel.getAgencyPoolAccountDetailDTO().getCloseAgencyPool() != null) {
			if (panelModel.getAgencyPoolAccountDetailDTO().getCloseAgencyPool()) {
				return true;
			}
		}
		return false;
	}

	private boolean isStopIntoPool() {
		if (panelModel.getAgencyPoolAccountDetailDTO() != null
				&& panelModel.getAgencyPoolAccountDetailDTO()
						.getStopIntoPoolTransfers() != null
				&& panelModel.getAgencyPoolAccountDetailDTO()
						.getStopIntoPoolTransfers()) {
			return true;
		}
		return false;
	}
	
	
	
	/**
	 * Override the modify access behavior. Can modify the screen only if the
	 * Pool is not closed
	 */
	public boolean hasModifyAccess(boolean originalAccess) {

		if (checkIsClosedPool()) {
			return false;
		}
		return originalAccess;
	}

}
