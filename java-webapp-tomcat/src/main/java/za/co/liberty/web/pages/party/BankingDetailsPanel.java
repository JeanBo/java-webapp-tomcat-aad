package za.co.liberty.web.pages.party;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.naming.NamingException;

import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;//org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow.MaskType;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.IFormValidator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.convert.IConverter;

import za.co.liberty.business.guicontrollers.agreement.IAgreementGUIController;
import za.co.liberty.business.guicontrollers.partymaintenance.IPartyMaintenanceController;
import za.co.liberty.business.party.IValidPartyValuesFactory;
import za.co.liberty.dto.agreement.AgreementRoleDTO;
import za.co.liberty.dto.agreement.properties.BankingDetailsHistoryDTO;
import za.co.liberty.dto.party.EmployeeDTO;
import za.co.liberty.dto.party.OrganisationDTO;
import za.co.liberty.dto.party.bankingdetail.BankBranchDTO;
import za.co.liberty.dto.party.bankingdetail.BankNameDTO;
import za.co.liberty.dto.party.bankingdetail.BankVerificationRulesDTO;
import za.co.liberty.dto.party.bankingdetail.BankingDetailsDTO;
import za.co.liberty.dto.party.bankingdetail.BankingDetailsSource;
import za.co.liberty.dto.party.bankingdetail.BankingVerificationResponseDTO;
import za.co.liberty.dto.party.bankingdetail.MaintainBankingDetailResultDTO;
import za.co.liberty.dto.party.bankingdetail.type.AVSStatusType;
import za.co.liberty.dto.party.bankingdetail.type.AccountHolderRelType;
import za.co.liberty.dto.party.bankingdetail.type.AccountType;
import za.co.liberty.dto.party.bankingdetail.type.ActionType;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.interfaces.party.BankingDetailType;
import za.co.liberty.web.constants.SRSAppWebConstants;
import za.co.liberty.web.data.enums.ComponentType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.interfaces.IHasAccessPanel;
import za.co.liberty.web.pages.interfaces.ISecurityPanel;
import za.co.liberty.web.pages.panels.BasePanel;
import za.co.liberty.web.pages.panels.GUIFieldPanel;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.pages.party.model.BankingDetailsPanelModel;
import za.co.liberty.web.pages.party.model.MaintainPartyPageModel;
import za.co.liberty.web.system.SRSAuthWebSession;
import za.co.liberty.web.wicket.markup.html.form.SRSDropDownChoice;

/**
 * Banking details panel.
 * 
 * @author Pritam
 * @author JZB0608 - 25/03/2011 - Modify to show the banking details of the national code if there
 * 			is one.
 *
 */
public class BankingDetailsPanel extends BasePanel implements ISecurityPanel, IHasAccessPanel{

	private static final long serialVersionUID = 1L;
	
	private MaintainPartyPageModel pageModel;
	private FeedbackPanel feedBackPanel;
	@SuppressWarnings("unused")
	private BankingDetailsForm form;
	
	private WebMarkupContainer paysToContainer;
	
	private WebMarkupContainer bankBranchContainer;

	private WebMarkupContainer accountContainer;
	
	private transient IValidPartyValuesFactory validPartyValuesFactory;
	
	private transient IAgreementGUIController guiController;
	
	private transient IPartyMaintenanceController partyMaintenanceController;
	private List<BankNameDTO> bankNameList = new ArrayList<BankNameDTO>();
	private List<BankBranchDTO> bankBranchList = new ArrayList<BankBranchDTO>();
	private List<BankBranchDTO> bankBranchListOnChange = new ArrayList<BankBranchDTO>();
	
	@SuppressWarnings("unchecked")
	private Collection<FormComponent> validationComponents = new ArrayList<FormComponent>();
	
	private final static Logger logger = Logger.getLogger(BankingDetailsPanel.class.getName());
	
	private static final String DEFAULTCHOICE ="Select one";
	
	private boolean showModifyWarning = false;
	
	
	/**
	 * BANK BRANCH CONTAINER
	 */
	private GUIFieldPanel bankNamePanel;
	private GUIFieldPanel branchNamePanel;
	private GUIFieldPanel branchCodePanel;
	private GUIFieldPanel bratLinkPanel;
	//Added PZM2509 - AVS Bank Verification
	private HelperPanel bankingDetailsRequestHistoryPanel;
	private ModalWindow bankingDetailsRequestHistoryWindow;
	
	private HelperPanel bratLinkLabel;
	
	/**
	 * ACCOUNT DETAIL CONTAINER
	 */
	private GUIFieldPanel accountNumberPanel;
	private GUIFieldPanel accountTypePanel;
	private GUIFieldPanel accHolderIntialsPanel;
	private GUIFieldPanel accHolderSurnamePanel;
	private GUIFieldPanel accDetailsChangeReasonPanel;
	private GUIFieldPanel accHolderRelPanel;
	private GUIFieldPanel displayFinancialDetailsPanel;
	
	private IConverter bankNameConverter;
	private IConverter branchCodeConverter;
	private IConverter branchNameConverter;
	
	private BankingDetailsDTO bankingDetailsDTO = null;
	private BankingDetailsDTO partyBankingDetailsDTO = null;
	private BankingDetailsDTO agreementBankingDetailsDTO = null;
	
	private boolean initialized;
	private boolean existingPayDetRequest;
//	private boolean isPaysToDetails=true;
	private Long shownAgreementNr;

	private BankingDetailType bankingDetailType;
	
	private BankingDetailsPanelModel bakingDetailsPageModel;
	private List<BankingDetailsHistoryDTO> bankingDHistory;
	
	long partyOID = 0;
	boolean partyIntermed = false;
	String agmtStatus = SRSAuthWebSession.get().getContextDTO().getAgreementContextDTO().getAgreementStatus();
	String className = "class za.co.liberty.web.pages.party.BankingDetailsPanel";
	boolean showErrorMsg = true;
	
	/**
	 * Default constructor 
	 * 
	 * @param tab_panel_id
	 * @param model
	 * @param editState
	 * @param feedpanel
	 * @param parentPage
	 */
	public BankingDetailsPanel(String tab_panel_id, MaintainPartyPageModel model, EditStateType editState, FeedbackPanel feedpanel, Page parentPage) {
		super(tab_panel_id, editState,parentPage);
		this.pageModel = model;	
		this.bankingDetailsDTO = (model.getPartyDTO() != null)?model.getPartyDTO().getBankingDetailsDTO():null;
		this.feedBackPanel = feedpanel;
	}
	
	@Override
	protected void onBeforeRender() {
		// Check if there are unauthorised requests.
		if(!initialized) {
/*			List<RequestKindType> unAuthRequests = getOutStandingRequestKinds();			
			//check for existing requests FIRST as other panels use variables set here
			for (RequestKindType kind : unAuthRequests) {
				if(kind == RequestKindType.MaintainPaymentDetails)
					existingPayDetRequest = true;
			}
*/			
			initModelForBankingDetails();
			add(form = new BankingDetailsForm("bankingDetailsForm",pageModel));
			initialized = true;
			//Add the history window to pop up banking details history 
			add(bankingDetailsRequestHistoryWindow = createBankingDetailsRequestHistoryWindow("bankingDetailsRequestHistoryWindow"));
			
		}
		
		if(feedBackPanel == null){			
			feedBackPanel = this.getFeedBackPanel();		
		}
		
		if (!existingPayDetRequest && pageModel.hasAgreementsWithPaysToRoles() && pageModel.getSelectedAgreementNr() == null) {
			error("Party might have multiple banking details due to 'PaysTo' role(s), please select an agreement into the context to view the appropriate details.");
		} 
		
		if(partyIntermed && partyBankingDetailsDTO == null && agreementBankingDetailsDTO == null && bankingDetailsDTO == null && partyBankingDetailsDTO.getBratLinkNo() == null && 
				agreementBankingDetailsDTO.getBratLinkNo() == null && bankingDetailsDTO.getBratLinkNo() == null && !existingPayDetRequest && !pageModel.hasAgreementsWithPaysToRoles()){
			error("Financial Account Details not found.");
		}
		
		if(!partyIntermed && getEditState().equals(EditStateType.VIEW) 
				&& pageModel != null && pageModel.getCurrentTabClass() != null 
				&& className.equalsIgnoreCase(pageModel.getCurrentTabClass().toString())){
			error("Party is not an Intermediary! Banking Details cannot be saved!");
		}
		
		if(StringUtils.isNotBlank(bankingDetailsDTO.getAccountDetailsChangeReason()) && getEditState().equals(EditStateType.AUTHORISE)){
			warn("A reason for this request - is available in Account Details");
		}
		
		if(!showErrorMsg){
			error("Agreement is still InProgress - please authorise the Activate Agreement request to enable maintenance of banking details.");
		}
		
		super.onBeforeRender();
			
	}
	
	//get the history and asign it within this
	private void initModelForBankingDetails() {
		if(pageModel == null || pageModel.getPartyDTO() ==  null) {
			error("Page Model should never be null, Please call support if you continue seeing this error");
			bankingDetailsDTO = new BankingDetailsDTO();
			return;		
			
		}
		setBankingDetailsDTOFromModel();
		
		if((getEditState().equals(EditStateType.MODIFY) ||getEditState().equals(EditStateType.ADD))&& 
				this.bankingDetailsDTO != null) {
			this.bankingDetailsDTO = setDefaultValuesForModel(this.bankingDetailsDTO);
			return;
		}
		
	}
	
	private void setBankingDetailsDTOFromModel() {
		
		if(getEditState() != EditStateType.AUTHORISE){

			partyOID = pageModel.getPartyDTO().getOid();
	
			try {
				if (pageModel.hasAgreementsWithPaysToRoles() && pageModel.getSelectedAgreementNr() == null) {
					// We can't retrieve the details if an agreement nr is required
					//   error already shown in onBeforeRender method
					this.bankingDetailsDTO = new BankingDetailsDTO();
					return;
				}
				MaintainBankingDetailResultDTO resultDto = getPartyMaintenanceController().getBankingDetailsDTO(partyOID, pageModel.getSelectedAgreementNr(), bankingDetailType);
				boolean isAgreementSpecific = (resultDto.getAgreementBankingDetailsObject() == null)? false : resultDto.getAgreementBankingDetailsObject().isAgreementSpecific();
				if (bankingDetailType == null) {
					bankingDetailType = isAgreementSpecific ? BankingDetailType.AGREEMENT : BankingDetailType.PARTY;
				}
				this.bankingDetailsDTO = resultDto.getBankingDetailsObject();
				if(pageModel !=null && pageModel.getSelectedAgreementNr() != null){
					bankingDetailsDTO.setSelectedAgreementNumber(pageModel.getSelectedAgreementNr());
				}
				this.partyBankingDetailsDTO = resultDto.getPartyBankingDetailsObject();
				this.partyBankingDetailsDTO = (partyBankingDetailsDTO==null)? new BankingDetailsDTO() : partyBankingDetailsDTO;
								
				this.agreementBankingDetailsDTO = resultDto.getAgreementBankingDetailsObject();
				this.agreementBankingDetailsDTO = (agreementBankingDetailsDTO==null)? new BankingDetailsDTO() : agreementBankingDetailsDTO;
				
				shownAgreementNr = resultDto.getAgreementNumber();
				
				pageModel.getPartyDTO().setBankingDetailsDTO(this.bankingDetailsDTO);
				if(pageModel.getPartyBeforeImage() != null){
					pageModel.getPartyBeforeImage().setBankingDetailsDTO(this.bankingDetailsDTO!= null?
						(BankingDetailsDTO)SerializationUtils.clone(this.bankingDetailsDTO):null);
				}
				
				if(bankingDetailsDTO != null && bankingDetailsDTO.getErrorMsgFromMQSI() !=null
						&& !SRSAppWebConstants.EMPTY_STRING.equals(bankingDetailsDTO.getErrorMsgFromMQSI()) && !existingPayDetRequest){
					error(bankingDetailsDTO.getErrorMsgFromMQSI());
					return;
				}
			} catch (DataNotFoundException e) {
				//Do not display error messages When in Add screen even if no Banking Details exist.
				//User should be able to add a new Banking Details or Modify an existing Banking Details
				//Also do not display ValidationExceptions messages if there is an existing request (as the existing request message will be displayed)
				if(!getEditState().equals(EditStateType.ADD) && !existingPayDetRequest){
					error(e.getMessage());
				}
			}
		}else{
//			When EditStateType.AUTHORISE
			bankingDetailType = bankingDetailsDTO.isAgreementSpecific() ? BankingDetailType.AGREEMENT : BankingDetailType.PARTY;
		}
		//If still null then set DTO
		if(this.bankingDetailsDTO == null)
			this.bankingDetailsDTO = new BankingDetailsDTO();
	}
	
	
	
	private class BankingDetailsForm extends Form<Object> {
		private static final long serialVersionUID = 1L;
		
		private BankingDetailsForm(String id,final MaintainPartyPageModel pageModel) {
			super(id);	
			
			add(getPaysToContainer("paysToContainer"));
			add(getDisplayFinancialDetailsView());
			add(getBankBranchContainer());
			add(getAccountContainer());
			
			// Commented out because we display Unmatched verification results at the top
			// and we also have the HISTORY popup whitch is the single source of truth 
			add(getAVSPanel());
			
			add(getValdationVersion());
			add(getBankingDetailsSource());
			//add(bankingDetailsRequestHistoryButton = createBankingDetailsRequestHistoryButton("bankingDetailsRequestHistoryButton"));
			//add(bankingDetailsRequestHistoryWindow = createBankingDetailsRequestHistoryWindow("bankingDetailsRequestHistoryWindow") );
			
			add(getFormValidator(pageModel));
			
		}
		
		// Replace the get banking verification response with properties from the database
		private AVSPanel getAVSPanel() {
			return new AVSPanel("avsPanel", bankingDetailsDTO.getBankingVerificationResponseDTO() , getEditState(), null);
		}

		// Build a validator for the form
		private BankingDetailsFormValidator getFormValidator(final MaintainPartyPageModel pageModel) {
			return new BankingDetailsFormValidator(pageModel);
		}

		// Define validation rules and avail methods to access them
		private final class BankingDetailsFormValidator implements IFormValidator {
			private final MaintainPartyPageModel pageModel;
			private static final long serialVersionUID = 1L;

			private BankingDetailsFormValidator(MaintainPartyPageModel pageModel) {
				this.pageModel = pageModel;
			}

			@SuppressWarnings("unchecked")
			public FormComponent[] getDependentFormComponents() {
				return validationComponents.toArray(new FormComponent[0]) ;
			}

			public void validate(Form<?> arg0) {
				
				if(validationComponents.size() == 0)
					return;
				//AVS Defect fix- SBS0510-Display Warning msg on Banking Details Step only and then proceed to next

				if(this.pageModel.isWarningOnlyOnAVSCall()) {
					this.pageModel.setWarningOnlyOnAVSCall(false);
					return;
				}
				
				//SBS0510-for AVS Validation check
				boolean isSurnameChanged = false;				
				String partySurname = null;
				
				BankingDetailsDTO newBankingDetails = BankingDetailsPanel.this.bankingDetailsDTO;

				if(pageModel.getPartyDTO() instanceof EmployeeDTO) {//Check surname if Person
					
					partySurname = ((EmployeeDTO)pageModel.getPartyDTO()).getSurname();
					
				} else if(pageModel.getPartyDTO() instanceof OrganisationDTO) {//Check Business Name if Organsiation
					
					partySurname = ((OrganisationDTO)pageModel.getPartyDTO()).getBusinessName();
				}
				
				if(StringUtils.isNotBlank(newBankingDetails.getAccountHolderSurname())){
					isSurnameChanged = !newBankingDetails.getAccountHolderSurname().equalsIgnoreCase(partySurname);
				}
				
				logger.info("newBankingDetails : " + newBankingDetails.toString());
				logger.info("Party Surname changed : ["+ isSurnameChanged+"]");
				
				//PartyBeforeImage can be null when adding a new agreement
				if (pageModel.getPartyBeforeImage() != null) {
					//Get handle of bankingDetailsDTO with old values
					BankingDetailsDTO oldBankingDetails = pageModel.getPartyBeforeImage().getBankingDetailsDTO();
					
					//logger.info("oldBankingDetails : " + oldBankingDetails != null ? oldBankingDetails.toString() : null);
				
				
					//If all values are the as same the old ones, stop process
					try {
						if (!getEditState().equals(EditStateType.ADD) &&  newBankingDetails.equals(oldBankingDetails)) {
							arg0.error("There are no changes - to save.");
							return;
						}
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
						return;
					}
				}
				
				//Explicitly validating mandatory condition for 
				if(newBankingDetails.getBankName() == null){
					arg0.error("Bank Name - is required");
					return;
				}
				
				if(newBankingDetails.getBranch() == null){
					arg0.error("Branch Name and Branch Code - are required");
					return;
				}
				
				if(newBankingDetails.getAccountNumber() == null || newBankingDetails.getAccountNumber() == 0){
					arg0.info("Account No - is required");
					return;
				}
				
				if(newBankingDetails.getAccountHolderSurname() == null || SRSAppWebConstants.EMPTY_STRING.equals(newBankingDetails.getAccountHolderSurname().trim())){
					arg0.info("Account Holder Surname - is required");
					return;
				}
						
				BankingDetailsDTO validatedBankingDetails = new BankingDetailsDTO();
				boolean validated = false;

				try {
					long partyOID = pageModel.getPartyDTO().getOid();
					boolean isIntermediary = getPartyMaintenanceController().isPartyAnIntermediary(partyOID);
					
					if (!isIntermediary && !getEditState().equals(EditStateType.ADD)) 
								throw new ValidationException("Party is not an Intermediary! Banking Details cannot be saved!");

					// If there's no data from AVS, sanitised local data and continue processing 
					try {
						validatedBankingDetails = getPartyMaintenanceController().validateFinancialAccountDetails(newBankingDetails, pageModel.getPartyDTO());
					} catch (CommunicationException ce) {
						throw ce;
						
					}catch (Exception e) {
						e.printStackTrace();						
					}
					
					validatedBankingDetails.setAccountDetailsChangeReason(newBankingDetails.getAccountDetailsChangeReason());
					
					double weightTotal = 0;
					boolean secondAuthRequired = false;
					String warning = "";
					List<String> bankingVerificationRuleWarnings = new ArrayList<String>();	
				
					
					if (validatedBankingDetails.getErrorMsgFromMQSI() == null || 
							validatedBankingDetails.getErrorMsgFromMQSI().trim().equals(SRSAppWebConstants.EMPTY_STRING)) {						

						//Sanitise values with empty strings, as it seems like PDB returns N even if the values are the same
						//Move this to a Business Object once confirmed to work
						ActionType actionType = validatedBankingDetails.getActionType();
						 
						if (actionType != null && actionType.equals(ActionType.VALIDATE) && !getEditState().equals(EditStateType.ADD)) {							
						
							String trimmedValue = null;
							
									
							if (validatedBankingDetails.getAccountHolderInitials() != null) {
								trimmedValue = validatedBankingDetails.getAccountHolderInitials().trim();
								
								if (trimmedValue.equalsIgnoreCase(newBankingDetails.getAccountHolderInitials())) {
									validatedBankingDetails.setAccountHolderInitials(trimmedValue);		
									validatedBankingDetails.getBankingVerificationResponseDTO().setIsInitials(AVSStatusType.YES);
								}
							}
							
							if (validatedBankingDetails.getAccountHolderSurname() != null) {
								trimmedValue = validatedBankingDetails.getAccountHolderSurname().trim();
								
								if (trimmedValue.equalsIgnoreCase(newBankingDetails.getAccountHolderSurname())) {
									validatedBankingDetails.setAccountHolderSurname(trimmedValue);		
									validatedBankingDetails.getBankingVerificationResponseDTO().setIsLastName((AVSStatusType.YES));
								}
							}
						
						}
						
						BankingVerificationResponseDTO avsBankingVerificationResponseDTO = validatedBankingDetails
																						.getBankingVerificationResponseDTO();
						
						//Verify AVS response
						List<BankVerificationRulesDTO> bankVerificationResults = getPartyMaintenanceController()
																						.verifyAVSResponse
																						(avsBankingVerificationResponseDTO);
						
						Collections.sort(bankVerificationResults);

						if (bankVerificationResults != null && !bankVerificationResults.isEmpty()) {
						
							for (BankVerificationRulesDTO bankVerificationResult : bankVerificationResults) {
												
								if(bankVerificationResult.getAvsResponse() == null){
									continue;
								}		

								//Collect a total of all the weightings ONLY FOR **FAILED** VALIDATION ( AVS STATUS =  NO / UNMATCHED)
								//to help determine whether a reason and secondauthoriser are required
								if (bankVerificationResult.getAvsResponse() == AVSStatusType.NO
										|| bankVerificationResult.getAvsResponse() == AVSStatusType.UNMATCHED) {
									weightTotal += bankVerificationResult.getWeighting();
								}
								
								//Display warning messages where responses are not Y 
								if (!bankVerificationResult.getIndicatorValue().equals(AVSStatusType.YES.getMqAvsStatus())) {	
									
									warning = bankVerificationResult.getDescription() + " validation result : " + bankVerificationResult.getAvsResponse().getDisplayName();
									
									arg0.warn(warning);	
									//AVS Defect fix- SBS0510-Display Warning msg on Banking Details Step only and then proceed to next

									BankingDetailsPanel.this.pageModel.setWarningOnlyOnAVSCall(true);
									
									bankingVerificationRuleWarnings.add(warning);
									
									//Is there any checktype that requires a second authoriser
									if (!secondAuthRequired) {
										secondAuthRequired = bankVerificationResult.isSecondAutheriser();
									}
								}
	
								avsBankingVerificationResponseDTO.setBankingVerificationRuleWarnings(bankingVerificationRuleWarnings);
																
								Logger.getLogger(this.getClass()).info("Affected bankVerificationResult : " + bankVerificationResult.toString());
							}	
								
							logger.info("bankVerification weightTotal : " + weightTotal);
							
						} else {
							logger.info("Bank Verification Results - not received");
						
							//If no response from AVS, require second authoriser
							weightTotal = 1;
							secondAuthRequired = true;
							
							arg0.warn("Bank Verification Results - not received");
						}
						
						if (secondAuthRequired || isSurnameChanged || weightTotal >= 1 ) {								
							if (StringUtils.isBlank(validatedBankingDetails.getAccountDetailsChangeReason())) {
										
								String err = "Overrider reason is required to proceed as Account holder details do not match or weightings are greater than recommended(Second Authoriser Required) !";
								
								arg0.error(err);
								BankingDetailsPanel.this.pageModel.setWarningOnlyOnAVSCall(false);
								
								bankingVerificationRuleWarnings.add(err);
							} else {									
								//Require a second authoriser when the total weighting is greater or equal to 1
								//and at least 1 checktype requires it
								validatedBankingDetails.setSecondAuthoriserRequired(Boolean.TRUE);
							}
						}
						
						validated = true;
						
					} else {
						if (!validatedBankingDetails.getErrorMsgFromMQSI().trim().equalsIgnoreCase("INVALID BANK BRANCH STATUS")) {
							arg0.error(validatedBankingDetails.getErrorMsgFromMQSI());
						} else {
							arg0.warn(validatedBankingDetails.getErrorMsgFromMQSI());
						}
					}
					
				} catch (ValidationException e) {
					for (String msg : e.getErrorMessages()) {
						arg0.error(msg);
					}
				} catch (CommunicationException e) {
					arg0.error(e.getMessage());
				}

				
				if (validated) {
					newBankingDetails.setBankingDetailsSource(validatedBankingDetails.getBankingDetailsSource());
					newBankingDetails.setBankingVerificationResponseDTO(validatedBankingDetails.getBankingVerificationResponseDTO());
					newBankingDetails.setRequestOid(validatedBankingDetails.getRequestOid());
					newBankingDetails.setPartyoid(validatedBankingDetails.getPartyoid());
					newBankingDetails.setIdentityNumber(validatedBankingDetails.getIdentityNumber());
					newBankingDetails.setPassportNumber(validatedBankingDetails.getPassportNumber());
					newBankingDetails.setCompanyRegistration(validatedBankingDetails.getCompanyRegistration());
					newBankingDetails.setHoursValid(validatedBankingDetails.getHoursValid());
					newBankingDetails.setSecondAuthoriserRequired(validatedBankingDetails.getSecondAuthoriserRequired());
					BankingDetailsPanel.this.pageModel.getPartyDTO().setBankingDetailsDTO(newBankingDetails);
					BankingDetailsPanel.this.bankingDetailsDTO = newBankingDetails;	
					logger.info("BankingDetailsPanel validated sucessfully on Submit");
				}else{
					arg0.error("Failed to save banking detail changes");
				}
			}		
		}		
	}

	public Class<?> getPanelClass() {
		return BankingDetailsPanel.class;
	}

	public WebMarkupContainer getAccountContainer() {
		if (accountContainer == null) {
			accountContainer = new WebMarkupContainer("accountContainer");
			RepeatingView leftPanel = new RepeatingView("leftPanel");
			/**
			 * Left panel content
			 */
			leftPanel.add(getAccountNumberPanel());
			leftPanel.add(getAccountTypePanel());
			leftPanel.add(getAccHolderRelPanel());
			leftPanel.add(getAccHolderIntialsPanel());
			leftPanel.add(getAccHolderSurnamePanel());
			leftPanel.add(getAccHolderChangeReasonPanel());
			
			accountContainer.setOutputMarkupId(true);
			accountContainer.add(leftPanel);
			
		}
		return accountContainer;
	}

	
	public RepeatingView getDisplayFinancialDetailsView() {
		
		RepeatingView displayFinancialDetailsView = new RepeatingView("displayFinancialDetailsView");
		displayFinancialDetailsView.add(getDisplayFinancialDetailsPanel());		
		//add(displayFinancialDetailsView);
		return displayFinancialDetailsView;
	}
	

	public Label getBankingDetailsSource() {
		if (EditStateType.MODIFY.equals(getEditState()) || this.bankingDetailsDTO == null || this.bankingDetailsDTO.getBankingDetailsSource() == null || this.bankingDetailsDTO.getBankingDetailsSource().equals(BankingDetailsSource.INTEGRATION)) return new Label("bankingDetailsSource", "");
		String message = this.bankingDetailsDTO.getBankingDetailsSource() == BankingDetailsSource.INTERNAL_TEST_FILE ? " ( Internal Test File )"
					: " ( External Test File )";
		return new Label("bankingDetailsSource", message);
	}
	
	public Label getValdationVersion() {
		String message = "";
		if (!EditStateType.MODIFY.equals(getEditState()) && this.bankingDetailsDTO != null && this.bankingDetailsDTO.getRequestOid() != null) {
			message = this.bankingDetailsDTO.getRequestOid() != 0 ? " (Historic result for bratlink " + this.bankingDetailsDTO.getBratLinkNo() +" )"
					: " (Current result for bratlink " + this.bankingDetailsDTO.getBratLinkNo() +" )";
		}
		return new Label("validationVersion", message);
	}

	/**
	 * Show a warning message for parties with pays To roles.
	 * 
	 * @param id
	 * @return
	 */
	public WebMarkupContainer getPaysToContainer(String id) {
		if (paysToContainer != null) {
			return paysToContainer;			
		}
		String message = null;
		if ((pageModel.getSelectedAgreementNr() != null) && pageModel.hasAgreementsWithPaysToRoles()) {
			if (pageModel.isSelectedAgreementHasPaysTo()) {
				message = "Agreement has 'PaysTo' role with agreement " + shownAgreementNr + " - this is " + shownAgreementNr 
					+ " agreement's banking details. ";
	
			} else {
				StringBuilder builder = new StringBuilder();
				for (AgreementRoleDTO dto : pageModel.getPaysToAgreementList()) {
					if (builder.length()>0) {
						builder.append(", ");
					}
					builder.append("" + dto.getAgreementNumber());
				}
				message = "This party has other agreements with paysTo roles [" + builder.toString()
						+ "], you are now viewing " +
						"the banking details linked to " + shownAgreementNr;
			}
		}

		paysToContainer = new WebMarkupContainer(id);	
		paysToContainer.add(new Label("paysToMessage", message));
		paysToContainer.setVisible(message!=null);
		return paysToContainer;
	}
	
	public WebMarkupContainer getBankBranchContainer() {
		if (bankBranchContainer == null) {
			bankBranchContainer = new WebMarkupContainer("bankBranchContainer");
			RepeatingView leftPanel = new RepeatingView("leftPanel");
			RepeatingView bratLinkPanel = new RepeatingView("bratLinkPanel");
			
			/**
			 * Left panel content
			 */
			leftPanel.add(getBankNamePanel());
			leftPanel.add(getBranchNamePanel());
			leftPanel.add(getBranchCodePanel());
			bratLinkPanel.add(getBratLinkPanel());
			bratLinkPanel.add(getBankingDetailsRequestHistoryPanel());
			
			bankBranchContainer.setOutputMarkupId(true);
			bankBranchContainer.add(leftPanel);
			bankBranchContainer.add(bratLinkPanel);
		}
		
		return bankBranchContainer;
	}


	public GUIFieldPanel getBratLinkPanel() {

		if(bratLinkPanel == null) {
			bratLinkPanel = createGUIFieldPanel("Bratlink No","Bratlink No","Bratlink No",
					createBratLinkLabel(this.bankingDetailsDTO,"bratLinkNo"));
			if(((HelperPanel)bratLinkPanel.getComponent()).getEnclosedObject() instanceof FormComponent){
				validationComponents.add((FormComponent<?>)((HelperPanel)bratLinkPanel.getComponent()).getEnclosedObject());
			}
		}
		
		if(this.bankingDetailsDTO != null && (this.bankingDetailsDTO.getBratLinkNo() == null ||
				this.bankingDetailsDTO.getBratLinkNo().longValue() == 0l))
				bratLinkPanel.setVisible(false);
			else
				bratLinkPanel.setVisible(true);

		return bratLinkPanel;
	}
	
	private HelperPanel createBratLinkLabel(BankingDetailsDTO bankingDetailsDTO,String attribute) {
		if(bratLinkLabel== null){
		Label label = new Label("value",
				new PropertyModel<Object>(bankingDetailsDTO,attribute));
				label.setOutputMarkupId(true);
				label.setOutputMarkupPlaceholderTag(true);
				
				bratLinkLabel = HelperPanel.getInstance("panel",label);
		}
		return bratLinkLabel;
	}

	public GUIFieldPanel getAccHolderRelPanel() {
		
		if(accHolderRelPanel == null){
			accHolderRelPanel = createGUIFieldPanel("Account Holder Rel","Account Holder Rel","Account Holder Rel",
					createDropdownField(this.bankingDetailsDTO,"Account Holder Rel","panel","accHolderRel",
							Arrays.asList(AccountHolderRelType.values()),new ChoiceRenderer<Object>("typeName","typeID"),
							DEFAULTCHOICE,true,true,getEditStateTypesForBankingDetails()),8);
			if(((HelperPanel)accHolderRelPanel.getComponent()).getEnclosedObject() instanceof FormComponent){
				validationComponents.add((FormComponent<?>)((HelperPanel)accHolderRelPanel.getComponent()).getEnclosedObject());
			}
		}
	
		return accHolderRelPanel;
	}

	public GUIFieldPanel getAccountNumberPanel() {

		if(accountNumberPanel == null) {
			accountNumberPanel = createGUIFieldPanel("Account No","Account No","Account No",
					createPageField(this.bankingDetailsDTO,"Account No","panel","accountNumber",
							ComponentType.TEXTFIELD, true,true,getEditStateTypesForBankingDetails()),4);
			if(((HelperPanel)accountNumberPanel.getComponent()).getEnclosedObject() instanceof FormComponent){
				validationComponents.add((FormComponent<?>)((HelperPanel)accountNumberPanel.getComponent()).getEnclosedObject());
			}
		}

		return accountNumberPanel;
	}

	public GUIFieldPanel getAccountTypePanel() {

		if(accountTypePanel == null) {
			accountTypePanel = createGUIFieldPanel("Account Type","Account Type","Account Type",
					createDropdownField(this.bankingDetailsDTO,"Account Type","panel","accountType",
							Arrays.asList(AccountType.values()),new ChoiceRenderer<Object>("typeName","typeID"),DEFAULTCHOICE,
							true,true,getEditStateTypesForBankingDetails()),5);
			if(((HelperPanel)accountTypePanel.getComponent()).getEnclosedObject() instanceof FormComponent){
				validationComponents.add((FormComponent<?>)((HelperPanel)accountTypePanel.getComponent()).getEnclosedObject());
			}
		}

		return accountTypePanel;
	}

	
	public GUIFieldPanel getBankNamePanel() {		
		if(bankNamePanel == null) {
			bankNamePanel = createGUIFieldPanel("Bank Name",getLabelForMandatory("Bank Name"),"Bank Name",!isView(getEditStateTypesForBankingDetails())?
					createDropdownField(this.bankingDetailsDTO,"Bank Name","panel","bankName",
							getValidBankNames(),new ChoiceRenderer<Object>("name","name"),DEFAULTCHOICE, 
							false,false,getEditStateTypesForBankingDetails()):createCustomLabel(this.bankingDetailsDTO,"Bank Name","panel","bankName",getBankNameConverter()),1);
			
			Object obj = ((HelperPanel)bankNamePanel.getComponent()).getEnclosedObject();
			if(obj instanceof FormComponent){
				validationComponents.add((FormComponent<?>)obj);
				
				if (obj instanceof DropDownChoice) {
					final SRSDropDownChoice<?> dropDownChoice = (SRSDropDownChoice<?>) obj;
					
					dropDownChoice.setOutputMarkupId(true);
					dropDownChoice.setOutputMarkupPlaceholderTag(true);
					dropDownChoice.add(new AjaxFormComponentUpdatingBehavior("change"){
								private static final long serialVersionUID = 1L;
								
								@Override
								protected void onUpdate(AjaxRequestTarget target) {	
									List<BankBranchDTO> list = null;
									String inp = dropDownChoice.getInput();
									if(SRSAppWebConstants.EMPTY_STRING.equals(inp))
										list = getValidPartyValuesFactory().getValidValuesForBankBranch();
									else
										list = getValidPartyValuesFactory().getValidValuesForBankBranchForBankName(inp);
									bankBranchList.clear();
									bankBranchList.addAll(list);
									bankBranchListOnChange.clear();
									bankBranchListOnChange.addAll(list);
									
									BankingDetailsPanel.this.bankingDetailsDTO.setBranch(null);
									target.add(branchNamePanel);
									target.add(branchCodePanel);	
									target.add(feedBackPanel);	
							    }							
					});				
				
				}
			}
		}
		return bankNamePanel;
	}

	@SuppressWarnings("serial")
	private HelperPanel createCustomLabel(Object propObject,String labelId,String componentID,String attribute,final IConverter converter) {

		Label viewLabel = new Label("value", new PropertyModel<Object>(propObject,attribute)) {
			@Override
			public IConverter getConverter(Class arg0) {
				return converter;
			}
		};
		return HelperPanel.getInstance(componentID, viewLabel,false);
	
	}

	public GUIFieldPanel getBranchCodePanel() {

		if(branchCodePanel == null) {
			branchCodePanel = createGUIFieldPanel("Branch Code",getLabelForMandatory("Branch Code"),"Branch Code",!isView(getEditStateTypesForBankingDetails())?
					createDropdownField(this.bankingDetailsDTO,"Branch Code","panel","branch",
							getValidBankBranches(),new ChoiceRenderer<Object>("code","code"),DEFAULTCHOICE, 
							false,false,getEditStateTypesForBankingDetails()):createCustomLabel(this.bankingDetailsDTO,"Branch Code","panel","branch",getBranchCodeConverter()),3);
			branchCodePanel.setOutputMarkupId(true);
			branchCodePanel.setOutputMarkupPlaceholderTag(true);
			
			Object obj = ((HelperPanel)branchCodePanel.getComponent()).getEnclosedObject();
			
			if( obj instanceof FormComponent){
				validationComponents.add((FormComponent<?>)obj);
				if (obj instanceof SRSDropDownChoice) {
					final SRSDropDownChoice<?> downChoice = (SRSDropDownChoice<?>) obj;
					downChoice.setOutputMarkupId(true);
					downChoice.setOutputMarkupPlaceholderTag(true);
					downChoice.add(new AjaxFormComponentUpdatingBehavior("change"){
						private static final long serialVersionUID = 1L;
						@Override
						protected void onUpdate(AjaxRequestTarget target) {	
							
							String inp = downChoice.getInput();
							if(!SRSAppWebConstants.EMPTY_STRING.equals(inp))
							{
								for(BankBranchDTO branchDTO:bankBranchListOnChange)
								{
 									if(branchDTO.getCode() == Long.parseLong(inp)){
										bankingDetailsDTO.setBranch(branchDTO);
										break;
									}
									
								}
							}else
							{
								bankingDetailsDTO.setBranch(null);
							}
							
							setSelected(branchNamePanel,bankingDetailsDTO,"branch");
							target.add(branchNamePanel);
							target.add(feedBackPanel);	
							
					    }
											
			});	
					
				}
			}
		}

		return branchCodePanel;
	}

	@SuppressWarnings("unchecked")
	public GUIFieldPanel getBranchNamePanel() {

		if(branchNamePanel == null) {
			showModifyWarning = false;
			branchNamePanel = createGUIFieldPanel("Branch Name",getLabelForMandatory("Branch Name"),"Branch Name",!isView(getEditStateTypesForBankingDetails())?
					createDropdownField(this.bankingDetailsDTO,"Branch Name","panel","branch",
							getValidBankBranches(),new ChoiceRenderer<Object>("name","name"),DEFAULTCHOICE, 
							false,false,getEditStateTypesForBankingDetails()):createCustomLabel(this.bankingDetailsDTO,"Branch Name","panel","branch",getBranchNameConverter()),2);
			branchNamePanel.setOutputMarkupId(true);
			branchNamePanel.setOutputMarkupPlaceholderTag(true);
			
			Object obj = ((HelperPanel)branchNamePanel.getComponent()).getEnclosedObject();
						
			if( obj instanceof FormComponent){
				validationComponents.add((FormComponent<?>)obj);
				if (obj instanceof SRSDropDownChoice) {
					final SRSDropDownChoice<BankBranchDTO> downChoice = (SRSDropDownChoice<BankBranchDTO>) obj;
					downChoice.setOutputMarkupId(true);
					downChoice.setOutputMarkupPlaceholderTag(true);
					if(this.bankingDetailsDTO.getBranch() != null){
					downChoice.setModelObject(bankingDetailsDTO.getBranch());
					}
					downChoice.add(new AjaxFormComponentUpdatingBehavior("change"){
						
						private static final long serialVersionUID = 1L;
						@Override
						protected void onUpdate(AjaxRequestTarget target) {	
							
							String inp = downChoice.getInput();
							if(!SRSAppWebConstants.EMPTY_STRING.equals(inp))
							{
								for(BankBranchDTO branchDTO:bankBranchListOnChange)
								{
 									if(branchDTO.getName().equals(inp)){
										bankingDetailsDTO.setBranch(branchDTO);
										break;
									}
									
								}
							}else
							{
								bankingDetailsDTO.setBranch(null);
							}
							
							setSelected(branchCodePanel,bankingDetailsDTO,"branch");
                            target.add(branchCodePanel);
                            target.add(feedBackPanel);	
					    }
											
			});							
					
				}
			}
		}

		return branchNamePanel;
	}

	public GUIFieldPanel getAccHolderIntialsPanel() {

		if(accHolderIntialsPanel == null) {
			accHolderIntialsPanel = createGUIFieldPanel("Account Holder Initials","Account Holder Initials","Account Holder Initials",
					createPageField(this.bankingDetailsDTO,"Initials","panel","accountHolderInitials",
							ComponentType.TEXTFIELD, false,true,getEditStateTypesForBankingDetails()),6);
			Object obj  = ((HelperPanel)accHolderIntialsPanel.getComponent()).getEnclosedObject(); 
			if( obj instanceof FormComponent){
				validationComponents.add((FormComponent<?>)obj);
				if(obj instanceof TextField){
					TextField<?> textField = (TextField<?>)obj;
					textField.add(new AttributeModifier("style","width: 35px;"));
				}
			}
		}

		return accHolderIntialsPanel;
	}

	public GUIFieldPanel getAccHolderSurnamePanel() {

		if(accHolderSurnamePanel == null) {
			accHolderSurnamePanel = createGUIFieldPanel("Account Holder Surname","Account Holder Surname","Account Holder Surname",
					createPageField(this.bankingDetailsDTO,"Surname","panel","accountHolderSurname",
							ComponentType.TEXTFIELD, true,true,getEditStateTypesForBankingDetails()),7);
			if(((HelperPanel)accHolderSurnamePanel.getComponent()).getEnclosedObject() instanceof FormComponent){
				validationComponents.add((FormComponent<?>)((HelperPanel)accHolderSurnamePanel.getComponent()).getEnclosedObject());
			}
			
			accHolderSurnamePanel.add(new AttributeModifier("size", 100));
		}

		return accHolderSurnamePanel;
	}
	
	public GUIFieldPanel getAccHolderChangeReasonPanel() {

		if(accDetailsChangeReasonPanel == null) {
			accDetailsChangeReasonPanel = createGUIFieldPanel("Overrider Reason","Overrider Reason","Reason",
															createPageField(
																			this.bankingDetailsDTO,
																			"Overrider Reason",
																			"panel",
																			"accountDetailsChangeReason",
																			 ComponentType.TEXTAREA, 
																			 false,
																			 true,
																			 getEditStateTypesForBankingDetails()),
															7
															);
			
			if(((HelperPanel)accDetailsChangeReasonPanel.getComponent()).getEnclosedObject() instanceof FormComponent){
				validationComponents.add((FormComponent<?>)((HelperPanel)accDetailsChangeReasonPanel.getComponent()).getEnclosedObject());
			}
		}

		return accDetailsChangeReasonPanel;
	}
	
	//PZM2509 investigate 
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public GUIFieldPanel getDisplayFinancialDetailsPanel() {
		if(displayFinancialDetailsPanel == null) {
			IModel model = new IModel() {
				private static final long serialVersionUID = 1L;
				
				public Object getObject() {
					return bankingDetailType;
				}
				public void setObject(Object arg0) {
					bankingDetailType = (BankingDetailType)arg0;
				}
				public void detach() {	
				}
			};
			List<BankingDetailType> choices = 	new ArrayList<BankingDetailType>() {
						{add(BankingDetailType.AGREEMENT);
				          add(BankingDetailType.PARTY);}};
					
			
			RadioChoice bankDetChoice = new RadioChoice("panel", model, choices) {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onComponentTag(ComponentTag tag) {
					super.onComponentTag(tag);
					tag.put("colspan", 2);
					tag.put("style", "padding-left: 50px; padding-right: 50px;padding-bottom:15px;");
				}
			};
			
			final RadioGroup gp = new RadioGroup("JippoGroup");
			bankDetChoice.setRequired(true);
			bankDetChoice.setLabel(new Model("Banking Details Choice"));
			
//			Radio radio = new Radio("radio1",model){
//				private static final long serialVersionUID = 1L;
//
//				@Override
//				protected RadioGroup getGroup() {
//					return gp;
//				}
//			};
			bankDetChoice.add(new AjaxFormChoiceComponentUpdatingBehavior(){

				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					if (bankingDetailType == BankingDetailType.AGREEMENT) {
						agreementBankingDetailsDTO.setAgreementSpecific(true);
						bankingDetailsDTO.copyInto(agreementBankingDetailsDTO);
					} else {
						partyBankingDetailsDTO.setAgreementSpecific(false);
						bankingDetailsDTO.copyInto(partyBankingDetailsDTO);  
					}
					//refresh bank details
					target.add(bankBranchContainer);
					target.add(accountContainer);

				}
			});
//			bankDetChoice.add(radio);
			bankDetChoice.setOutputMarkupId(true);
			bankDetChoice.setOutputMarkupPlaceholderTag(true);
			bankDetChoice.setEnabled(getEditState().equals(EditStateType.MODIFY) || getEditState().equals(EditStateType.ADD));
	
//			radio.setOutputMarkupId(true);
//			radio.setOutputMarkupPlaceholderTag(true);
			Label label = new Label("label",new Model(""));
			label.setVisible(false);
			displayFinancialDetailsPanel = new GUIFieldPanel("bankDetChoices",label,bankDetChoice);
			displayFinancialDetailsPanel.setOutputMarkupId(true);
			displayFinancialDetailsPanel.setOutputMarkupPlaceholderTag(true);			
		}		
		return displayFinancialDetailsPanel;
	}
	
	//Added PZM2509 - AVS Bank Verification Response 
	
	/**
	 * Create Bank Request history popup
	 * @param id
	 * @return
	 */
	//Added PZM2509 - AVS Bank Verification
	@SuppressWarnings("unchecked")
	private HelperPanel getBankingDetailsRequestHistoryPanel() {
		if(bankingDetailsRequestHistoryPanel == null){
			
			Button button = new Button("value", Model.of("History"));
			button.setOutputMarkupId(true);
			button.setOutputMarkupPlaceholderTag(true);
			button.setEnabled(true);
			button.add(new AjaxFormComponentUpdatingBehavior("click"){
				private static final long serialVersionUID = 0L;
				@Override
				protected void onUpdate(AjaxRequestTarget target){
					
					bankingDetailsRequestHistoryWindow.show(target);
				}
			});
			bankingDetailsRequestHistoryPanel = HelperPanel.getInstance("History", button);
			
			bankingDetailsRequestHistoryPanel.setOutputMarkupId(true);
			button.setEnabled(getEditState() == EditStateType.MODIFY
					|| getEditState() == EditStateType.VIEW);
		
		}
		return bankingDetailsRequestHistoryPanel;
	}
	
	/**
	 * Create Bank Request history popup
	 * 
	 * @param id
	 * @return
	 */
	
	private ModalWindow createBankingDetailsRequestHistoryWindow(String id){
		final ModalWindow window = new ModalWindow(id);
		window.setTitle("History");
		// Create the page
		window.setPageCreator(new ModalWindow.PageCreator() {
			private static final long serialVersionUID = 1L;
			public Page createPage(){
				
				List<BankingDetailsHistoryDTO> dtoList = getGuiController()
						.getBankingVerificationHistoryForAgreementNo(bankingDetailsDTO.getSelectedAgreementNumber());
				if(dtoList == null) {
					dtoList = new ArrayList<BankingDetailsHistoryDTO>();
				}
				
				
				BankingDetailsRequestHistoryPage bnkdtlsHistoryPage = new BankingDetailsRequestHistoryPage(
						window,bakingDetailsPageModel,dtoList);
				bnkdtlsHistoryPage.setOutputMarkupId(true);
				return bnkdtlsHistoryPage;
			}
		});
		// Initialise window settings
				window.setMinimalHeight(350);
				window.setInitialHeight(350);
				window.setMinimalWidth(980);
				window.setInitialWidth(980);
				window.setMaskType(MaskType.SEMI_TRANSPARENT);
				window.setCssClassName(ModalWindow.CSS_CLASS_GRAY);	
				window.setOutputMarkupId(true);
				window.setOutputMarkupPlaceholderTag(true);
				window.setCookieName("BankingDetailsRequestHistoryPage");//window.setPageMapName("BankingDetailsRequestHistoryPage");
				return window;
	}
	//
	private EditStateType[] getEditStateTypesForBankingDetails() {
		
		//will disable any modification if there are any requests pending auth
		if(existingPayDetRequest){
			return new EditStateType[]{};
		}
		
		EditStateType[] editstateTypes = new EditStateType[]{EditStateType.MODIFY,EditStateType.ADD};
		
		return editstateTypes;
	}

	private IValidPartyValuesFactory getValidPartyValuesFactory() {
		if(validPartyValuesFactory == null){
				try {
					validPartyValuesFactory = ServiceLocator.lookupService(IValidPartyValuesFactory.class);
				} catch (NamingException e) {
					throw new CommunicationException(e);
				}
			}
			return validPartyValuesFactory;
		
	}
	
	private IPartyMaintenanceController getPartyMaintenanceController() {
		if(partyMaintenanceController == null){
				try {
					partyMaintenanceController = ServiceLocator.lookupService(IPartyMaintenanceController.class);
				} catch (NamingException e) {
					throw new CommunicationException(e);
				}
			}
			return partyMaintenanceController;
		
	}
	
	private List<BankNameDTO> getValidBankNames() {
		if(bankNameList.size() == 0) {
			List<BankNameDTO> list = getValidPartyValuesFactory().getValidValuesForBankName();
			bankNameList.clear();
			bankNameList.addAll(list);
		}
		return bankNameList;
		
	}
	
	private List<BankBranchDTO> getValidBankBranches() {
		if (logger.isDebugEnabled())
			logger.debug("Get validBankBranches - called - bankBranchList.size()=" +bankBranchList.size());
		
		List<BankBranchDTO> list = null;
		if(bankBranchList.size() == 0)
		{
			if(this.bankingDetailsDTO.getBankName() != null) {
				
				list = getValidPartyValuesFactory().getValidValuesForBankBranchForBankName(this.bankingDetailsDTO.getBankName().getName());
				if (list.size()==0 && this.getEditState().isModify() && !showModifyWarning) {
					showModifyWarning = true;
					this.warn("Bank/Branch details do not match local valid branch or bank names and will have to selected again when modifying.");
				}
			} else
				list = getValidPartyValuesFactory().getValidValuesForBankBranch();
			
			if (logger.isDebugEnabled())
				logger.debug("Get validBankBranches - bankName=" 
						+ ((this.bankingDetailsDTO.getBankName() != null) ? this.bankingDetailsDTO.getBankName().getName() : "Get ALL branches")
						+ "   list size =" + list.size());
			
			
			
			bankBranchList.clear();
			bankBranchList.addAll(list);
		}
		return bankBranchList;
		
	}
	
	private BankingDetailsDTO  setDefaultValuesForModel(BankingDetailsDTO bankingDetailsDTO) {
		if(bankingDetailsDTO != null) {
			if(bankingDetailsDTO.getAccHolderRel() == null)
				bankingDetailsDTO.setAccHolderRel(AccountHolderRelType.OWN);
			if(bankingDetailsDTO.getAccountType() == null)
				bankingDetailsDTO.setAccountType(AccountType.CURRENT);
		}
		return bankingDetailsDTO;
		
	}
	
	
	@SuppressWarnings("serial")
	private IConverter getBankNameConverter() {
		if (bankNameConverter == null) {
			bankNameConverter = new IConverter() {
				public Object convertToObject(String stringValue, Locale locale) {
					if (stringValue==null) {
						return null;
					}
					return new BankNameDTO(0,stringValue);
				}

				public String convertToString(Object objectValue, Locale locale) {
					if (objectValue!=null && objectValue instanceof BankNameDTO) {
						return ((BankNameDTO)objectValue).getName();
					}
					return SRSAppWebConstants.EMPTY_STRING;
				}
				
			};
			
		}
		return bankNameConverter;
	}
	
	@SuppressWarnings("serial")
	private IConverter getBranchCodeConverter() {
		if (branchCodeConverter == null) {
			branchCodeConverter = new IConverter() {
				//Not Used
				public Object convertToObject(String stringValue, Locale locale) {
					if (stringValue==null) {
						return null;
					}
					return new BankBranchDTO(0,stringValue);
				}
				//Used
				public String convertToString(Object objectValue, Locale locale) {
					if (objectValue!=null && objectValue instanceof BankBranchDTO) {
						return String.valueOf(((BankBranchDTO)objectValue).getCode());
					}
					return SRSAppWebConstants.EMPTY_STRING;
				}
				
			};
			
		}
		return branchCodeConverter;
	}
	
	@SuppressWarnings("serial")
	private IConverter getBranchNameConverter() {
		if (branchNameConverter == null) {
			branchNameConverter = new IConverter() {
				public Object convertToObject(String stringValue, Locale locale) {
					if (stringValue==null) {
						return null;
					}
					return new BankBranchDTO(0,stringValue);
				}

				public String convertToString(Object objectValue, Locale locale) {
					if (objectValue!=null && objectValue instanceof BankBranchDTO) {
						return ((BankBranchDTO)objectValue).getName();
					}
					return SRSAppWebConstants.EMPTY_STRING;
				}
				
			};
			
		}
		return branchNameConverter;
	}
	
	@SuppressWarnings("unchecked")
	private void setSelected(GUIFieldPanel fieldPanel, BankingDetailsDTO bankingDetailsDTO,String atrribute)
	{
		if(fieldPanel != null && fieldPanel.getComponent() instanceof HelperPanel) {
			HelperPanel helperPanel = (HelperPanel)fieldPanel.getComponent();
			if(helperPanel.getEnclosedObject() instanceof DropDownChoice){
			DropDownChoice<?> downChoice = (DropDownChoice<?>) helperPanel.getEnclosedObject();
			downChoice.setModel(new PropertyModel(bankingDetailsDTO, atrribute));
			}
		}
	}
	
	private String getLabelForMandatory(String str) {
		StringBuilder builder = new StringBuilder(str);
		if((EditStateType.MODIFY == getEditState() || EditStateType.ADD == getEditState()) 
				&& str.indexOf("*") == -1 )
			return builder.append("*").toString();
		else
			return str;
			
	}

	/**
	 * Override the modify access behaviour.  Can only modify own banking details.
	 */
	public boolean hasModifyAccess(boolean originalAccess) {
		partyOID = pageModel.getPartyDTO().getOid();
		partyIntermed = getPartyMaintenanceController().isPartyAnIntermediary(partyOID);
		
		List<RequestKindType> unAuthRequests = getOutStandingRequestKinds();			
		//check for existing requests FIRST as other panels use variables set here
		for (RequestKindType kind : unAuthRequests) {
			if(kind == RequestKindType.MaintainPaymentDetails)
				existingPayDetRequest = true;
		}
		
		if (logger.isDebugEnabled())
			logger.debug("Calling hasModifyAccess with original access = " + originalAccess);
		
		if (originalAccess 
				&& ((pageModel.hasAgreementsWithPaysToRoles() && pageModel.getSelectedAgreementNr() == null)
					|| (pageModel.getSelectedAgreementNr() != null && pageModel.isSelectedAgreementHasPaysTo())) ) {
			logger.info("Modify access denied for banking details, selected agreement nr "+pageModel.getSelectedAgreementNr());
			return false;
		}
		
		if(!partyIntermed && getEditState().equals(EditStateType.VIEW) 
				&& pageModel != null && pageModel.getCurrentTabClass() != null 
				&& className.equalsIgnoreCase(pageModel.getCurrentTabClass().toString())){
			return false;
		}
		
		if(agmtStatus != null && agmtStatus.equalsIgnoreCase("InProgress") && getEditState().equals(EditStateType.VIEW) && className.equalsIgnoreCase(pageModel.getCurrentTabClass().toString())){
			 showErrorMsg = false;
			 return false;
		 }
		
		if(existingPayDetRequest && getEditState().equals(EditStateType.VIEW) && className.equalsIgnoreCase(pageModel.getCurrentTabClass().toString())){
			return false;
		}
		
		return originalAccess;
	}
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
			}
		}
		return guiController;
	}
}
